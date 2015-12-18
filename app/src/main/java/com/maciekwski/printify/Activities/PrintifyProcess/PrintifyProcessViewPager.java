package com.maciekwski.printify.Activities.PrintifyProcess;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 17.12.2015.
 */
public class PrintifyProcessViewPager extends ViewPager {
    Context appContext;
    public  PrintifyProcessViewPager(Context context) {
        super(context);

        this.appContext = context;
    }
    private boolean isPagingEnabled = true;

    public  PrintifyProcessViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.appContext = context;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

}
