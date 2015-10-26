package me.vijayjaybhay.galleryviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import me.vijayjaybhay.galleryview.GalleryViewActivity;
import me.vijayjaybhay.galleryview.cache.DiskImageCache;
import me.vijayjaybhay.galleryview.cache.ImageCache;
import me.vijayjaybhay.galleryview.cache.MemoryImageCache;
import me.vijayjaybhay.galleryview.custom.GalleryProperties;
import me.vijayjaybhay.galleryview.utils.BitmapUtility;
import me.vijayjaybhay.galleryview.utils.Utils;


public class MainActivity extends AppCompatActivity {
    private  int[]d;
    private Button mChooseButton;
    private ImageView mImageView;
    private List<Object> files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChooseButton=(Button)findViewById(R.id.chooseImage);
        mImageView=(ImageView)findViewById(R.id.imageView);

        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d = new int[]{
                        R.drawable.image1,
                        R.drawable.image2,
                        R.drawable.image3,
                        R.drawable.image4,
                        R.drawable.image5,
                        R.drawable.image6,
                        R.drawable.image7,
                        R.drawable.image8,
                        R.drawable.image9
                };
                Intent intent = new Intent(GalleryViewActivity.ACTION_GALLERY_VIEW_ACTIVITY);
                GalleryProperties props = new GalleryProperties();
               // props.setDataSource(d);
                files= Utils.getFiles(Environment.getExternalStorageDirectory() + "/temp");
                props.setDataSource(files);
                props.hideTitle(false);
                props.setTitle("My Title for activity");
                props.hideImageScroller(true);
                intent.putExtra(GalleryViewActivity.ARG_GALLERY_VIEW_PROPERTIES, props);
                startActivityForResult(intent, 100);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100){
            if(resultCode== Activity.RESULT_OK){
                int index=data.getIntExtra(GalleryViewActivity.KEY_SELECTED_IMAGE_INDEX, 0);
                String imageKey=String.valueOf(files.get(index).hashCode());//for drawables use d[index]
                MemoryImageCache memoryImageCache=MemoryImageCache.getInstance(MainActivity.this);
                Bitmap bitmap=memoryImageCache.getBitmapFromMemCache(imageKey);
                if(bitmap==null){
                    DiskImageCache diskImageCache=DiskImageCache.getInstance(MainActivity.this);
                    bitmap=diskImageCache.getBitmapFromDiskCache(imageKey);
                }
                mImageView.setImageBitmap(bitmap);
            }else if(resultCode==Activity.RESULT_CANCELED){

            }
        }
    }

}
