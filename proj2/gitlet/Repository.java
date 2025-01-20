package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

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
        String fullPath = CWD.getAbsolutePath() + File.separator + fileName;
        File file = new File(fullPath);
        if (!file.exists()){
            errorMessage("File does not exist.");
            System.exit(0);
        }
        Index.stageForAddition(fullPath);

    }

    /** TODO : commit function to commit  files in index area*/

    public static void commit (){

    }


    /** TODO : rm  : look at document */

    public static void rm (String fileName) {
//       String fullPath = CWD.getAbsolutePath() + File.separator + fileName;
//       Index.stageForRemoval(fullPath);

    }

    /** TODO : log to get information about all commits*/

    public static void log (){

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

    public static void checkout (){

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
   }









    /* TODO: fill in the rest of this class. */








}
