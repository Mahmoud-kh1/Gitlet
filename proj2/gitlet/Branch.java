package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Branch {


    /**
     * just creat a branch with given name and set the it's last commit to the Head sha1
     * @param branchName a given name to create a branch with
     */
   public static void create(String branchName) {
       try {
           File newBranch = new File(Repository.BRANCHES_DIR, branchName);
           if (newBranch.exists()) {
               System.out.println("A branch with that name already exists.");
               return;
           }
           newBranch.createNewFile();
           Utils.writeContents(newBranch, Head.getHeadSha1());

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



    public static List<String> getBranchesNames(){
        List<String> branchNames;
        branchNames = Utils.plainFilenamesIn(Repository.BRANCHES_DIR);
        branchNames.sort(null);
        return branchNames;
    }


    public static void remove(String branchName) {
        File branchFile = new File(Repository.BRANCHES_DIR, branchName);

        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        if (branchName.equals(Repository.curBranchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branchFile.delete();
    }

}
