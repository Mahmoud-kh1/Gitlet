package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static gitlet.Main.errorMessage;
/** Represents a gitlet repository.
 * My architecture   for gitlet is already simple as git 
 * ----gitlet(dir)
 *      -- Blobs(dir)
 *          - files with name sha1 for it's content
 *      -- Commits(dir)
 *           - files with name sha1 for serialized commit in it
 *
 *      -- Branches(dir)
 *             - files with name of the branches and inside each one the sha1 of last commit inside this branch
 *
 *      --Staged for additoin (dir)
 *          -- contain folders with names of the hash path of the file stagged for addition
 *              -- inside this each folder we have two files one for file itself and one for path
 *
 *      -- Staged for removal (dir)
 *          -- contain folders with names of the hash path of the file stagged for removal
 *              -- inside this each folder we have two files one for file itself and one for path
 *
 *      -- Head (file)
 *           -- contain the current commit sha1
 *      --Current Branch
 *           -- contain the name of the currrent branch
 *
 *
 *  @author Mahmoud Khaled
 */
public class Repository {

    /** current working directory */
    public static File CWD = new File(System.getProperty("user.dir"));
    /** gitlet dir */
    public static File GITLET_DIR = new File(CWD, ".gitlet");
    /** commits dir */
    public static File COMMITS_DIR = new File(GITLET_DIR, "commits");
    /** Bolbs dir */
    public static File BLOBS_DIR = new File(GITLET_DIR, "BLOBS");
    /** branches dir */
    public static File BRANCHES_DIR = new File(GITLET_DIR, "branches");
    /** stagged for addition dir */
    public static File STAGED_ADD = new File(GITLET_DIR, "staggedAdd");
    /** stagged for removal  dir */
    public static File STAGED_RM = new File(GITLET_DIR, "staggedRemove");
    /** File to contain the sha1 for the current commit */
    public static File HEAD_FILE = new File(GITLET_DIR, "head");
    /** File to contain the name of the current branch */
    public static File CUR_BRANCH = new File(GITLET_DIR, "curBranch");
    /** Files to contain the sha1 for the HEAD and name of the CurBranch FOR  LAZY LOADING */
    public static String headSha1 = "";
    public static String curBranchName = "";








    /**  inialize our repo  by creating our files and folders we need and make the initial commit
     * and set the head to this commit sha1 and the branch to master
     * but first we check if already exist
     *
     * @usage :  java gitlet.Main init
     * */

    public  static void init ()  {
        // check if the repo already exist*/
         if (repoExists()){
             errorMessage("A Gitlet version-control system already exists in the current directory.");
             System.exit(0);
         }
        //  create the files and folders we need
        createRepoFilesAndFolders();

        // create the intial commit
        Commit intialCommit = new Commit();
        //  get sha1 for intial commit
        String intialCommitsha1 = intialCommit.getSha1();

        // set head to sha1 of the intial commit
         setHead(intialCommitsha1);
       //  create  branch with content is the sha1 for the intial commit  with name master
             createBranch("master");

        //we need to set the current Branch to name of master
            setCurrentBranchName("master");

    }

    /**
     * function to stage file for addition (if it will) to be commited
     * @param realativePath  of cwd for the  file will be added
     * @usage java gitlet.Main  add realtivePath
     *  */

    public static void add (String realativePath)  {
        // check if repo exist if not we stop the program and print message show the reason
        checkExistRepo();
        // get full path for this realative Path
        String fullPath = CWD.getAbsolutePath() + File.separator + realativePath;
        // check if we don't have a file with path
        File file = new File(fullPath);
        if (!file.exists()){
            errorMessage("File does not exist.");
        }

        // here we may add it or not according to some logic (I discussed it in Index Class)  as real git do
        Index.stageForAddition(fullPath);

    }

    /**
     * function to create new commit with spec message and current date
     * and the file will it track
     * @param message
     * @usage java gitlet.Main commit message
     */

