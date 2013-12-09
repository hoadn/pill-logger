package uk.co.pilllogger.animations;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.MainActivity;
import uk.co.pilllogger.helpers.Logger;

/**
 * Created by Nick on 07/12/13.
 */
public class FadeBackgroundPageTransformer implements ViewPager.PageTransformer {

    MainActivity _activity;
    int change;
    float r;
    float g;
    float b;
    float _previousV = -1;
    float[] _results;
    int[] _fadefrom;
    View _colourBackground;
    boolean _goingForwards = true;

    public FadeBackgroundPageTransformer(MainActivity activity) {
        _activity = activity;
        _colourBackground = _activity.findViewById(R.id.colour_background);
    }

    @Override
    public void transformPage(View view, float v) {

        if (_previousV == 1.0 && v == 0.0)
            return;

        if (((_previousV == -1) || (_previousV - v > 0.5)) && (((v*100) < 15) && (v >=0 && v <= 1))) {
            _fadefrom = _activity.getFadeFrom();
            _results = calculateColourTransition(_fadefrom, _activity.getFadeToBackward());
            _goingForwards = false;
            Logger.v("Test", "Going Backwards");
        }
        else if (((_previousV == -1) || (v - _previousV > 0.5)) && (((v*100) >= 15) && (v >=0 && v <= 1))) {
            _fadefrom = _activity.getFadeFrom();
            _results = calculateColourTransition(_fadefrom, _activity.getFadeToForward());
            _goingForwards = true;
            Logger.v("Test", "rgb Going Forwards");
        }
        if (v >= 0 && v <= 1) {
            if (_goingForwards) {
                r = _fadefrom[0] - ((100-(v*100)) * _results[0]);
                g = _fadefrom[1] - ((100-(v*100)) * _results[1]);
                b = _fadefrom[2] - ((100-(v*100)) * _results[2]);
            }
            else {
                r = _fadefrom[0] - ((v*100) * _results[0]);
                g = _fadefrom[1] - ((v*100) * _results[1]);
                b = _fadefrom[2] - ((v*100) * _results[2]);
            }
            _colourBackground.setBackgroundColor(Color.argb(120, (int)r, (int)g, (int)b));
            Logger.v("Test", "rgb = " + r + " " + g + " " + b + " V = " + (v * 100) + " previousV = " + _previousV + " v = " + v);
            _previousV = v;
        }
        if (v == 0 || v == 1) {
            _previousV = -1;
        }

    }

    private float[] calculateColourTransition(int [] fadeFrom, int[] fadeTo) {
        float[] results = new float[3];
        for (int i = 0 ; i < fadeFrom.length ; i++) {
            float diff = fadeFrom[i] - fadeTo[i];
            results[i] = diff/100;
        }
        if (results[0] == 0.0 && results[1] == 0.0 && results[2] == 0.0)
            results = calculateColourTransition(_activity.getFadeFrom(), _activity.getFadeToBackward());
        Logger.v("Test", "Results: " + results[0] + " " + results[1] + " " + results[2]);
        return results;
    }
}
