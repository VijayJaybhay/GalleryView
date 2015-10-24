package me.vijayjaybhay.galleryview.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;

import me.vijayjaybhay.galleryview.R;
import me.vijayjaybhay.galleryview.cache.ImageCache;
import me.vijayjaybhay.galleryview.cache.MemoryImageCache;
import me.vijayjaybhay.galleryview.utils.Utils;

/**
 * Created by Jaybhay Vijay on 9/29/2015.
 */
public class BackgroundPagerAdapater extends PagerAdapter {

    private Context mContext;
    private List<Object> mImages;



    public BackgroundPagerAdapater(Context context, List<Object> images) {
        this.mContext=context;
        this.mImages=images;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return  view ==object;
    }
    @Override
    public Object instantiateItem(ViewGroup container,final int position) {
        View view=container;
//            if(view==null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_background_view_pager, null);
//        }
       final ImageView imageView= (ImageView) view.findViewById(R.id.gv_iv_background_view_pager_background_image);
        ProgressBar progressBar=(ProgressBar) view.findViewById(R.id.gv_pb_image_view_progress);
        progressBar.setVisibility(View.VISIBLE);
        ImageCache imageCache=ImageCache.getInstance(mContext);
        final MemoryImageCache memoryImageCache=MemoryImageCache.getInstance(mContext);
        final Bitmap bitmap=memoryImageCache.getBitmapFromMemCache(String.valueOf(mImages.get(position)));
        if(bitmap!=null){
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);
                }
            });

            progressBar.setVisibility(View.GONE);
        }else {
            ImageCache.BitmapWorkerTask bitmapWorkerTask = imageCache.new BitmapWorkerTask(new MyHandler(imageView, progressBar));
            //imageView.setImageBitmap(Utils.getBitmap(mImages.get(position), mContext));
            bitmapWorkerTask.execute(mImages.get(position));
        }
        ((ViewPager) container).addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    private class MyHandler extends Handler {
        private ImageView mImageView;
        private ProgressBar mProgressBar;
        public MyHandler(ImageView imageView, ProgressBar progressBar) {
            mImageView=imageView;
            mProgressBar=progressBar;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Bitmap bitmap= (Bitmap) msg.obj;
            mImageView.setImageBitmap(bitmap);
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageBitmap(bitmap);
                    }
                });
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
