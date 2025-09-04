package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Main.errorMessage;

/** Represents a gitlet commit object. */
public class Commit implements Serializable {

    private String message;
    /** First parent (the current branch head at commit-time). */
    private String parentsha;
    /** Second parent (present only for merge commits; empty otherwise). */
    private String secondParentSha;
    private Date timestamp;
    /** key: absolute path, value: blob sha1 */
    private Map<String, String> trackedFiles;

    /** constructor to make the initial commit */
    public Commit() {
        this.message = "initial commit";
        this.parentsha = "";
        this.secondParentSha = "";
        this.timestamp = new Date(0);
        this.trackedFiles = new HashMap<>();
        saveCommit();
    }

    /** normal commit (single parent = HEAD) */
    public Commit(String message, Map<String, String> trackedFiles) {
        this.message = message;
        this.parentsha = Head.getHeadSha1();
        this.secondParentSha = "";
        this.timestamp = new Date();
        this.trackedFiles = trackedFiles;
        this.saveCommit();
    }

    /** merge commit: first parent = HEAD, second parent = given branch head */
    public Commit(String message, Map<String, String> trackedFiles, String secondParentSha) {
        this.message = message;
        this.parentsha = Head.getHeadSha1();
        this.secondParentSha = (secondParentSha == null) ? "" : secondParentSha;
        this.timestamp = new Date();
        this.trackedFiles = trackedFiles;
        this.saveCommit();
    }

    /** serialize this commit into .gitlet/commits/<sha> */
    public void saveCommit() {
        String sha1 = this.getSha1();
        File commit = new File(Repository.COMMITS_DIR, sha1);
        try {
            commit.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(commit, this);
    }

    /** return sha1 for this object */
    public String getSha1() {
        return Utils.getObjectSha1(this);
    }

    /** get current commit (HEAD) */
    public static Commit getCurrentCommit() {
        String sha1 = Head.getHeadSha1();
        File commit = new File(Repository.COMMITS_DIR, sha1);
        return Utils.readObject(commit, Commit.class);
    }

    public static Commit getCommitBySha(String sha) {
        File commit = new File(Repository.COMMITS_DIR, sha);
        if (!commit.exists()) {
            errorMessage("No commit with that id exists.");
        }
        return Utils.readObject(commit, Commit.class);
    }

    public void displayCommit() {
        System.out.println("===");
        System.out.println("commit " + this.getSha1());
        if (secondParentSha != null && !secondParentSha.equals("") && parentsha != null && !parentsha.equals("")) {
            // show the abbreviated parents like real git
            String a = parentsha.length() >= 7 ? parentsha.substring(0, 7) : parentsha;
            String b = secondParentSha.length() >= 7 ? secondParentSha.substring(0, 7) : secondParentSha;
            System.out.println("Merge: " + a + " " + b);
        }
        System.out.println("Date: " + HelperMethods.formateDate(this.getTimestamp()) + " -0800");
        System.out.println(this.getMessage());
        System.out.println();
    }

    public static void find(String message){
        List<String> commits = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
        boolean found = false;
        for (String C : commits) {
            File file = new File(Repository.COMMITS_DIR, C);
            Commit commit = Utils.readObject(file, Commit.class);
            if (commit.getMessage().equals(message)) {
                found = true;
                System.out.println(commit.getSha1());
            }
        }
        if (!found) {
            errorMessage("Found no commit with that message.");
        }
    }

    public Date getTimestamp() { return timestamp; }
    public String getParentsha() { return parentsha; }
    public String getSecondParentSha() { return secondParentSha; }
    public Map<String, String> getTrackedFiles() { return trackedFiles; }
    public String getMessage() { return message; }
}
