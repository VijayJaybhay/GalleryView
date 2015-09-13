package me.vijayjaybhay.galleryview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class GalleryViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

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

    GalleryProperties properties;

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
        mToolbar= (Toolbar) findViewById(R.id.tGalleryViewToolbar);
        mBackgroundImage= (ImageView) findViewById(R.id.ivGalleryViewBackgroundImage);
        mCancel= (ImageView) findViewById(R.id.ivGalleryViewCancel);
        mDone= (ImageView) findViewById(R.id.ivGalleryViewDone);
        mImageContainer= (ImageScroller) findViewById(R.id.icGalleryViewImageContainer);
        mTitle= (TextView) findViewById(R.id.tvGalleryViewTitle);
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
    }

    private void setHandlers(){
        mImageContainer.setOnItemClickListener(this);
        mCancel.setOnClickListener(this);
        mDone.setOnClickListener(this);
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
            setBackgroundImage(0, getImageBitmap(0));
        }else if(dataSource instanceof List){
            mImages= (List<Object>) dataSource;
            if(mImages==null || mImages.isEmpty())
                throw new IllegalArgumentException("Argument can not be null. It must be list of String and should not be empty");
            setBackgroundImage(0, getImageBitmap(0));
        }else{
            throw new IllegalArgumentException("Invalid data source");
        }
        mAdapter=new ImageScrollerAdapter(this,mImages);
        mImageContainer.setAdapter(mAdapter);
    }

    /**
     * Sets Drawables left and right drawables of Toolbar
     */
    private void setToolbarProps() {
        mCancel.setBackgroundResource((Integer) properties.getProps().get(GalleryProperties.GalleryProperty.DRAWABLE_LEFT_ACTION));
        mDone.setBackgroundResource((Integer) properties.getProps().get(GalleryProperties.GalleryProperty.DRAWABLE_RIGHT_ACTION));
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setBackgroundImage(int position,Bitmap bitmap){
        mBackgroundImage.setImageBitmap(bitmap);
        mSelectedImageBitmap=bitmap;
        mSelectedImageIndex=position;
    }
    public Bitmap getImageBitmap(int index){
        Object dataSource=properties.getProps().get(GalleryProperties.GalleryProperty.DATA_SOURCE);
        Bitmap bitmap=null;
        if( dataSource instanceof int[]){
            bitmap=BitmapUtility.decodeSampledBitmapFromResource(getResources(), (Integer) mImages.get(index), DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
        }else if(dataSource instanceof List) {
            bitmap = BitmapUtility.decodeSampledBitmapFromResource((String) mImages.get(index), DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
        }
        return bitmap;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mSelectedImageIndex!=position) {
            mSelectedImageBitmap = getImageBitmap(position);
            mBackgroundImage.setImageBitmap(mSelectedImageBitmap);
        }
        mSelectedImageIndex=position;
    }

    @Override
    public void onClick(View v) {
        Intent resIntent=new Intent();
        if(v==mCancel){
            setResult(Activity.RESULT_CANCELED,resIntent);
        }else if(v==mDone){
            resIntent.putExtra(KEY_SELECTED_IMAGE_INDEX,mSelectedImageIndex);
            setResult(Activity.RESULT_OK,resIntent);
        }
        finish();
    }
}
