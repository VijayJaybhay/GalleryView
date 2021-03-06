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

package me.vijayjaybhay.galleryview.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import me.vijayjaybhay.galleryview.R;
import me.vijayjaybhay.galleryview.cache.ImageCache;

/**
 * Created by Jaybhay Vijay on 9/11/2015.
 */
public class ImageScrollerAdapter extends BaseAdapter {
    /**
     * List of image paths
     */
    private List<Object> mImages;
    private Context  mContext;
    private ImageCache mImageCache;

    public ImageScrollerAdapter(Context mContext,List<Object> mImages){
        this.mImages=mImages;
        this.mContext=mContext;
        mImageCache=ImageCache.getInstance(mContext);
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
        mImageCache.loadBitmap(mImages.get(position), imageView);
        return view;
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
