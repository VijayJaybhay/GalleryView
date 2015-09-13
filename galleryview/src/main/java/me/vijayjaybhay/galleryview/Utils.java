package me.vijayjaybhay.galleryview;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jaybhay Vijay on 9/11/2015.
 */
public class Utils {
    public static List<Object> getFiles(String folderPath){
        File file=new File(folderPath);
        List<File> paths= Arrays.asList(file.listFiles());
        List<Object> res=new ArrayList<>();
        for (File f:paths){
            res.add(f.getAbsolutePath());
        }
        return res;
    }
}
