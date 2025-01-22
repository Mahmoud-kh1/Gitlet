package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Blobs {

  public static void create(File blob, String sha1)  {
      File newBlob = new File(Repository.BLOBS_DIR, sha1);
      if (newBlob.exists()) {
          return;
      }
      try {
          newBlob.createNewFile();
          Files.copy(blob.toPath(), newBlob.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
      catch (IOException e) {
          e.printStackTrace();
      }
  }
}
