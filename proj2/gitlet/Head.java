package gitlet;

import java.io.File;

public final class Head {


    public static String getHeadSha1() {
        if (Repository.headSha1 == ""){
            Repository.headSha1 = Utils.readContentsAsString(Repository.HEAD_FILE);
        }
        return Repository.headSha1;
    }


    public static void setHead(String sha1) {
        Utils.writeContents(Repository.HEAD_FILE, sha1);
        Repository.headSha1 = sha1;
    }

}
