package com.maciekwski.printify.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.maciekwski.printify.R;
import net.soulwolf.image.picturelib.PictureProcess;

import java.util.ArrayList;


public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choo, menu);
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

    public void startActivityOnClick(View v) {
        Intent in;
        switch (v.getId()) {
            case R.id.button_take_pictures:
                break;
            case R.id.button_from_gallery:
                chooseFromGallery();
                break;
            case R.id.button_your_pictures:
                in = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(in);
        }
    }

    private void chooseFromGallery() {
        Intent startGalleryForMultiSelect = new Intent(getApplicationContext(), GalleryActivity.class);
        startGalleryForMultiSelect.putExtra("multiselect", R.integer.multiselect);
        startActivity(startGalleryForMultiSelect);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (Intent.ACTION_SEND_MULTIPLE.equals(data.getExtras().get()) && data.hasExtra(Intent.EXTRA_STREAM)) {
            // retrieve a collection of selected images
            data.getClipData();
            ArrayList<Parcelable> list = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            // iterate over these images
            if( list != null ) {
                for (Parcelable parcel : list) {
                    Uri uri = (Uri) parcel;
                    Log.d("Uri", uri.toString());

                }
            }
       // }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
