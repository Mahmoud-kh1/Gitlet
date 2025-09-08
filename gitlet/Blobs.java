package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Blobs {

    /**
     * create a blob with name sha1 and inside it the content of the tracked file
     * @param blob the file we will create blob for
     * @param sha1 sha1 for this file
     */
  public static void create(File blob, String sha1)  {
      // check if this content is already exist or not
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
