package me.vijayjaybhay.galleryview.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jaybhay Vijay on 9/11/2015.
 */
public class Utils {
    /**
     *  Retrieve all files in specified folder
     * @param folderPath Directory path
     * @return List of file paths
     */
    public static List<Object> getFiles(String folderPath){
        File file=new File(folderPath);
        List<File> paths= Arrays.asList(file.listFiles());
        List<Object> res=new ArrayList<>();
        for (File f:paths){
            res.add(f.getAbsolutePath());
        }
        return res;
    }

    /**
     * Decodes item to Bitmap
     * @param item Image resource or image file path
     * @param context Context
     * @return Bitmap for item
     */
    public static Bitmap getBitmap(Object item,Context context){
        Bitmap bitmap=null;
        if(item instanceof Integer ){
            bitmap = BitmapUtility.decodeSampledBitmapFromPath(context.getResources(), (Integer) item, 300, 400);
        }else if(item instanceof String){
            bitmap = BitmapUtility.decodeSampledBitmapFromPath((String) item, 300, 400);
        }else {
            throw new IllegalArgumentException("Invalid object passed in list");
        }
        return bitmap;
    }
}