    public static void commit (String message)  {

        checkExistRepo();

        checkChangesToCommit();
        // get the Head sha1
        String currentCommitSha1 = Head.getHeadSha1();
        Commit currentCommit;

        // get the current commit and map of it
        File commit = new File(Repository.COMMITS_DIR, currentCommitSha1);
        currentCommit = Utils.readObject(commit, Commit.class);
        Map<String,String> trackedFilesForNewCommit = currentCommit.getTrackedFiles();

        // remove from it the files with path in Stage for removal
        removeThePathsInRemoval(trackedFilesForNewCommit);
        //add blobls that in stage for addition and map them in the trakced Fils
        handleTheAddedFils(trackedFilesForNewCommit);
        Commit newCommit = new Commit(message, trackedFilesForNewCommit);
        String newCommitSha1 = newCommit.getSha1();
        // now we set the head to sha1 of this commit
        setHead(newCommitSha1);
        // and set the current Branch sha1 to this also */
        Branch.setLastCommitInCurrentBranch(newCommitSha1);

        Index.clearIndex();
    }


    /**
     * stage the file into stageing area for removal
     * @param realativePath of cwd for the file will be stagged for removal
     * @usage java gitlet.Main rm realativePath
     */

    public static void rm (String realativePath)  {
        checkExistRepo();
       String fullPath = CWD.getAbsolutePath() + File.separator + realativePath;
       Index.stageForRemoval(fullPath);
    }

    /**
     * just show information of commits starting with the current got the parent
     * @usage  java gitlet.Main log
     * */

    public static void log ()   {
            checkExistRepo();
            Commit commit = Commit.getCurrentCommit();
            while(true){
                commit.displayCommit();
                if (commit.getParentsha().equals("")){
                    break;
                }
                File file = new File(Repository.COMMITS_DIR, commit.getParentsha());
                commit = Utils.readObject(file, Commit.class);
            }
    }

    /**
     * display all commits information
     * */

    public static void globalLog (){
        checkExistRepo();
        List<String>S = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
        for(String C : S){
            File commit = new File(Repository.COMMITS_DIR, C);
            Commit curCommit = Utils.readObject(commit, Commit.class);
            curCommit.displayCommit();
        }
    }



    /**
     *  display all sha1 for any commit have A given message
     * @param message message we will search for
     * */

    public static void find (String message)  {
          Commit.find(message);
    }

    /** TODO : status function : look at document*/

