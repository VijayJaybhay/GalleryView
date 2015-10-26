package me.vijayjaybhay.galleryview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.vijayjaybhay.galleryview.adapters.ImageScrollerAdapter;
import me.vijayjaybhay.galleryview.cache.DiskImageCache;
import me.vijayjaybhay.galleryview.cache.ImageCache;
import me.vijayjaybhay.galleryview.cache.MemoryImageCache;
import me.vijayjaybhay.galleryview.custom.GalleryProperties;
import me.vijayjaybhay.galleryview.custom.ImageScroller;


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
     * Default width used to scale Image(sub-sample).Default is 300
     */
    private int requestedWidth=300;
    /**
     * Default height used to scale Image(sub-sample).Default is 400
     */
    private int requestedHeight=400;
    /**
     * Toolbar for actions in activity
     */
    private Toolbar mToolbar;
    /**
     * Background image of activity
     */
    private ImageView mBackgroundImage;

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

    /**
     * Selected index in Data Source
     */
    private int mSelectedImageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        setReferences();
        initUi();
        setHandlers();
    }

    /**
     * Sets references to UI elements
     */
    private void setReferences(){
        mToolbar= (Toolbar) findViewById(R.id.gv_t_gallery_view_toolbar);
        mBackgroundImage=(ImageView)findViewById(R.id.gv_iv_gallery_view_background_image);
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
        //set first image in background image
        loadBackgroundImage(0);
    }

    /**
     * Sets handlers
     */
    private void setHandlers(){
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
        mImageContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadBackgroundImage(position);
            }
        });
    }

    /**
     * Loads background image from Date Source.
     * @param position index of image in Data Source
     */
    private void loadBackgroundImage(int position){
        mSelectedImageIndex=position;
        String imageKey=String.valueOf(mImages.get(position).hashCode());
        MemoryImageCache memoryImageCache=MemoryImageCache.getInstance(GalleryViewActivity.this);
        Bitmap bitmap=memoryImageCache.getBitmapFromMemCache(imageKey);
        ImageCache imageCache=ImageCache.getInstance(GalleryViewActivity.this);
        if(bitmap==null){
            ImageCache.BitmapWorkerTask bitmapWorkerTask = imageCache.new BitmapWorkerTask(new MyHandler(mBackgroundImage));
            bitmapWorkerTask.execute(mImages.get(position));
        }
        mBackgroundImage.setImageBitmap(bitmap);
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
        mImageContainer.setAdapter(mAdapter);
    }

    /**
     * Sets left and right drawables of Toolbar
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

    private class MyHandler extends Handler {
        private ImageView mImageView;
        public MyHandler(ImageView imageView) {
            mImageView=imageView;
        }

        @Override
        public void handleMessage(Message msg) {
            final Bitmap bitmap= (Bitmap) msg.obj;
            mImageView.setImageBitmap(bitmap);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(bitmap);
                }
            });
        }
    }
}
