package com.maciekwski.printify.Activities;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Parcelable;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.View;
        import android.widget.GridView;

        import com.maciekwski.printify.Adapters.PictureAdapter;
        import com.maciekwski.printify.ImageProcessing.ProcessingOption;
        import com.maciekwski.printify.R;
        import net.soulwolf.image.picturelib.PictureProcess;
        import nl.changer.polypicker.Config;
        import nl.changer.polypicker.ImagePickerActivity;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.HashSet;
        import java.util.List;


public class GalleryActivity extends AppCompatActivity {

    static final boolean DEBUG = true;

    static final String  LOG_TAG = "GalleryActivity:";

    PictureProcess mPictureProcess;

    Toolbar mToolbar;

    GridView mPictureGrid;

    List<Uri> mPictureList;
    HashMap<ProcessingOption, Boolean> processingOptions;
    PictureAdapter mPictureAdapter;

    private static final String TAG = GalleryActivity.class.getSimpleName();

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_GET_N_IMAGES = 14;

    private Context mContext;
    HashSet<Uri> mMedia = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mContext = GalleryActivity.this;
        mPictureGrid = (GridView) findViewById(R.id.pi_grid);
        mPictureProcess = new PictureProcess(this);

        mPictureList = new ArrayList<>();
        mPictureAdapter = new PictureAdapter(this,mPictureList);
        mPictureGrid.setAdapter(mPictureAdapter);
        initProcessingOptions();
    }

    private void initProcessingOptions() {
        processingOptions = new HashMap<>();
        processingOptions.put(ProcessingOption.A, true);
        processingOptions.put(ProcessingOption.B, true);
        processingOptions.put(ProcessingOption.C, true);
        processingOptions.put(ProcessingOption.D, true);
    }


    public void getImages(View v) {
        Intent intent = new Intent(mContext, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.blue)
                .setCameraButtonColor(R.color.orange)
                .setSelectionLimit(5)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES || requestCode == INTENT_REQUEST_GET_N_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null) {
                    for (Uri uri : uris) {
                        Log.i(TAG, " uri: " + uri);
                        mMedia.add(uri);
                    }

                    showMedia();
                }
            }
        }
    }
/*
    private void showMedia() {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();

        Iterator<Uri> iterator = mMedia.iterator();
        ImageInternalFetcher imageFetcher = new ImageInternalFetcher(this, 500);
        while (iterator.hasNext()) {
            Uri uri = iterator.next();

            // showImage(uri);
            Log.i(TAG, " uri: " + uri);
            if (mMedia.size() >= 1) {
                mSelectedImagesContainer.setVisibility(View.VISIBLE);
            }

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.media_layout, null);

            // View removeBtn = imageHolder.findViewById(R.id.remove_media);
            // initRemoveBtn(removeBtn, imageHolder, uri);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);

            if (!uri.toString().contains("content://")) {
                // probably a relative uri
                uri = Uri.fromFile(new File(uri.toString()));
            }

            imageFetcher.loadImage(uri, thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

            // set the dimension to correctly
            // show the image thumbnail.
            int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
            int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));
        }
    }
*/

    protected void showMedia(){
        mPictureList.clear();
        if(!mMedia.isEmpty()){
            mPictureList.addAll(mMedia);
        }
        mPictureAdapter.notifyDataSetChanged();
    }

    public void startPrintify(View view) {
        //TODO add options
        Intent intent = new Intent(mContext, PrintifyActivity.class);
        intent.putParcelableArrayListExtra("imageList", (ArrayList<Uri>)mPictureList);
        startActivity(intent);
    }
}