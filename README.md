# GalleryView
[ ![Download](https://api.bintray.com/packages/jaybhayvijay/maven/GalleryView/images/download.svg) ](https://bintray.com/jaybhayvijay/maven/GalleryView/_latestVersion)

GalleryView is Open Source Android Project that is built with idea of Android Gallery Widget (which was deprecated now). Gallery View based on single activity GalleryViewActivity. You just need to pass it list of drawables or folder containing images. It renders images. To obtain performance benefits, it has implemented two way image cache (Memory and Disk Cache) to load images faster. 

#Demo
[![Gallery View Demo](https://github.com/VijayJaybhay/GalleryView/tree/master/app/src/main/res/demo.gif)](https://www.youtube.com/watch?v=g66utT6_wws)

#How to use?

For drawables in res folder :
```
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
                props.setDataSource(d);
                props.hideTitle(false);
                props.setTitle("Select Wallpaper");
                props.hideImageScroller(true);
                intent.putExtra(GalleryViewActivity.ARG_GALLERY_VIEW_PROPERTIES, props);
                startActivityForResult(intent, 100);
 ```               
and 
```
 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100){
            if(resultCode== Activity.RESULT_OK){
                int index=data.getIntExtra(GalleryViewActivity.KEY_SELECTED_IMAGE_INDEX, 0);
                String imageKey=String.valueOf(d[index]);
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
```
For image files on external/internal storage:
```
 Intent intent = new Intent(GalleryViewActivity.ACTION_GALLERY_VIEW_ACTIVITY);
                GalleryProperties props = new GalleryProperties();
                files= Utils.getFiles(Environment.getExternalStorageDirectory() + "/temp");//directory containing images
                props.setDataSource(files);
                props.hideTitle(false);
                props.setTitle("Select Wallpaper");
                props.hideImageScroller(true);
                intent.putExtra(GalleryViewActivity.ARG_GALLERY_VIEW_PROPERTIES, props);
                startActivityForResult(intent, 100);
 ```
 
 and 
 ```
  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100){
            if(resultCode== Activity.RESULT_OK){
                int index=data.getIntExtra(GalleryViewActivity.KEY_SELECTED_IMAGE_INDEX, 0);
                String imageKey=String.valueOf(files.get(index).hashCode());
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
 ```
 
#Credits
1. The all icons in this project taken from flaticon.com. I want to credit following authors:<br/>
<div>Icons made by <a href="http://www.flaticon.com/authors/petr-had" title="Petr Had">Petr Had</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a>  
<div>Icons made by <a href="http://www.flaticon.com/authors/google" title="Google">Google</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> <br/>
<div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a>

2. The project uses following classes from Android Opensource Project:DiskLruCache,StrictLineReader
3. Thanks to stackoverflow community, specifically following stackoverflow user:<br/>
    <a href="http://stackoverflow.com/users/53501/rich">rich</a> for his solution on <a href="http://stackoverflow.com/questions/15459834/lrucache-not-working for LRU cache hit problem"> LRU cache hit issue </a>
 
