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

package me.vijayjaybhay.galleryview.custom;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import me.vijayjaybhay.galleryview.R;

/**
 * Created by Jaybhay Vijay on 9/11/2015.
 */
public class GalleryProperties implements Serializable {
    /**
     * Holds all gallery properties
     */
    private Map<GalleryProperty,Object> props;

    public GalleryProperties(){
        props=new HashMap<>();
        setDefaultGalleryProperties();
    }


    public Map<GalleryProperty, Object> getProps() {
        return props;
    }

    /**
     * Sets title to activity's Toolbar
     * @param title
     */
    @NonNull
    public void setTitle(String title){
        props.put(GalleryProperty.TEXT_TITLE,title);
    }


    /**
     * Sets resource ID of left drawable of toolbar in activity
     * @param resourceId
     */
    @DrawableRes
    public void setLeftDrawable(int resourceId){
        props.put(GalleryProperty.DRAWABLE_LEFT_ACTION, resourceId);
    }

    /**
     * Sets resource of right drawable of toolbar in activity
     * @param resourceId
     */
    @DrawableRes
    public void setRightDrawable(int resourceId){
        props.put(GalleryProperty.DRAWABLE_RIGHT_ACTION,resourceId);
    }

    /**
     * Sets visibility of Toolbar title of activity. If true is passed , the title is visible else hidden
     * If title is null then no action is performed.
     * @param hide
     */

    public void hideTitle(boolean hide){
        props.put(GalleryProperty.HIDE_TITLE,hide);
    }


    /**
     * Hides the Image scroller if user taps outside of ImageScroller and shows ImageScroller if user again
     * taps on screen.
     * @param hide
     */
    public void hideImageScroller(boolean hide){
        props.put(GalleryProperty.HIDE_IMAGE_SCROLLER,hide);
    }

    /**
     * Sets data source of gallery view
     * @param dataSource
     */
    public void setDataSource(Object dataSource){
        props.put(GalleryProperty.DATA_SOURCE,dataSource);
    }

    /**
     * Sets default properties for Gallery
     */
    private void setDefaultGalleryProperties(){
        props.put(GalleryProperty.TEXT_TITLE,"");
        props.put(GalleryProperty.HIDE_TITLE,true);
        props.put(GalleryProperty.DRAWABLE_LEFT_ACTION, R.mipmap.gv_ic_back);
        props.put(GalleryProperty.DRAWABLE_RIGHT_ACTION, R.mipmap.gv_ic_done);
        props.put(GalleryProperty.HIDE_IMAGE_SCROLLER,true);
    }
    /**
     * Defines all Gallery Properties
     */
    public enum GalleryProperty{
        TEXT_TITLE,
        DRAWABLE_LEFT_ACTION,
        DRAWABLE_RIGHT_ACTION,
        HIDE_TITLE,
        HIDE_IMAGE_SCROLLER,
        DATA_SOURCE,
    }

}
