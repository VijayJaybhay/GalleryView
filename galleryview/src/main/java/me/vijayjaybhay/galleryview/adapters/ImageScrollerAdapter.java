package me.vijayjaybhay.galleryview.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import me.vijayjaybhay.galleryview.BuildConfig;
import me.vijayjaybhay.galleryview.R;
import me.vijayjaybhay.galleryview.cache.DiskLruCache;
import me.vijayjaybhay.galleryview.cache.Util;
import me.vijayjaybhay.galleryview.utils.BitmapUtility;
import me.vijayjaybhay.galleryview.utils.Utils;

import static android.os.Environment.isExternalStorageRemovable;

/**
 * Created by Jaybhay Vijay on 9/11/2015.
 */
public class ImageScrollerAdapter extends BaseAdapter {
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR =Environment.getExternalStorageDirectory()+"/thumbnails";
    /**
     * List of image paths
     */
    private List<Object> mImages;
    private Context  mContext;
    private Bitmap[] mBitmaps;
    private int count=0;

    public ImageScrollerAdapter(Context mContext,List<Object> mImages){
        this.mImages=mImages;
        this.mContext=mContext;
        mBitmaps=new Bitmap[mImages.size()];
        new InitDiskCacheTask().execute(getDiskCacheDir(mContext,DISK_CACHE_SUBDIR));
    }


    @Override
    public int getCount() {
        return mImages.size();
    }
    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mImages.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        final ImageView imageView;
        if(view==null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_galleryview_image, null);
        }

//        if(count>=0 && count<=mImages.size()-1){
//            mBitmaps[position]= Utils.getBitmap(mImages.get(position),mContext);
//        }
        imageView = (ImageView) view.findViewById(R.id.ivImageViewItem);
        loadBitmap(mImages.get(position),imageView);
//        imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(mBitmaps[position], 50, 50));
//        count++;
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    /**
     *
     * @param item
     * @param imageView
     */
    public void loadBitmap(Object item, ImageView imageView) {
        final String imageKey = String.valueOf(item);

        final Bitmap bitmap = getBitmapFromDiskCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap,100,100));
        } else {
            imageView.setImageResource(R.mipmap.ic_galleryview_done);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(item);
        }
    }

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

    class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
        private ImageView mImageView;
        public BitmapWorkerTask(ImageView imageView) {
            mImageView=imageView;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Object... params) {
            final String imageKey = String.valueOf(params[0]);
            // Check disk cache in background thread
            Bitmap bitmap = getBitmapFromDiskCache(imageKey);
            if (bitmap == null) { // Not found in disk cache
                // Process as normal
                bitmap=Utils.getBitmap(params[0],mContext);
            }
            // Add final bitmap to caches
            addBitmapToCache(imageKey, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(
                            ThumbnailUtils.extractThumbnail(bitmap,100,100));
                }
            });
        }
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        // Also add to disk cache
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
    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {}
            }
            if (mDiskLruCache != null) {
               return getBitmap(key);
            }
        }
        return null;
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? DISK_CACHE_SUBDIR :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    private boolean writeBitmapToFile( Bitmap bitmap, DiskLruCache.Editor editor )
            throws IOException, FileNotFoundException {
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


    public void put( String key, Bitmap data ) {

        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit( key );
            if ( editor == null ) {
                return;
            }

            if( writeBitmapToFile( data, editor ) ) {
                mDiskLruCache.flush();
                editor.commit();
                if ( mDiskLruCache.DEBUG ) {
                    Log.d("cache_test_DISK_", "image put on disk cache " + key);
                }
            } else {
                editor.abort();
                if ( mDiskLruCache.DEBUG ) {
                    Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
                }
            }
        } catch (IOException e) {
            if ( BuildConfig.DEBUG ) {
                Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
            }
            try {
                if ( editor != null ) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }

    }
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
            Log.d( "cache_test_DISK_", bitmap == null ? "" : "image read from disk " + key);
        }
        return bitmap;
    }

}
