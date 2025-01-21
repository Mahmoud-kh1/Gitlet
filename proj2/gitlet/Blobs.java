package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Blobs {

  public static void create(File blob, String sha1) throws IOException {
      File folderToBolb = new File(Repository.BLOBS_DIR, sha1);
      if (folderToBolb.exists()){
          return;
      }
      folderToBolb.mkdir();
      File file = new File(folderToBolb, blob.getName());
      Files.copy(blob.toPath(), file.toPath());
  }
}
