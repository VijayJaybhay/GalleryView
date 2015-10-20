package me.vijayjaybhay.galleryview.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import me.vijayjaybhay.galleryview.R;
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
    public Object instantiateItem(ViewGroup container, int position) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.item_background_view_pager,null);
        ImageView imageView= (ImageView) view.findViewById(R.id.gv_iv_background_view_pager_background_image);
        imageView.setImageBitmap(Utils.getBitmap(mImages.get(position),mContext));
        ((ViewPager) container).addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
