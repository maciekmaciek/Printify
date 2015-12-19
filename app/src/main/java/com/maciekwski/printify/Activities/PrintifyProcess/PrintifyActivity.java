package com.maciekwski.printify.Activities.PrintifyProcess;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import com.maciekwski.printify.Adapters.PrintifyPagerAdapter;
import com.maciekwski.printify.R;

import java.util.ArrayList;

public class PrintifyActivity extends ActionBarActivity {

    public PrintifyProcessViewPager printifyPager;
    private PagerAdapter printifyPagerAdapter;
    private ArrayList<Uri> imagesToDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.changeImages();
        setContentView(R.layout.activity_printify);
        this.preparePager();

    }
    private void changeImages() {
        this.imagesToDisplay = getIntent().getParcelableArrayListExtra("imageList");
    }

    private void preparePager() {
        printifyPager = (PrintifyProcessViewPager) findViewById(R.id.printify_pager);
        printifyPagerAdapter = new PrintifyPagerAdapter(getSupportFragmentManager(), imagesToDisplay);
        printifyPager.setAdapter(printifyPagerAdapter);
        PageChangeListener pageChangeListener = new PageChangeListener();
        printifyPager.addOnPageChangeListener(pageChangeListener);
        ((TextView) findViewById(R.id.text_view_page_num)).setText(1 + " / " + printifyPagerAdapter.getCount());

    }

    @Override
    public void onBackPressed() {
        if (printifyPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            printifyPager.setCurrentItem(printifyPager.getCurrentItem() - 1);
        }
    }


    public void continuePrintifyAsync(View view) {
        new PrintifyAsyncTask(getApplicationContext(), imagesToDisplay, this).execute();
    }

    public void finishPrintify(View view) {
        finish();
    }

    public void onPostExexute() {
        Intent intent = new Intent(getApplicationContext(), PostPrintifyActivity.class);
        intent.putParcelableArrayListExtra("imageList", imagesToDisplay);
        startActivity(intent);
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        TextView indicator = (TextView) findViewById(R.id.text_view_page_num);

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            indicator.setText((position + 1) + " / " + printifyPagerAdapter.getCount());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
/*    private class CreatePixelTablesAsyncTask extends AsyncTask<Integer, Integer, Integer> {


        }*/
}
