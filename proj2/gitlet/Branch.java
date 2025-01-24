package gitlet;

import java.io.File;
import java.io.IOException;

public class Branch {


    /**
     * just creat a branch with given name and set the it's last commit to the Head sha1
     * @param branchName a given name to create a branch with
     */
   public static void create(String branchName) {
       try {
           File newFile = new File(Repository.BRANCHES_DIR, branchName);
           newFile.createNewFile();
           Utils.writeContents(newFile, Head.getHeadSha1());
       }
       catch (IOException e) {
           e.printStackTrace();
       }
   }


    /**
     * just get the current branch Name with obtaingin lazy loading concept
     * @return the current branch Name
     */
    public  static String getCurBranchName(){
        if (Repository.curBranchName == ""){
            Repository.curBranchName = Utils.readContentsAsString(Repository.CUR_BRANCH);
        }
        return Repository.curBranchName;
    }

    /**
     * set teh crrent branch Name to a given Name
     * @param branchName a Name to set the current branch with
     */
    public static void setCurrentBranchName(String branchName){
        Utils.writeContents(Repository.CUR_BRANCH, branchName);
    }


    /**
     * set the last commit int the current Branch to a given sha1 for commit
     * @param sha1 a sha1 to set the last commit in the current branch to
     */
    public  static void setLastCommitInCurrentBranch(String sha1){
       String name = Branch.getCurBranchName();
       File branchFile = new File(Repository.BRANCHES_DIR, name);
       Utils.writeContents(branchFile, sha1);
    }
}
