package me.vijayjaybhay.galleryview.cache;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

/**
 * Created by Jaybhay Vijay on 10/24/2015.
 */
public class MemoryImageCache {
    /**
     * Get max available VM memory, exceeding this amount will throw an
     * OutOfMemory exception. Stored in kilobytes as LruCache takes an
     * int in its constructor.
     */
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    /**
     * Use 1/8th of the available memory for this memory cache
     */
    final int cacheSize = maxMemory / 8;

    private LruCache<String, Bitmap> mMemoryCache;

    private static  MemoryImageCache mMemoryImageCache;
    private Context mContext;
    private MemoryImageCache(Context context){
        mContext=context;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return byteSizeOf(bitmap)/ 1024;
            }
        };
    }

    public static MemoryImageCache  getInstance(Context context){
        if(mMemoryImageCache==null) {
            mMemoryImageCache=new MemoryImageCache(context);
        }
        return mMemoryImageCache;
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected int byteSizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return data.getByteCount();
        } else {
            return data.getAllocationByteCount();
        }
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

}
