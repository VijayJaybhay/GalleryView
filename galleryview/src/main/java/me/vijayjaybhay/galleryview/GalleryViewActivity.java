package me.vijayjaybhay.galleryview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.vijayjaybhay.galleryview.adapters.BackgroundPagerAdapater;
import me.vijayjaybhay.galleryview.adapters.ImageScrollerAdapter;
import me.vijayjaybhay.galleryview.custom.GalleryProperties;
import me.vijayjaybhay.galleryview.custom.ImageScroller;
import me.vijayjaybhay.galleryview.utils.Utils;


public class GalleryViewActivity extends AppCompatActivity {

    /**
     * Action for this activity
     */
    public static final String ACTION_GALLERY_VIEW_ACTIVITY="me.vijayjaybhay.galleryview.GalleryViewActivity";
    /**
     * Property  of gallery view
     */
    public static final String ARG_GALLERY_VIEW_PROPERTIES="props";

    /**
     * Key to retrieve selected image
     */
    public static final String KEY_SELECTED_IMAGE_INDEX="selectedImage";
    /**
     * Default width used to scale Image
     */
    private static int DEFAULT_IMAGE_WIDTH=300;
    /**
     * Default height used to scale Image
     */
    private static int DEFAULT_IMAGE_HEIGHT=400;
    /**
     * Toolbar for actions in activity
     */
    private Toolbar mToolbar;
    /**
     * Background image of activity
     */
    private ImageView mBackgroundImage;
    private ViewPager mBackgroundViewPager;
    private PagerAdapter mBackgroundPagerAdapter;
    /**
     * Cancel ImageView to perform Cancel action
     */
    private ImageView mCancel;
    /**
     * Done ImageView to perform Done action
     */
    private ImageView mDone;

    /**
     * Title of Gallery view
     */
    private TextView mTitle;

    /**
     * List adapter for Images
     */
    private ImageScroller mImageContainer;
    /**
     * Custom adapter for populating passed images
     */
    private ListAdapter mAdapter;
    /**
     * Image data source to show in ImageScroller
     */
    private List<Object> mImages;

    /**
     * Represents properties of Gallery View.
     */
    private GalleryProperties properties;


    private Bitmap mSelectedImageBitmap;
    private int mSelectedImageIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        setReferences();
        initUi();
        setHandlers();
    }


    private void setReferences(){
        mToolbar= (Toolbar) findViewById(R.id.gv_t_gallery_view_toolbar);
        mBackgroundViewPager= (ViewPager) findViewById(R.id.gv_vp_gallery_view_background_adapter);
        mCancel= (ImageView) findViewById(R.id.gv_iv_gallery_view_cancel);
        mDone= (ImageView) findViewById(R.id.gv_iv_gallery_view_done);
        mImageContainer= (ImageScroller) findViewById(R.id.gv_is_gallery_view_image_container);
        mTitle= (TextView) findViewById(R.id.gv_tv_gallery_view_title);
    }

    /**
     * Initializes gallery view using properties passed in argument
     */
    private void initUi(){
        properties=(GalleryProperties)getIntent().getSerializableExtra(ARG_GALLERY_VIEW_PROPERTIES);
        if(properties==null)
            throw new NullPointerException("Gallery Properties cannot be null");
        setDataSourceProps();
        setToolbarProps();
        setTitleProps();
        setImageScrollerProps();
    }

    private void setHandlers(){
        mImageContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //changeImage(position);
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resIntent);
                finish();
            }
        });
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resIntent = new Intent();
                resIntent.putExtra(KEY_SELECTED_IMAGE_INDEX, mSelectedImageIndex);
                setResult(Activity.RESULT_OK, resIntent);
                finish();
            }
        });
        mBackgroundViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                mImageContainer.clearFocus();
                mImageContainer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageContainer.requestFocusFromTouch();
                        mImageContainer.setSelection(position);
                        mImageContainer.requestFocus();
                    }
                },100L);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * Sets data source for adapter
     */
    public void setDataSourceProps(){

        Object dataSource=properties.getProps().get(GalleryProperties.GalleryProperty.DATA_SOURCE);
        if( dataSource instanceof int[]){
            mImages=new ArrayList<>();
            int[] res= (int[]) dataSource;
            if(res==null || res.length==0)
                throw new IllegalArgumentException("Argument can not be null. It must be array int and should not be empty");
            for(int r:res)
                mImages.add(new Integer(r));
        }else if(dataSource instanceof List){
            mImages= (List<Object>) dataSource;
            if(mImages==null || mImages.isEmpty())
                throw new IllegalArgumentException("Argument can not be null. It must be list of String and should not be empty");
        }else{
            throw new IllegalArgumentException("Invalid data source");
        }
        mAdapter=new ImageScrollerAdapter(this,mImages);
        mBackgroundPagerAdapter=new BackgroundPagerAdapater(this,mImages);
        mImageContainer.setAdapter(mAdapter);
        mBackgroundViewPager.setAdapter(mBackgroundPagerAdapter);

    }

    /**
     * Sets Drawables left and right drawables of Toolbar
     */
    private void setToolbarProps() {
        mCancel.setImageDrawable(ContextCompat.getDrawable(this,
                (Integer) properties.getProps().get(GalleryProperties.GalleryProperty.DRAWABLE_LEFT_ACTION)));
        mDone.setImageDrawable(ContextCompat.getDrawable(this,
                (Integer) properties.getProps().get(GalleryProperties.GalleryProperty.DRAWABLE_RIGHT_ACTION)));
    }


    /**
     * Sets title properties of gallery view
     */
    private void setTitleProps() {
        boolean hideTitle= (boolean) properties.getProps().get(GalleryProperties.GalleryProperty.HIDE_TITLE);
        if(hideTitle)
            mTitle.setVisibility(View.GONE);
        else
            mTitle.setVisibility(View.VISIBLE);
        String titleText= (String) properties.getProps().get(GalleryProperties.GalleryProperty.TEXT_TITLE);
        if(titleText!=null && !titleText.equals(""))
            mTitle.setText(titleText);
    }

    /**
     * Sets behaviour of image scroller
     */
    private void setImageScrollerProps(){
        boolean hideImageScroller= (boolean) properties.getProps().get(GalleryProperties.GalleryProperty.HIDE_IMAGE_SCROLLER);
        if(hideImageScroller)
            mBackgroundImage.setOnTouchListener(new View.OnTouchListener() {
                boolean isShowing = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (isShowing) {
                        mImageContainer.setVisibility(View.GONE);
                        isShowing=false;
                    } else {
                        mImageContainer.setVisibility(View.VISIBLE);
                        isShowing=true;
                    }
                    return false;
                }
            });
    }

    public void changeImage(int index) {
        mSelectedImageIndex=index;
        mBackgroundViewPager.setCurrentItem(index);
    }
}
