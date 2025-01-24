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
            String sha1 = Head.getHeadSha1();
            while(true){
                System.out.println("===");
                System.out.println("commit " + sha1);
                System.out.println("Date: " + HelperMethods.formateDate(commit.getTimestamp()) + " -0800");

                System.out.println(commit.getMessage());
                System.out.println();
                if (commit.getParentsha().equals("")){
                    break;
                }
                File file = new File(Repository.COMMITS_DIR, commit.getParentsha());
                commit = Utils.readObject(file, Commit.class);
                sha1 = commit.getSha1();
            }
    }

    /** TODO : global  also look at document*/

    public static void globalLog (){

    }

    /** TODO : find function : look at document*/

    public static void find (){

    }

    /** TODO : status function : look at document*/

    public static void status (){

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


    /** TODO : branch function to create a new branch */

    public static void branch (){

    }

    /** TODO : remove branch function to delete the pointer to this branch only not to delete commits in it */

    public static void removeBranch (){

    }

    /** TODO : reset look at document*/

    public static void reset (){

    }

    /** TODO : merge look at document*/

    public static void merge (){

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


   /** just for test intial commits */
   public static void readHeadCommit(){
       File file = new File(Repository.COMMITS_DIR, getHeadSha1());
       Commit commit = Utils.readObject(file, Commit.class);
       System.out.println(commit.getSha1());
       System.out.println(commit.getTimestamp());
       System.out.println(commit.getParentsha());
       Map<String ,String> trackedFilesForNewCommit = commit.getTrackedFiles();
       trackedFilesForNewCommit.forEach((key, value) -> {
           System.out.println("Key: " + key + ", Value: " + value);
       });
   }



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
