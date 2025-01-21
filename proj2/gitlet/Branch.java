package gitlet;

import java.io.File;
import java.io.IOException;

public class Branch {





   public static void create(String branchName) throws IOException {
       File newFile = new File(Repository.BRANCHES_DIR, branchName);
       newFile.createNewFile();
       Utils.writeContents(newFile, Head.getHeadSha1());
   }
    public  static String getCurBranchName(){
        if (Repository.curBranchName == ""){
            Repository.curBranchName = Utils.readContentsAsString(Repository.CUR_BRANCH);
        }
        return Repository.curBranchName;
    }

    public static void setCurrentBranchName(String branchName){
        Utils.writeContents(Repository.CUR_BRANCH, branchName);
    }


    public  static void setLastCommitInCurrentBranch(String sha1){
       String name = Branch.getCurBranchName();
       File branchFile = new File(Repository.BRANCHES_DIR, name);
       Utils.writeContents(branchFile, sha1);
    }
}
