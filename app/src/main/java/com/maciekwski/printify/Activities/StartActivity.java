package com.maciekwski.printify.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import com.maciekwski.printify.R;

import java.util.ArrayList;


public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void startActivityOnClick(View v) {
        chooseFromGallery();
    }

    private void chooseFromGallery() {
        Intent startGalleryForMultiSelect = new Intent(getApplicationContext(), GalleryActivity.class);
        startGalleryForMultiSelect.putExtra("multiselect", R.integer.multiselect);
        startActivity(startGalleryForMultiSelect);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        data.getClipData();
        ArrayList<Parcelable> list = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (list != null) {
            for (Parcelable parcel : list) {
                Uri uri = (Uri) parcel;
                Log.d("Uri", uri.toString());

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
