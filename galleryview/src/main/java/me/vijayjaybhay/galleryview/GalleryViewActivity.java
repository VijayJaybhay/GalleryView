package me.vijayjaybhay.galleryview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;


public class GalleryViewActivity extends AppCompatActivity {

    /**
     * Action for this activity
     */
    public static final String ACTION_GALLERY_VIEW_ACTIVITY="me.vijayjaybhay.galleryview.GalleryViewActivity";
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
     * List adapter for Images
     */
    private ImageScroller mImageContainer;
    /**
     * Custom adapter for populating passed images
     */
    private ListAdapter mAdapter;
    /**
     * Image paths containing path of image to show in ImageScroller
     */
    private List<String> mImagePaths;


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
        //mImagePaths=Utils.getDrawablePaths();
        List<Integer> temp=new ArrayList<>();
        int[] res=getIntent().getIntArrayExtra("d");
        if(res==null)
            throw new IllegalArgumentException("Argrument can not be null. It must be array int");
        for(int r:res)
            temp.add(new Integer(r));
        mBackgroundImage.setImageBitmap(BitmapUtility.decodeSampledBitmapFromResource(getResources(),res[0],300,400));
        mAdapter=new ImageScrollerAdapter(this,temp);
        mImageContainer= (ImageScroller) findViewById(R.id.icGalleryViewImageContainer);
    }

    private void initUi(){
        mImageContainer.setAdapter(mAdapter);
    }

    private void setHandlers(){
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
}
