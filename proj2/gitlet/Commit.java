package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;

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
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The message of this Commit. */
    private  String message;
    private  String parentsha;
    private  Date timestamp;
    private Map<String, String>trackedFiles;
    private Map<String, String>deletedFiles;


    /** constructor to make the intial commit */
    public Commit() throws IOException {
        this.message = "initial commit";
        this.parentsha = "";
        this.timestamp = new Date(0);
        this.trackedFiles = new HashMap<>();
        this.deletedFiles = new HashMap<>();
         saveCommit();
    }










   public void saveCommit() throws IOException {
        String sha1 = this.getSha1();
        // here we create a file to serialize in it with name with sha in floder commits
       File commit = new File (Repository.COMMITS_DIR, sha1);
       commit.createNewFile();
       Utils.writeObject(commit, this);
   }



    public String getSha1(){
        return Utils.getObjectSha1(this);
    }


    public static Commit getCurrentCommit() {
        String sha1 = Head.getHeadSha1();
        File commit = new File (Repository.COMMITS_DIR, sha1);
        Commit co = Utils.readObject(commit, Commit.class);
        return co;
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







    /* TODO: fill in the rest of this class. */
}
