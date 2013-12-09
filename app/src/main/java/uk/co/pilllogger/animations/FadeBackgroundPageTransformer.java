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
    int[] _fadeFrom;
    int[] _fadeToForward;
    int[] _fadeToBackward;
    float[] _results;
    View _colourBackground;
    boolean _goingForwards = true;

    public FadeBackgroundPageTransformer(MainActivity activity) {
        _activity = activity;
        _colourBackground = _activity.findViewById(R.id.colour_background);
        _fadeFrom = _activity.getColour1();
        _fadeToForward = _activity.getColour2();
        _fadeToBackward = _activity.getColour3();
        _results = calculateColourTransition(_fadeFrom, _fadeToForward);
    }

    @Override
    public void transformPage(View view, float v) {
        if (_previousV == 1.0 && v == 0.0)
            return;

        if ((_previousV == -1) && (((v*100) < 5) && (v >=0 && v <= 1))) {
            _results = calculateColourTransition(_fadeFrom, _fadeToBackward);
            _goingForwards = false;
            Logger.v("Test", "Going Backwards");
        }
        else if ((_previousV == -1) && (((v*100) >= 5) && (v >=0 && v <= 1))) {
            _results = calculateColourTransition(_fadeFrom, _fadeToForward);
            _goingForwards = true;
            Logger.v("Test", "Going Forwards");
        }
        if (v >= 0 && v <= 1) {
            if (_goingForwards) {
                r = _fadeFrom[0] - ((100-(v*100)) * _results[0]);
                g = _fadeFrom[1] - ((100-(v*100)) * _results[1]);
                b = _fadeFrom[2] - ((100-(v*100)) * _results[2]);
            }
            else {
                r = _fadeFrom[0] - ((v*100) * _results[0]);
                g = _fadeFrom[1] - ((v*100) * _results[1]);
                b = _fadeFrom[2] - ((v*100) * _results[2]);
            }
            _colourBackground.setBackgroundColor(Color.argb(120, (int)r, (int)g, (int)b));
            _previousV = v;
            Logger.v("Test", "rgb = " + r + " " + g + " " + b + " V = " + (v * 100) + " previousV = " + _previousV + " v = " + v);
        }
        if (v == 0 || v == 1) {
            switch (_activity.getPageNumber()) {
                case 0:
                    _fadeFrom = _activity.getColour1();
                    _fadeToForward = _activity.getColour2();
                    Logger.v("Test", "rgb CHANGED COLOURS TO 0");
                    break;
                case 1:
                    _fadeFrom = _activity.getColour2();
                    _fadeToForward = _activity.getColour3();
                    _fadeToBackward = _activity.getColour1();
                    Logger.v("Test", "rgb CHANGED COLOURS TO 1");
                    break;
                case 2:
                    _fadeFrom = _activity.getColour3();
                    _fadeToBackward = _activity.getColour2();
                    Logger.v("Test", "rgb CHANGED COLOURS TO 2");
                    break;
            }

            _previousV = -1;
        }

    }

    private float[] calculateColourTransition(int [] fadeFrom, int[] fadeTo) {
        float[] results = new float[3];
        for (int i = 0 ; i < fadeFrom.length ; i++) {
            float diff = fadeFrom[i] - fadeTo[i];
            results[i] = diff/100;
        }
        Logger.v("Test", "Results: " + results[0] + " " + results[1] + " " + results[2]);
        return results;
    }
}
