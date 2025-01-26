package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gitlet.Main.errorMessage;

public  final class Index {


    /**
     * stage the file with given path to stage for addtion if it will
     * @param fullPath full path of the file will be staged for addition
     */
    public static void stageForAddition(String fullPath) {
           // get hash of the path
           String hashedPath = Utils.hashPath(fullPath);
           // we remove the file from stage for removal if it exist
           removeFromStageForRemoval(hashedPath);
           // also remove it from additoin if it exist
           removeFromStageForAddition(hashedPath);

           Commit currCommit = Commit.getCurrentCommit();
           Map<String,String>trackedFilesForCurrentCommit = currCommit.getTrackedFiles();
           File currFile = new File(fullPath);
           // we add the file if not tracked in the current commit or it's different (sha1 is different from it's blob in the current commit)
           if(!trackedFilesForCurrentCommit.containsKey(fullPath) || !trackedFilesForCurrentCommit.get(fullPath).equals(Utils.getShaForFile(currFile))){
               addThisFile(hashedPath, currFile, fullPath);
           }

       }


    /**
     * stage the file for removal with the given path
     * @param fullPath path of the file will be staged for removal
     */
     public static void stageForRemoval(String fullPath)  {
           String hashedPath = Utils.hashPath(fullPath);
           // boolean to mark if the file in stagged for addition and also the function delete it if exist
           boolean inAdd = removeFromStageForAddition(hashedPath);
           Commit commit = Commit.getCurrentCommit();
           Map<String,String>trackedFilesForCurrentCommit = commit.getTrackedFiles();
           // if the file not tracked in the current and not in staged for addtion no reason for delete
           if (!inAdd && !trackedFilesForCurrentCommit.containsKey(fullPath)) {
               errorMessage("No reason to remove the file.");
               System.exit(0);
           }
           File currFile = new File(fullPath);

           // we don't remove the file unless it was tracked in the current commit   and also it exist
           if (currFile.exists() && trackedFilesForCurrentCommit.containsKey(fullPath)) {
               currFile.delete();
           }
         addToRemove(hashedPath, fullPath);

     }


    /**
     * create a folder in Staged For removal  with name the hash path and inside it two files
     * one the file itself and one for the path
     * @param hashedPath hashed value for the path of the file
     * @param fullPath the path of the file
     */
     public  static void addToRemove(String hashedPath, String fullPath)  {
           File folder = new File (Repository.STAGED_RM, hashedPath);
           folder.mkdir();
           File pathh = new File(folder, "path");
           try {
               pathh.createNewFile();
           }
           catch (IOException e) {
               e.printStackTrace();
           }
           Utils.writeContents(pathh, fullPath);
     }


      /** create a folder in Staged For addition   with name the hash path and inside it two files
      * one the file itself and one for the path
     * @param hashedPath hashed value for the path of the file
      *@param fullPath the path of the file
     */
      public static void addThisFile(String hashedPath, File currFile, String fullPath)  {
           File folder  = new File(Repository.STAGED_ADD, hashedPath);
           folder.mkdir();
           File path = new File(folder, "path");
           try {
               path.createNewFile();
           }
           catch (IOException e) {
               e.printStackTrace();
           }
           Utils.writeContents(path, fullPath);
           File newFile = new File(folder, currFile.getName());
           if (newFile.exists()) {
               newFile.delete();
           }
           try {
               Files.copy(currFile.toPath(), newFile.toPath());
           }
           catch (IOException e) {
               e.printStackTrace();
           }
      }



    /**
     * remove the file and it's folder from stage form removal it exist
     * @param hasedPath the name of the folder contain two file one is the path and on the file itself
     * @return ture if it remove this file and false if it's not exist
     *
     *
     */
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


    /**
     * remove the file and it's folder from stage form addition it exist
     * @param hasedPath the name of the folder contain two file one is the path and on the file itself
     * @return ture if it remove this file and false if it's not exist
     */
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

    /**
     * get the paths in the in stage area for removal
     * @return List contain this Paths
     */
    public  static List<String> getRemovedPaths(){
           List<String> rFiles = Utils.plainFilenamesIn(Repository.STAGED_RM);
           List<String> removedPaths = new ArrayList<>();
           for (String filename : rFiles){
               File fold = new  File(Repository.STAGED_RM, filename);
               File fiile = new File(fold, "path");
                   String p = Utils.readContentsAsString(fiile);
                   removedPaths.add(p);

           }
           return removedPaths;
    }

    /**
     * handle added file add them to our map and create a new blob for them
     * @param trackedFiles map contain the paths of tracked file and sha1 for them
     */
    public static void updateAddedFiles(Map<String, String> trackedFiles)  {
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


    public static List<String> getStaggedFilesNames(){
        List<String>addFolder = Utils.plainFilenamesIn(Repository.STAGED_ADD);
        List<String>addedFiles = new ArrayList<String>();
        for(String fo : addFolder){
            File file = new File(Repository.STAGED_ADD, fo);
            List<String> files = Utils.plainFilenamesIn(file);
            for(String filename : files){
                if (!filename.equals("path")) {
                    addedFiles.add(filename);
                }
            }
        }
        addedFiles.sort(null);
        return addedFiles;
    }



    public static List<String> getRemovedFilesNames(){
        List<String>removePaths = getRemovedPaths();
        List<String> removedFilesNames = new ArrayList<String>();
        for(String path : removePaths){
            File file = new File(path);
            removedFilesNames.add(file.getName());
        }

        return removedFilesNames;
    }







    /**
     * clear stagged Area
     */
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

    public static boolean isStaggedForAddition(File curFile){
        String path = curFile.getAbsolutePath();
        String hashPath = Utils.hashPath(path);
        File fold = new File(Repository.STAGED_ADD, hashPath);
        if (fold.exists()) {
            return true;
        }
        else return false;
    }
    public static boolean isStaggedForAddAndDifferFromCWD(File curFile){
        String path = curFile.getAbsolutePath();
        String hashPath = Utils.hashPath(path);
        File fold = new File(Repository.STAGED_ADD, hashPath);
        List<String> files = Utils.plainFilenamesIn(fold);
        File stageFile = null;
        for(String filename : files){
            if (filename.equals("path")) {
                continue;
            }
            stageFile = new File(fold, filename);
        }
        return !Utils.getShaForFile(stageFile).equals(Utils.getShaForFile(curFile));

    }

    public static List<String> getStaggedPaths(){
        List<String>addFolder = Utils.plainFilenamesIn(Repository.STAGED_ADD);
        List<String>addedPaths = new ArrayList<String>();
            for (String fo : addFolder) {
                File file = new File(Repository.STAGED_ADD, fo);
                List<String> files = Utils.plainFilenamesIn(file);
                for (String filename : files) {
                    if (filename.equals("path")) {
                        File pa = new File(file, "path");
                        String p = Utils.readContentsAsString(pa);
                        addedPaths.add(p);
                    }
                }
            }

        return addedPaths;
    }



    public static boolean isStaggedForRemoval(String path){
        String hashPath = Utils.hashPath(path);
        File fold = new File(Repository.STAGED_RM, hashPath);
        if (fold.exists()) {
            return true;
        }
        return false;
    }



}
