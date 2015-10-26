package me.vijayjaybhay.galleryview.cache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import me.vijayjaybhay.galleryview.R;
import me.vijayjaybhay.galleryview.utils.Utils;

/**
 * Created by Jaybhay Vijay on 10/24/2015.
 */
public class ImageCache {
    /**
     * Memory Cache to get bitmaps that are available in RAM
     */
    private  MemoryImageCache mMemoryImageCache;
    /**
     * Disk Cache to get bitmaps that are cached to external storage
     */
    private DiskImageCache mDiskImageCache;
    /**
     * Image Cache to get bitmaps from memory or disk cache.It abstracts use of
     * Memory cache and Disk Cache
     */
    private static ImageCache mImageCache;
    /**
     * Context to access application specific resources
     */
    private Context mContext;

    /**
     * Initializes memory cache and disk cache.
     * @param context Context of application
     */
    private ImageCache(Context context){
        mContext=context;
        mMemoryImageCache=MemoryImageCache.getInstance(mContext);
        mDiskImageCache=DiskImageCache.getInstance(mContext);
    }

    /**
     * Initializes single instance of Image Cache.
     * @param context Applicati
     * @return ImageCahe object
     */
    public static ImageCache getInstance(Context context){
        if(mImageCache==null){
            mImageCache=new ImageCache(context);
        }
        return mImageCache;
    }

    /**
     * Loads bitmap from image cache.It first check Memory Cache.If Bitmap found in Memory Cache, it will
     * return Bitmap object else it uses disk cache.
     * @param item Image resource or image file path
     * @param imageView Image View to which resultant bitmap needs to be set
     */
    public void loadBitmap(Object item, ImageView imageView) {
        final String imageKey = String.valueOf(item.hashCode());
        Bitmap bitmap = mMemoryImageCache.getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 100, 100));
        } else {
            imageView.setImageResource(R.mipmap.gv_ic_image_placeholder);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(item);
        }
    }


    /**
     * AsyncTask that caches bitmaps asynchronously and sets to corresponding image view.
     */
    public class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
        private ImageView mImageView;
        private  Handler mHandler;
        public BitmapWorkerTask(ImageView imageView) {
            mImageView=imageView;
        }
        public BitmapWorkerTask(Handler handler){
            mHandler=handler;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Object... params) {
            final String imageKey = String.valueOf(params[0].hashCode());
            //find in disk cache
            Bitmap bitmap = mDiskImageCache.getBitmapFromDiskCache(imageKey);
            if (bitmap == null) { // Not found in disk cache as well as memory cache
                // Process as normal
                bitmap= Utils.getBitmap(params[0], mContext);
            }
            if(bitmap!=null){
                //add bitmap to memory cache
                mMemoryImageCache.addBitmapToMemoryCache(imageKey,bitmap);
            }
            //add final bitmap to disk cache
            mDiskImageCache.addBitmapToCache(imageKey, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mHandler!=null){
                Message message=new Message();
                message.obj=bitmap;
                mHandler.handleMessage(message);
            }
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mImageView!=null) {
                        mImageView.setImageBitmap(
                                ThumbnailUtils.extractThumbnail(bitmap, 100, 100));
                    }
                }
            });
        }
    }

}
