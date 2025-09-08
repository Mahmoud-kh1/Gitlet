package gitlet;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class HelperMethods {
    public static String formateDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static String getFileSha(File f){
        try{
            byte [] bytes = Files.readAllBytes(f.toPath());
            return Utils.sha1(bytes);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
