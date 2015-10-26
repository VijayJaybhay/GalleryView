
/*
 * Copyright 2015  Vijay Jaybhay
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package me.vijayjaybhay.galleryview.cache;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

/**
 * Created by Jaybhay Vijay on 10/24/2015.
 */
public class MemoryImageCache {
    /**
     * LruCache for implementation of memory cache mechanism
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * Memory Cache to get bitmaps that are available in RAM
     */
    private static  MemoryImageCache mMemoryImageCache;
    /**
     * Context to access application specific resources
     */
    private Context mContext;

    /**
     * Initializes memory cache
     * @param context Application context
     */
    private MemoryImageCache(Context context){
        mContext=context;
        mMemoryCache = new LruCache<String, Bitmap>(getCacheSize()) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return byteSizeOf(bitmap)/ 1024;
            }
        };
    }

    /**
     * Initializes single instance of MemoryImageCache.
     * @param context Application context
     * @return MemoryImageCache object
     */
    public static MemoryImageCache  getInstance(Context context){
        if(mMemoryImageCache==null) {
            mMemoryImageCache=new MemoryImageCache(context);
        }
        return mMemoryImageCache;
    }

    /**
     * Computes cache size
     * @return maximum memory cache size
     */
    private int getCacheSize(){
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        int memClassBytes = am.getMemoryClass() * 1024 * 1024;
        return memClassBytes / 8;
    }
    /**
     * Computes number of bytes in bitmap
     * @param data Bitmap data
     * @return  number of bytes in bitmap
     */
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


    /**
     * Add bitmap to memory cache
     * @param key Key to identify bitmap
     * @param bitmap bitmap object to be stored corresponding to key
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * Finds bitmap corresponding to key.
     * @param key Key corresponding to bitmap
     * @return Bitmap object if found in memory otherwise return null
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

}