    public static void status (){
        System.out.println("=== Branches ===");
        List<String>branches = Branch.getBranchesNames();
        String curBranch = Branch.getCurBranchName();
        for(String Branch : branches){
            if(Branch.equals(curBranch)){
                System.out.println("*" + Branch);
            }
            else System.out.println(Branch);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> staggedFiles = Index.getStaggedFilesNames();
        for(String file: staggedFiles){
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        List<String> removedFiles= Index.getRemovedFilesNames();
        for(String file: removedFiles){
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> Modi = new ArrayList<>();
        List<String>untracked = new ArrayList<>();
         getModifidNotStaged(Modi, untracked);
        for(String file: Modi){
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for(String file: untracked){
            System.out.println(file);
        }
        System.out.println();




    }




    public static void getModifidNotStaged(List<String> mo, List<String> untraked ){
        Commit curCommit = Commit.getCurrentCommit();
        Map<String, String> trackedFiles = curCommit.getTrackedFiles();
       getModifid(mo, trackedFiles, untraked);
       getDeleted(mo, trackedFiles);
       mo.sort(null);


    }
    public static void getDeleted(List<String> mo, Map<String, String> trackedFiles){
        List<String>stagedFiles = Index.getStaggedPaths();
        if(!stagedFiles.isEmpty()) {
            for (String path : stagedFiles) {
                File file = new File(path);
                if (!file.exists()) {
                    String relativePath = CWD.toURI().relativize(file.toURI()).getPath();
                    mo.add(relativePath+ " " + "(deleted)");
                }
            }
        }
        for(String key: trackedFiles.keySet()){
            File file = new File(key);
            if(!Index.isStaggedForRemoval(key) && !file.exists()){
                String relativePath = CWD.toURI().relativize(file.toURI()).getPath();
                mo.add(relativePath+ " " + "(deleted)");
            }
        }
        Set<String> S = new HashSet<>(mo);
        mo = new ArrayList<>(S);
    }
    public static List<String> getModifid(List<String> mo, Map<String, String> trackedFiles, List<String> untracked){
        Stack<File>folders = new Stack<>();

        folders.push(CWD);
        while(!folders.isEmpty()){
            File fold = folders.pop();
            List<String> files = Utils.plainFilenamesIn(fold);
            for(String file : files){
                File fileOrFolder = new File(fold, file);
                if (fileOrFolder.isDirectory() ){
                    if(!file.equals(".gitlet")) {
                        folders.push(fileOrFolder);
                    }
                }
                else{
                    if(!trackedFiles.containsKey(fileOrFolder.getAbsolutePath())) {
                        String relativePath = CWD.toURI().relativize(fileOrFolder.toURI()).getPath();
                             untracked.add(relativePath);
                    }

                    if(checkModificatoin(fileOrFolder, trackedFiles)){
                        String relativePath = CWD.toURI().relativize(fileOrFolder.toURI()).getPath();
                        mo.add(relativePath+ " " + "(modified)");
                    }
                }

            }

        }
        return mo;
    }

    public static boolean checkModificatoin(File curFile, Map<String, String> trackedFiles){
          if (Index.isStaggedForAddition(curFile)){
              if(Index.isStaggedForAddAndDifferFromCWD(curFile)){
                  return true;
              }
          }
          else if (trackedFiles.containsKey(curFile.getPath())){
              if(!trackedFiles.get(curFile.getAbsolutePath()).equals(Utils.getShaForFile(curFile))){
                   return true;
              }
          }

          return false;
    }

    /**
     * checkout file from the current commit overwriting it in cwd if exist if not we create it
     * @param realativePath of cwd for the file will be checkout from current commit
     **/
    public static void checkOutFile (String realativePath) {
        checkExistRepo();
        Commit curCommit = Commit.getCurrentCommit();
        checkOut(curCommit, realativePath);
    }


    /**
     * checkout file from a given commit sha1  overwriting it in cwd if exist if not we create it
     * @param realativePath of cwd for the file will be checkout from a given  commit
     * @param commitSha1 sha1 for the commit will be the file checkout from
     **/
    public static void checkOutFileFromGivenCommit(String commitSha1, String realativePath) {
         checkExistRepo();
         Commit curCommit = Commit.getCommitBySha(commitSha1);
         checkOut(curCommit, realativePath);
    }

    /** TODO : checkout form given branch
     */
    public static void checkOutBranch(String Sha1){

    }


    /** TODO : remove branch function to delete the pointer to this branch only not to delete commits in it */

    public static void removeBranch (String branchName){
      Branch.remove(branchName);
    }

    /** TODO : reset look at document*/

    public static void reset (String commitId){
       Commit targetCommit = Commit.getCommitBySha(commitId);
        if (targetCommit == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

       Commit currentCommit = Commit.getCommitBySha(commitId);
       Map<String, String>currentTrackedFiles = currentCommit.getTrackedFiles();
       Map<String, String>tragetTrackedFiles = currentCommit.getTrackedFiles();


        for (Map.Entry<String, String> entry : tragetTrackedFiles.entrySet()) {
            String filePath = entry.getKey();
            String blobId = entry.getValue();
            File f = new File(filePath);
            byte[] contents = Utils.readContents(new File(Repository.BLOBS_DIR, blobId));
            Utils.writeContents(f, contents);
        }


        for (String fileName : currentTrackedFiles.keySet()) {
            if (!tragetTrackedFiles.containsKey(fileName)) {
                File f = new File(Repository.CWD, fileName);
                if (f.exists()) {
                    f.delete();
                }
            }
        }

        Head.setHead(commitId);
        Index.clearIndex();
    }


    public static void merge(String branchName) {
        checkExistRepo();

        // Failure: uncommitted staged changes
        if (!Utils.plainFilenamesIn(STAGED_ADD).isEmpty() || !Utils.plainFilenamesIn(STAGED_RM).isEmpty()) {
            errorMessage("You have uncommitted changes.");
        }

        // Failure: branch existence
        File branchFile = new File(BRANCHES_DIR, branchName);
        if (!branchFile.exists()) {
            errorMessage("A branch with that name does not exist.");
        }

        // Failure: self-merge
        String curBranch = Branch.getCurBranchName();
        if (branchName.equals(curBranch)) {
            errorMessage("Cannot merge a branch with itself.");
        }

        // Heads and commits
        String currentHeadSha = Head.getHeadSha1();
        String givenHeadSha = Utils.readContentsAsString(branchFile);
        Commit currentCommit = Commit.getCommitBySha(currentHeadSha);
        Commit givenCommit = Commit.getCommitBySha(givenHeadSha);

        // Find split point
        String splitSha = findSplitPoint(currentHeadSha, givenHeadSha);

        // Ancestor cases
        if (splitSha.equals(givenHeadSha)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitSha.equals(currentHeadSha)) {
            // Fast-forward: check out given into CWD and move current branch pointer
            checkoutToCommit(givenCommit);
            Head.setHead(givenHeadSha);
            Branch.setLastCommitInCurrentBranch(givenHeadSha);
            Index.clearIndex();
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        // Maps for decision logic
        Commit splitCommit = Commit.getCommitBySha(splitSha);
        Map<String, String> splitTracked = splitCommit.getTrackedFiles();
        Map<String, String> currTracked  = currentCommit.getTrackedFiles();
        Map<String, String> givenTracked = givenCommit.getTrackedFiles();

        // Untracked-file-in-the-way protection (before doing anything)
        if (existsUntrackedFileInTheWay(currTracked, splitTracked, givenTracked)) {
            errorMessage("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        // Union of paths
        Set<String> allPaths = new HashSet<>();
        allPaths.addAll(splitTracked.keySet());
        allPaths.addAll(currTracked.keySet());
        allPaths.addAll(givenTracked.keySet());

        boolean encounteredConflict = false;

        // Apply per-file rules
        for (String path : allPaths) {
            String s = splitTracked.get(path);
            String c = currTracked.get(path);
            String g = givenTracked.get(path);


            java.util.function.Consumer<String> stageBlobFromGiven = p -> {
                writeBlobToPath(g, p);
                Index.stageForAddition(p);
            };
            java.util.function.Consumer<String> stageRemoval = p -> {
                Index.stageForRemoval(p);
            };



            if (s == null) {
                if (g != null && c == null) {
                    stageBlobFromGiven.accept(path);
                } else if (g != null && c != null) {
                    if (!c.equals(g)) {
                        writeConflict(path, c, g);
                        Index.stageForAddition(path);
                        encounteredConflict = true;
                    }
                }

                continue;
            }


            boolean cUnchangedFromSplit = Objects.equals(c, s);
            boolean gUnchangedFromSplit = Objects.equals(g, s);

            if (cUnchangedFromSplit && gUnchangedFromSplit) {
                continue;
            }

            if (cUnchangedFromSplit && !gUnchangedFromSplit) {
                if (g == null) {
                    stageRemoval.accept(path);
                } else {
                    stageBlobFromGiven.accept(path);
                }
                continue;
            }

            if (!cUnchangedFromSplit && gUnchangedFromSplit) {
                continue;
            }

            if (Objects.equals(c, g)) {
                continue;
            } else {
                writeConflict(path, c, g);
                Index.stageForAddition(path);
                encounteredConflict = true;
            }
        }

        Map<String, String> nextTracked = new HashMap<>(currentCommit.getTrackedFiles());
        removeThePathsInRemoval(nextTracked);
        handleTheAddedFils(nextTracked);

        String msg = "Merged " + branchName + " into " + curBranch + ".";
        Commit mergeCommit = new Commit(msg, nextTracked, givenHeadSha);
        String mergeSha = mergeCommit.getSha1();

        Head.setHead(mergeSha);
        Branch.setLastCommitInCurrentBranch(mergeSha);

        Index.clearIndex();

        if (encounteredConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }


    private static boolean existsUntrackedFileInTheWay(Map<String, String> currTracked,
                                                       Map<String, String> splitTracked,
                                                       Map<String, String> givenTracked) {
        Set<String> all = new HashSet<>();
        all.addAll(splitTracked.keySet());
        all.addAll(currTracked.keySet());
        all.addAll(givenTracked.keySet());

        for (String path : all) {
            String s = splitTracked.get(path);
            String c = currTracked.get(path);
            String g = givenTracked.get(path);

            boolean willWrite = false;

            if (s == null) {
                if (g != null && !Objects.equals(c, g)) {
                    willWrite = true;
                }
            } else {
                boolean cUnchanged = Objects.equals(c, s);
                boolean gUnchanged = Objects.equals(g, s);
                if (cUnchanged && !gUnchanged && g != null) {
                    willWrite = true;
                } else if (!cUnchanged && !gUnchanged && !Objects.equals(c, g)) {
                    willWrite = true;
                }
            }

            if (willWrite) {
                File f = new File(path);
                boolean untracked = !currTracked.containsKey(path) && !Index.isStaggedForAddition(f);
                if (f.exists() && untracked) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void writeBlobToPath(String blobSha, String absPath) {
        if (blobSha == null) return;
        File dest = new File(absPath);
        if (dest.getParentFile() != null) {
            dest.getParentFile().mkdirs();
        }
        byte[] contents = Utils.readContents(new File(BLOBS_DIR, blobSha));
        Utils.writeContents(dest, contents);
    }

    private static void writeConflict(String absPath, String currentBlobSha, String givenBlobSha) {
        String cur = (currentBlobSha == null) ? "" :
                new String(Utils.readContents(new File(BLOBS_DIR, currentBlobSha)));
        String giv = (givenBlobSha == null) ? "" :
                new String(Utils.readContents(new File(BLOBS_DIR, givenBlobSha)));

        String merged =
                "<<<<<<< HEAD\n" +
                        cur +
                        "=======\n" +
                        giv +
                        ">>>>>>>\n";

        File dest = new File(absPath);
        if (dest.getParentFile() != null) {
            dest.getParentFile().mkdirs();
        }
        Utils.writeContents(dest, merged);
    }

    /** Fast-forward checkout: make CWD match target commit exactly (no staging). */
    private static void checkoutToCommit(Commit target) {
        for (Map.Entry<String, String> e : target.getTrackedFiles().entrySet()) {
            writeBlobToPath(e.getValue(), e.getKey());
        }
        Commit cur = Commit.getCurrentCommit();
        for (String p : cur.getTrackedFiles().keySet()) {
            if (!target.getTrackedFiles().containsKey(p)) {
                File f = new File(p);
                if (f.exists()) f.delete();
            }
        }
    }

    /** Find "latest" common ancestor using nearest-sum distance from both heads (BFS over both parents). */
    private static String findSplitPoint(String headA, String headB) {
        Map<String, Integer> distA = bfsDistances(headA);
        Map<String, Integer> distB = bfsDistances(headB);

        String best = null;
        int bestScore = Integer.MAX_VALUE;

        for (String sha : distA.keySet()) {
            if (distB.containsKey(sha)) {
                int score = distA.get(sha) + distB.get(sha);
                if (score < bestScore) {
                    bestScore = score;
                    best = sha;
                }
            }
        }

        if (best == null) best = headA;
        return best;
    }

    private static Map<String, Integer> bfsDistances(String startSha) {
        Map<String, Integer> dist = new HashMap<>();
        Deque<String> dq = new ArrayDeque<>();
        dq.add(startSha);
        dist.put(startSha, 0);

        while (!dq.isEmpty()) {
            String cur = dq.removeFirst();
            int d = dist.get(cur);
            Commit c = Commit.getCommitBySha(cur);

            String p1 = c.getParentsha();
            if (p1 != null && !p1.equals("") && !dist.containsKey(p1)) {
                dist.put(p1, d + 1);
                dq.addLast(p1);
            }
            String p2 = c.getSecondParentSha();
            if (p2 != null && !p2.equals("") && !dist.containsKey(p2)) {
                dist.put(p2, d + 1);
                dq.addLast(p2);
            }
        }
        return dist;
    }

    /**
     * just create the file and folder in our repo
     */
   private static void createRepoFilesAndFolders()  {
       GITLET_DIR.mkdir();
       COMMITS_DIR.mkdir();
       BLOBS_DIR.mkdir();
       BRANCHES_DIR.mkdir();
       STAGED_ADD.mkdir();
       STAGED_RM.mkdir();
       try {
           HEAD_FILE.createNewFile();
           CUR_BRANCH.createNewFile();
       }
       catch (IOException e){
           e.printStackTrace();
       }
   }

    /**
     * check if the repo exist or not
     * @return true if the repo exist and false otherwise
     */
    public static boolean repoExists() {
        if (GITLET_DIR.exists()) {
            return true;
        }
        return false;
    }

    /**
     * check if repo exist or not
     * @return false if it's not exist and stop program and true otherwise
     */
    public static boolean checkExistRepo(){
        if (!repoExists()){
            errorMessage("Not in an initialized Gitlet directory.");
            return false;
        }
        return true;
    }



   /** get head sha1
    * @return String contain the sha1 for the current commit
    * */
   public static String getHeadSha1() {
        return Head.getHeadSha1();
   }


   /**
    * @return get current branch name
    * */
   public  static String getCurBranchName(){
        return Branch.getCurBranchName();
   }


   /** we set head to A given sha1 for commit
    * @sha1 the sha1 will be set in Head file
    * */
   public static void setHead(String sha1){
        Head.setHead(sha1);
   }


    /**
     * create a branch file in Branches folder with given name and set it's content to the current commit
     * @param branchName
     */
   public static void createBranch(String branchName)  {
       Branch.create(branchName);
   }
   /**
    * set the current branch Name
    * @param branchName a name to change the current branch to
    * */
   public static void setCurrentBranchName(String branchName){
       Branch.setCurrentBranchName(branchName);
   }


   /**
    * display  information about a given Commit
    * @param commit to be displayed
    *  */



    /**
     * remove paths from parent tracked path if it was staged for removal
     * @param trakedFiles
     * @return Map contain the tracked file after remove the Paths will be removed
     */
    public static Map<String, String> removeThePathsInRemoval(Map<String, String>trakedFiles){
       List<String>deletedPaths = Index.getRemovedPaths();
       for (String path : deletedPaths){
           trakedFiles.remove(path);
       }
       return trakedFiles;
    }

    /**
     * handle the added file and create blobls to it if I will create
     * @param trackedFiles
     */
    public static void handleTheAddedFils(Map<String ,String> trackedFiles)  {
       Index.updateAddedFiles(trackedFiles);
    }

    /**
     * just check out if there is a change to be commited or not
     * by checking if there is something in to be add or removed
     */
   public static void checkChangesToCommit(){
       List<String>rmFolders = Utils.plainFilenamesIn(Repository.STAGED_RM);
       List<String>addFolders = Utils.plainFilenamesIn(Repository.STAGED_ADD);
       if (rmFolders.isEmpty() && addFolders.isEmpty()){
           errorMessage("No changes added to the commit.");
       }

   }


    /**
     * check out the file from a given commit overwriting it if exist other wise we create it
     * @param commit
     * @param realativePath
     */
    public static void checkOut(Commit commit , String realativePath)  {
        String fullPath = CWD.getAbsolutePath() + File.separator + realativePath;
       Map<String , String> trackedFiles = commit.getTrackedFiles();
       // check if the file tarcked by the given commit or not
       if (!trackedFiles.containsKey(fullPath)){
           errorMessage("File does not exist in that commit.");
       }
       String shaBolb = trackedFiles.get(fullPath);
       File origin = new File(fullPath);
       File blob = new File(Repository.BLOBS_DIR, shaBolb);
       // create the file if not exist in cwd
       if (!origin.exists()){
           try {
               origin.createNewFile();

           }
           catch (IOException e){
               e.printStackTrace();
           }
       }
       // copy content
       try{
           Files.copy(blob.toPath(), origin.toPath(), StandardCopyOption.REPLACE_EXISTING);
       }
       catch (IOException e){
           e.printStackTrace();
       }

    }











}
