package uk.co.cntwo.pilllogger.animations;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by nick on 25/10/13.
 */
public class HeightAnimation extends Animation {
    private int _tartgetHeight;
    private View _view;
    private boolean _expand;
    private Activity _activity;

    public HeightAnimation(View view, int targetHeight, boolean expand, Activity activity) {
        _view = view;
        _tartgetHeight = targetHeight;
        _expand = expand;
        _activity = activity;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        if (_expand) {
            newHeight = (int) (_tartgetHeight * interpolatedTime);
        } else {
            newHeight = (int) (_tartgetHeight * (1 - interpolatedTime));
        }
        _view.getLayoutParams().height = newHeight;
        _view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
