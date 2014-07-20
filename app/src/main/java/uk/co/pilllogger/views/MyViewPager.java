package uk.co.pilllogger.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import timber.log.Timber;

/**
 * Created by nick on 16/12/13.
 */
public class MyViewPager extends ViewPager {

    private Rect _rect;


    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (_rect != null) {
            Timber.v("rect is not null");
            if (_rect.contains((int) event.getX(), (int) event.getY())) {
                return false;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setViewForTouchIntercept(Rect rect) {
        _rect = rect;
        Timber.v("View has been passed in");
    }
}
