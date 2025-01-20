package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  final class Index {

       public static void stageForAddition(String fullPath) throws IOException {
           String hashedPath = Utils.hashPath(fullPath);
           removeFromStageForRemoval(hashedPath);
           removeFromStageForAddition(hashedPath);
           /** when I add if it's not tracked in current commit */
           Commit currCommit = Commit.getCommitBySha1();
           Map<String,String>trackedFilesForCurrentCommit = currCommit.getTrackedFiles();
           File currFile = new File(fullPath);
           /** we add if it's not tracked and if it's not the same sha1 */
           if(!trackedFilesForCurrentCommit.containsKey(fullPath) || trackedFilesForCurrentCommit.get(fullPath).equals(Utils.getShaForFile(currFile))){
               addThisFile(hashedPath, currFile, fullPath);
           }

       }



     public static void stageForRemoval(String fullPath) throws IOException {
           String hashedPath = Utils.hashPath(fullPath);
//           removeFrom
     }



      public static void addThisFile(String hashedPath, File currFile, String fullPath) throws IOException {
           File folder  = new File(Repository.STAGED_ADD, hashedPath);
           folder.mkdir();
           File path = new File(folder, "path");
           path.createNewFile();
           Utils.writeContents(path, fullPath);
           File newFile = new File(folder, currFile.getName());
           if (newFile.exists()) {
               newFile.delete();
           }
          Files.copy(currFile.toPath(), newFile.toPath());
      }



       public static void removeFromStageForRemoval(String hasedPath){
           File file = new File(Repository.STAGED_RM, hasedPath);
           if (!file.exists()){
               return;
           }
           List<String> files = Utils.plainFilenamesIn(file);
           for (String filename : files){
               File toDelete = new File(file, filename);
               toDelete.delete();
           }
           file.delete();
       }



    public static void removeFromStageForAddition(String hasedPath){
        File file = new File(Repository.STAGED_ADD, hasedPath);
        if (!file.exists()){
            return;
        }
        List<String> files = Utils.plainFilenamesIn(file);
        for (String filename : files){
            File toDelete = new File(file, filename);
                toDelete.delete();
        }
        file.delete();
    }



}
