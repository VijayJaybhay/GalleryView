package me.vijayjaybhay.galleryview.utils;

import android.content.Context;
import android.graphics.Bitmap;
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

    public static Bitmap getBitmap(Object item,Context context){
        Bitmap bitmap=null;
        if(item instanceof Integer ){
            bitmap = BitmapUtility.decodeSampledBitmapFromResource(context.getResources(), (Integer) item, 300,400);
        }else if(item instanceof String){
            bitmap = BitmapUtility.decodeSampledBitmapFromResource((String) item, 300, 400);
        }else {
            throw new IllegalArgumentException("Invalid object passed in list");
        }
        return bitmap;
    }
}
