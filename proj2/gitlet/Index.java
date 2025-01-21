package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static gitlet.Main.errorMessage;

public  final class Index {

       public static void stageForAddition(String fullPath) throws IOException {
           String hashedPath = Utils.hashPath(fullPath);
           removeFromStageForRemoval(hashedPath);
           removeFromStageForAddition(hashedPath);
           /** when I add if it's not tracked in current commit */
           Commit currCommit = Commit.getCurrentCommit();
           Map<String,String>trackedFilesForCurrentCommit = currCommit.getTrackedFiles();
           File currFile = new File(fullPath);
           /** we add if it's not tracked and if it's not the same sha1 */
           if(!trackedFilesForCurrentCommit.containsKey(fullPath) || !trackedFilesForCurrentCommit.get(fullPath).equals(Utils.getShaForFile(currFile))){
               addThisFile(hashedPath, currFile, fullPath);
           }

       }



     public static void stageForRemoval(String fullPath) throws IOException {
           String hashedPath = Utils.hashPath(fullPath);
           boolean inAdd = removeFromStageForAddition(hashedPath);
           Commit commit = Commit.getCurrentCommit();
           Map<String,String>trackedFilesForCurrentCommit = commit.getTrackedFiles();
           if (!inAdd && !trackedFilesForCurrentCommit.containsKey(fullPath)) {
               errorMessage("No reason to remove the file.");
               System.exit(0);
           }
           File currFile = new File(fullPath);

           /** we don't remove the file unless it was tracked in the current commit */
           if (currFile.exists() && trackedFilesForCurrentCommit.containsKey(fullPath)) {
               currFile.delete();
           }
         addToRemove(hashedPath, fullPath);

     }

     public  static void addToRemove(String hashedPath, String fullPath) throws IOException {
           File folder = new File (Repository.STAGED_RM, hashedPath);
           folder.mkdir();
           File pathh = new File(folder, "path");
           pathh.createNewFile();
           Utils.writeContents(pathh, fullPath);
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



       public static boolean removeFromStageForRemoval(String hasedPath){
           File file = new File(Repository.STAGED_RM, hasedPath);
           if (!file.exists()){
               return false;
           }
           List<String> files = Utils.plainFilenamesIn(file);
           for (String filename : files){
               File toDelete = new File(file, filename);
               toDelete.delete();
           }
           file.delete();
           return true;
       }



    public static boolean removeFromStageForAddition(String hasedPath){
        File file = new File(Repository.STAGED_ADD, hasedPath);
        if (!file.exists()){
            return false;
        }
        List<String> files = Utils.plainFilenamesIn(file);
        for (String filename : files){
            File toDelete = new File(file, filename);
                toDelete.delete();
        }
        file.delete();
        return true;
    }

    public  static List<String> getRemovedPaths(){
           List<String> rFiles = Utils.plainFilenamesIn(Repository.STAGED_RM);
           for (String filename : rFiles){
               File file = new File(filename, "path");
               String p = Utils.readContentsAsString(file);
               rFiles.add(p);
           }
           return rFiles;
    }

    public static void updateAddedFiles(Map<String, String> trackedFiles) throws IOException {
           List<String> folders = Utils.plainFilenamesIn(Repository.STAGED_ADD);
           if (!folders.isEmpty()) {
               for (String sha1Path : folders) {
                   File sha1ForPath = new File(Repository.STAGED_ADD, sha1Path);
                   List<String> addedFiles = Utils.plainFilenamesIn(sha1ForPath);
                   String p = "";
                   File fileToAdd = null;
                   for (String nameOfFileInPathSha1 : addedFiles) {

                       if (nameOfFileInPathSha1.equals("path")) {
                           File file = new File(sha1ForPath, nameOfFileInPathSha1);
                           p = Utils.readContentsAsString(file);
                       } else {
                           fileToAdd = new File(sha1ForPath, nameOfFileInPathSha1);
                       }
                   }
                   String newFileSha1 = Utils.getShaForFile(fileToAdd);
                   trackedFiles.put(p, newFileSha1);
                   Blobs.create(fileToAdd, newFileSha1);
               }
           }
    }


    public static void clearIndex(){
           List<String> rm = Utils.plainFilenamesIn(Repository.STAGED_RM);
           List<String> ad = Utils.plainFilenamesIn(Repository.STAGED_ADD);
           for (String remove : rm){
               removeFromStageForRemoval(remove);
           }
           for (String add : ad){
               removeFromStageForAddition(add);
           }
    }




}
