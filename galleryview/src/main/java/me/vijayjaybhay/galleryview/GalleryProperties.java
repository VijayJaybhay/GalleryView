package me.vijayjaybhay.galleryview;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jaybhay Vijay on 9/11/2015.
 */
public class GalleryProperties {
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
     * Enables swipe left or right if true is passed else swipe is disabled.
     * @param enable
     */
    public void enableSwipe(boolean enable){
        props.put(GalleryProperty.ENABLE_IMAGE_SWIPE,enable);
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
     * Sets default properties for Gallery
     */
    private void setDefaultGalleryProperties(){
        props.put(GalleryProperty.TEXT_TITLE,"");
        props.put(GalleryProperty.HIDE_TITLE,true);
        props.put(GalleryProperty.DRAWABLE_LEFT_ACTION, R.mipmap.ic_galleryview_cancel);
        props.put(GalleryProperty.DRAWABLE_RIGHT_ACTION,R.mipmap.ic_galleryview_done);
        props.put(GalleryProperty.ENABLE_IMAGE_SWIPE,true);
        props.put(GalleryProperty.ENABLE_IMAGE_SWIPE,true);
    }
    /**
     * Defines all Gallery Properties
     */
    private enum GalleryProperty{
        TEXT_TITLE,
        DRAWABLE_LEFT_ACTION,
        DRAWABLE_RIGHT_ACTION,
        HIDE_TITLE,
        ENABLE_IMAGE_SWIPE,
        HIDE_IMAGE_SCROLLER,
    }
}
