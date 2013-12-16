package uk.co.pilllogger.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by nick on 16/12/13.
 */
public class MyViewPager extends ViewPager {

    private int childId;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewPager(Context context) {
        super(context);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//         return false;
//    }
}
