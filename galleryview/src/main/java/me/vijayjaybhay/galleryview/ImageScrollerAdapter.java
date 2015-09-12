package me.vijayjaybhay.galleryview;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaybhay Vijay on 9/11/2015.
 */
public class ImageScrollerAdapter extends BaseAdapter {
    /**
     * List of image paths
     */
    private List<Integer> mImages;
    private Context  mContext;
    private Bitmap[] mBitmaps;
    private int count=0;
    public ImageScrollerAdapter(Context mContext,List<Integer> mImages){
        this.mImages=mImages;
        this.mContext=mContext;
        mBitmaps=new Bitmap[mImages.size()];
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
        ImageView imageView;
        if(view==null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_galleryview_image, null);
        }

        if(count>=0 && count<=mImages.size()-1){
            Bitmap bitmap = BitmapUtility.decodeSampledBitmapFromResource(mContext.getResources(), mImages.get(position), 300, 400);
            mBitmaps[position]=bitmap;
        }

        imageView = (ImageView) view.findViewById(R.id.ivImageViewItem);
        imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(mBitmaps[position], 50, 50));
        count++;
        return view;
    }
}
