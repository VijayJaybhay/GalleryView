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
    public static String getDrawablePath(){
        return Uri.parse("android.resource://me.vijayjaybhay.galleryviewdemo/drawable").toString();
    }

    public static List<String> getDrawablePaths(){
        String path=getDrawablePath();
        File file=new File(path);
        List<File> paths= Arrays.asList(file.listFiles());
        List<String> res=new ArrayList<>();
        for (File f:paths){
            res.add(f.getAbsolutePath());
        }
        return res;
    }
}
