package me.vijayjaybhay.galleryview.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
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
import me.vijayjaybhay.galleryview.cache.DiskImageCache;
import me.vijayjaybhay.galleryview.cache.DiskLruCache;
import me.vijayjaybhay.galleryview.cache.Util;
import me.vijayjaybhay.galleryview.utils.Utils;

import static android.os.Environment.isExternalStorageRemovable;

/**
 * Created by Jaybhay Vijay on 9/11/2015.
 */
public class ImageScrollerAdapter extends BaseAdapter {
    /**
     * List of image paths
     */
    private List<Object> mImages;
    private Context  mContext;

    public ImageScrollerAdapter(Context mContext,List<Object> mImages){
        this.mImages=mImages;
        this.mContext=mContext;
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
        imageView = (ImageView) view.findViewById(R.id.ivImageViewItem);
        DiskImageCache.getInstance(mContext).loadBitmap(mImages.get(position),imageView);
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
