package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Main.errorMessage;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  -commitObject
 *      sha-1
 *      Message
 *      sha-1 for it's parent
 *      the track files
 *      Date for this commit which when it was created
 *
 *  @author TODO
 */
public class Commit implements Serializable{

    private  String message;
    private  String parentsha;
    private  Date timestamp;
    /** the key is the path and the value is the sha1 this commit track for this path */
    private Map<String, String>trackedFiles;


    /** constructor to make the intial commit */
    public Commit()  {
        this.message = "initial commit";
        this.parentsha = "";
        this.timestamp = new Date(0);
        this.trackedFiles = new HashMap<>();
         saveCommit();
    }

    public Commit (String message, Map<String, String> trackedFiles)  {
        this.message = message;
        this.parentsha = Head.getHeadSha1();
        this.timestamp = new Date();
        this.trackedFiles = trackedFiles;
        this.saveCommit();
    }


    /**
     * serialize  a commit inside a commits dir inside file with name of it's sha1
     */
   public void saveCommit()  {
        String sha1 = this.getSha1();
        // here we create a file to serialize in it with name with sha in floder commits
       File commit = new File (Repository.COMMITS_DIR, sha1);
       try {
           commit.createNewFile();
       }
       catch (IOException e) {
           e.printStackTrace();
       }
       Utils.writeObject(commit, this);
   }


    /** return sha1 for this object
     * @return sha1
     * */
    public String getSha1()  {
        return Utils.getObjectSha1(this);
    }


    /**
     * get current commit
     * @return  commit
     */
    public static Commit getCurrentCommit() {
        String sha1 = Head.getHeadSha1();
        File commit = new File (Repository.COMMITS_DIR, sha1);
        Commit co = Utils.readObject(commit, Commit.class);
        return co;
    }

    public static Commit getCommitBySha(String sha)  {
        File commit = new File (Repository.COMMITS_DIR, sha);
        if (!commit.exists()){
            errorMessage("No commit with that id exists.");
        }
        Commit co = Utils.readObject(commit, Commit.class);
        return co;
    }



    public void displayCommit(){
        System.out.println("===");
        System.out.println("commit " + this.getSha1());
        System.out.println("Date: " + HelperMethods.formateDate(this.getTimestamp()) + " -0800");
        System.out.println(this.getMessage());
        System.out.println();
    }

    public static void find(String message){
        List<String> commits = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
        boolean found = false;
        for(String C : commits){
            File file = new File(Repository.COMMITS_DIR, C);
            Commit commit = Utils.readObject(file, Commit.class);
            if (commit.getMessage().equals(message)){
                found = true;
                System.out.println(commit.getSha1());
            }
        }
        if (!found){
            errorMessage("Found no commit with that message.");
        }
    }

    public Date getTimestamp() {
        return timestamp;
    }
    public String getParentsha() {
        return parentsha;
    }
    public Map<String, String> getTrackedFiles() {
        return trackedFiles;
    }
    public String getMessage() {
        return message;
    }






    /* TODO: fill in the rest of this class. */
}
