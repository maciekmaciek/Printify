package com.maciekwski.printify.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import com.maciekwski.printify.Activities.VerticesSetter.VerticesSetterActivity;
import com.maciekwski.printify.Adapters.PictureAdapter;
import com.maciekwski.printify.R;
import net.soulwolf.image.picturelib.PictureProcess;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class GalleryActivity extends Activity {

    PictureProcess mPictureProcess;
    GridView mPictureGrid;
    List<Uri> mPictureList;
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
        mPictureAdapter = new PictureAdapter(this, mPictureList);
        mPictureGrid.setAdapter(mPictureAdapter);
    }


    public void getImages(View v) {
        Intent intent = new Intent(mContext, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.SeaGreen)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.background_material_dark)
                .setCameraButtonColor(R.color.SeaGreen)
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

    protected void showMedia() {
        mPictureList.clear();
        if (!mMedia.isEmpty()) {
            mPictureList.addAll(mMedia);
        }
        mPictureAdapter.notifyDataSetChanged();
    }

    public void startVerticesSetting(View view) {
        Intent intent = new Intent(mContext, VerticesSetterActivity.class);
        intent.putParcelableArrayListExtra("imageList", (ArrayList<Uri>) mPictureList);
        startActivity(intent);
        finish();
    }
}