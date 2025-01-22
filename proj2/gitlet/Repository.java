package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static gitlet.Main.errorMessage;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *   first it will have all functions to do all  commands
 *   it will have repo dir called .gitlet
 *   it will have another useful things like   objects   commits  logs  branches(inside it we will have the heads of the latest commit in each branch)
 *   head file(to tell what the current commit)
 *
 *  @author Mahmoud Khaled
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    public static File CWD = new File(System.getProperty("user.dir"));
    /** gitlet repo */
    public static File GITLET_DIR = new File(CWD, ".gitlet");
    /** commits folder */
    public static File COMMITS_DIR = new File(GITLET_DIR, "commits");
    /** Bolbs folder */
    public static File BLOBS_DIR = new File(GITLET_DIR, "BLOBS");
    /** branches folder */
    public static File BRANCHES_DIR = new File(GITLET_DIR, "branches");
    /** stagged for addition folder */
    public static File STAGED_ADD = new File(GITLET_DIR, "staggedAdd");
    /** stagged for removal  folder */
    public static File STAGED_RM = new File(GITLET_DIR, "staggedRemove");
    /** File to contain the sha1 for the current commit */
    public static File HEAD_FILE = new File(GITLET_DIR, "head");
    /** File to contain the name of the current branch */
    public static File CUR_BRANCH = new File(GITLET_DIR, "curBranch");
    /** Files to contain the sha1 for the HEAD and name of the CurBranch FOR  LAZY LOADING */
    public static String headSha1 = "";
    public static String curBranchName = "";








    /**  init function to initialize repo */
    public  static void init () throws IOException {
        /** first we need to check if the repo already exist*/
         if (repoExists()){
             errorMessage("A Gitlet version-control system already exists in the current directory.");
             System.exit(0);
         }
        /** we need to create the files and folders we need */
        createRepoFilesAndFolders();

        /** create the intial commit */
        Commit intialCommit = new Commit();
        /**  here we should have the sha1 for intial commit */
        String intialCommitsha1 = intialCommit.getSha1();

        /** set head to sha1 of the intial commit */
         setHead(intialCommitsha1);
        /**  create  branch with content is the sha1 for the intial commit  with name master*/
             createBranch("master");

        /** we need to set the current Branch to name of master*/
            setCurrentBranchName("master");

    }

    /** TODO : add function to stage files */

    public static void add (String fileName) throws IOException {
        checkExistRepo();
        String fullPath = CWD.getAbsolutePath() + File.separator + fileName;
        File file = new File(fullPath);
        if (!file.exists()){
            errorMessage("File does not exist.");
        }
        Index.stageForAddition(fullPath);

    }

    /** TODO : commit function to commit  files in index area*/

    public static void commit (String message) throws IOException {

        checkExistRepo();

        checkChangesToCommit();
        /** get the Head sha1*/
        String currentCommitSha1 = Head.getHeadSha1();
        Commit currentCommit;

        /** get the current commit and map of it **/
        File commit = new File(Repository.COMMITS_DIR, currentCommitSha1);
        currentCommit = Utils.readObject(commit, Commit.class);
        Map<String,String> trackedFilesForNewCommit = currentCommit.getTrackedFiles();
        /** now we should remove from it the files with path in Stage for removal*/
        removeThePathsInRemoval(trackedFilesForNewCommit);
        /** now we should add blobls that in stage for addition and map them in the trakced Fils */
        handleTheAddedFils(trackedFilesForNewCommit);
        Commit newCommit = new Commit(message, trackedFilesForNewCommit);
        String newCommitSha1 = newCommit.getSha1();
        /** now we set the head to sha1 of this commit */
        setHead(newCommitSha1);
        /** and set the current Branch sha1 to this also */
        Branch.setLastCommitInCurrentBranch(newCommitSha1);
        /** clear index */
        Index.clearIndex();
    }


    /** TODO : rm  : look at document */

    public static void rm (String fileName) throws IOException {
        checkExistRepo();
       String fullPath = CWD.getAbsolutePath() + File.separator + fileName;
       Index.stageForRemoval(fullPath);
    }

    /** TODO : log to get information about all commits*/

    public static void log () throws IOException {
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

    /** TODO : checkout  function  also : look at document*/

    public static void checkOutFile (String fileName) throws IOException {
        checkExistRepo();
        Commit curCommit = Commit.getCurrentCommit();
        checkOut(curCommit, fileName);
    }


    public static void checkOutFileFromGivenCommit(String commitSha1, String fileName) throws IOException {
         checkExistRepo();
         Commit curCommit = Commit.getCommitBySha(commitSha1);
         checkOut(curCommit, fileName);
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



   private static void createRepoFilesAndFolders() throws IOException {
       GITLET_DIR.mkdir();
       COMMITS_DIR.mkdir();
       BLOBS_DIR.mkdir();
       BRANCHES_DIR.mkdir();
       STAGED_ADD.mkdir();
       STAGED_RM.mkdir();
       HEAD_FILE.createNewFile();
       CUR_BRANCH.createNewFile();
   }


    public static boolean repoExists() {
        if (GITLET_DIR.exists()) {
            return true;
        }
        return false;
    }
    public static boolean checkExistRepo(){
        if (!repoExists()){
            errorMessage("Not in an initialized Gitlet directory.");
            return false;
        }
        return true;
    }



   /** get head sha1 */
   public static String getHeadSha1() {
        return Head.getHeadSha1();
   }
   /** get name of current branch */
   public  static String getCurBranchName(){
        return Branch.getCurBranchName();
   }
   /** setHead to new sha1*/
   public static void setHead(String sha1){
        Head.setHead(sha1);
   }

   /** create branch with name*/
   public static void createBranch(String branchName) throws IOException {
       Branch.create(branchName);
   }
   /** set the current branch name */
   public static void setCurrentBranchName(String branchName){
       Branch.setCurrentBranchName(branchName);
   }
   /** just for test intial commits */
   public static void readHeadCommit() throws IOException {
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


    public static Map<String, String> removeThePathsInRemoval(Map<String, String>trakedFiles){
       List<String>deletedPaths = Index.getRemovedPaths();
       for (String path : deletedPaths){
           trakedFiles.remove(path);
       }
       return trakedFiles;
    }
    public static void handleTheAddedFils(Map<String ,String> trackedFiles) throws IOException {
       Index.updateAddedFiles(trackedFiles);
    }
   public static void checkChangesToCommit(){
       List<String>rmFolders = Utils.plainFilenamesIn(Repository.STAGED_RM);
       List<String>addFolders = Utils.plainFilenamesIn(Repository.STAGED_ADD);
       if (rmFolders.isEmpty() && addFolders.isEmpty()){
           errorMessage("No changes added to the commit.");
       }

   }


    public static void checkOut(Commit commit , String fileName) throws IOException {
        String fullPath = CWD.getAbsolutePath() + File.separator + fileName;
       Map<String , String> trackedFiles = commit.getTrackedFiles();
       if (!trackedFiles.containsKey(fullPath)){
           errorMessage("File does not exist in that commit.");
       }
       String shaBolb = trackedFiles.get(fullPath);
       File origin = new File(fullPath);
       File blob = new File(Repository.BLOBS_DIR, shaBolb);
       if (!origin.exists()){
           origin.createNewFile();
       }
        Files.copy(blob.toPath(), origin.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }






    /* TODO: fill in the rest of this class. */








}
