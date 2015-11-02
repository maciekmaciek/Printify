package com.maciekwski.printify.Activities.VerticesSetter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TextView;
import com.maciekwski.printify.R;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 29.10.2015.
 */
public class VerticesSetterViewPager extends ViewPager {
    Context appContext;
    public VerticesSetterViewPager(Context context) {
        super(context);

        this.appContext = context;
    }

    public VerticesSetterViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.appContext = context;
    }


}
