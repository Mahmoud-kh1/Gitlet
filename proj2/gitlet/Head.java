package gitlet;

import java.io.File;

public final class Head {

    /**
     * get sha1 for the current commit which in Head file
     * @return sha1 for the current commit
     */
    public static String getHeadSha1() {
        if (Repository.headSha1 == ""){
            Repository.headSha1 = Utils.readContentsAsString(Repository.HEAD_FILE);
        }
        return Repository.headSha1;
    }


    /**
     * set the Head sha1 to a given sha1
     * @param sha1 sha1 for the commit will be the in Head
     */
    public static void setHead(String sha1) {
        Utils.writeContents(Repository.HEAD_FILE, sha1);
        Repository.headSha1 = sha1;
    }

}
