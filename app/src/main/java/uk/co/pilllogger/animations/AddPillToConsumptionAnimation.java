package uk.co.pilllogger.animations;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by nick on 25/10/13.
 */
public class AddPillToConsumptionAnimation extends Animation {
    private int _targetWidth;
    private View _view;
    private boolean _expand;
    private Activity _activity;

    public AddPillToConsumptionAnimation(View view, int targetWidth, boolean expand, Activity activity) {
        _view = view;
        _targetWidth = targetWidth;
        _expand = expand;
        _activity = activity;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth;
        if (_expand) {
            newWidth = (int) (_targetWidth * interpolatedTime);
        } else {
            newWidth = (int) (_targetWidth * (1 - interpolatedTime));
        }
        _view.getLayoutParams().width = newWidth;
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
