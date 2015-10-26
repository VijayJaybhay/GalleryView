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
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.vijayjaybhay.galleryview.BuildConfig;
import me.vijayjaybhay.galleryview.R;
import me.vijayjaybhay.galleryview.utils.Utils;

import static android.os.Environment.isExternalStorageRemovable;

/**
 * Created by Jaybhay Vijay on 10/24/2015.
 */
public class DiskImageCache {

    /**
     * Debug TAG
     */
    private static final String TAG="DiskImageCache";
    /**
     * Disk cache size. Default set to 10MB
     */
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    /**
     * Writable cache directory.Default set to /galleryview
     */
    private static final String DISK_CACHE_SUBDIR = Environment.getExternalStorageDirectory()+"/galleryview";

    /**
     *Bitmap compression format. Default set to JPEG.
     */
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    /**
     * Bitmap compression quality. Default set to 70.
     */
    private int mCompressQuality = 70;
    /**
     * App version of this application. Default set to 1.
     */
    private static final int APP_VERSION = 1;
    /**
     * Number of values per cache entry. Default set to  1.
     */
    private static final int VALUE_COUNT = 1;
    /**
     *
     */
    private final Object mDiskCacheLock = new Object();
    /**
     * Flag indicating disk cache started.
     */
    private boolean mDiskCacheStarting = true;
    /**
     * Disk Cache to get bitmaps that are cached to external storage
     */
    private DiskLruCache mDiskLruCache;
    /**
     *Context to access application specific resources
     */
    private Context mContext;

    /**
     * DiskImageCache to get bitmaps from disk cache
     */
    private static DiskImageCache mDiskImageCache;

    /**
     * Initializes disk cache.
     * @param context Application context
     */
    private DiskImageCache(Context context){
        this.mContext=context;
        new InitDiskCacheTask().execute(getDiskCacheDir(mContext,DISK_CACHE_SUBDIR));
    }

    /**
     *Initializes single instance of DiskImageCache.
     * @param context Application context
     * @return DiskImageCache object
     */
    public static DiskImageCache getInstance(Context context){
        if (mDiskImageCache==null){
            mDiskImageCache=new DiskImageCache(context);
        }
        return mDiskImageCache;
    }

    /**
     * Initializes DiskImageCache Asynchronously
     */
    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir,APP_VERSION,VALUE_COUNT,DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    /**
     * Adds bitmap to disk cache
     * @param key Key to identify bitmap
     * @param bitmap bitmap object to be stored corresponding to key
     */
    public void addBitmapToCache(String key, Bitmap bitmap) {
        synchronized (mDiskCacheLock) {
            try {
                if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                    put(key,bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds bitmap corresponding to key.
     * @param key Key corresponding to bitmap
     * @return Bitmap object if found in disk cache otherwise return null
     */
    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mDiskLruCache != null) {
                return getBitmap(key);
            }
        }
        return null;
    }

    /**
     * Creates a unique subdirectory of the designated app cache directory. Tries to use external
     * but if not mounted, falls back on internal storage.
     * @param context Application context
     * @param uniqueName Name of directory
     * @return File object corresponding to directory
     */

    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? DISK_CACHE_SUBDIR :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Writes bitmap to the cache directory
     * @param bitmap Bitmap object being written
     * @param editor Disk cache editor
     * @return true if successfully compressed
     * @throws IOException
     */
    private boolean writeBitmapToFile( Bitmap bitmap, DiskLruCache.Editor editor )
            throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ), Util.IO_BUFFER_SIZE );
            return bitmap.compress( mCompressFormat, mCompressQuality, out );
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    /**
     * Puts bitmap to disk cache
     * @param key Key to identify bitmap
     * @param bitmap bitmap object to be stored corresponding to key
     */
    public void put( String key, Bitmap bitmap ) {

        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit( key );
            if ( editor == null ) {
                return;
            }

            if( writeBitmapToFile( bitmap, editor ) ) {
                mDiskLruCache.flush();
                editor.commit();
                if (DiskLruCache.DEBUG) {
                    Log.d(TAG, "image put on disk cache " + key);
                }
            } else {
                editor.abort();
                if (DiskLruCache.DEBUG) {
                    Log.d( TAG, "ERROR on: image put on disk cache " + key );
                }
            }
        } catch (IOException e) {
            if ( BuildConfig.DEBUG ) {
                Log.d( TAG, "ERROR on: image put on disk cache " + key );
            }
            try {
                if ( editor != null ) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }

    }

    /**
     * Finds bitmap corresponding to key  in disk cache.
     * @param key Key corresponding to bitmap
     * @return Bitmap object if found in cache otherwise return null
     */
    public Bitmap getBitmap( String key ) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskLruCache.get( key );
            if ( snapshot == null ) {
                return null;
            }
            final InputStream in = snapshot.getInputStream( 0 );
            if ( in != null ) {
                final BufferedInputStream buffIn =
                        new BufferedInputStream( in, Util.IO_BUFFER_SIZE );
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }
        if ( BuildConfig.DEBUG ) {
            Log.d( TAG, bitmap == null ? "" : "image read from disk " + key);
        }
        return bitmap;
    }

}
