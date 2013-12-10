package uk.co.pilllogger.animations;

import android.app.Fragment;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.MainActivity;
import uk.co.pilllogger.adapters.SlidePagerAdapter;
import uk.co.pilllogger.helpers.Logger;

/**
 * Created by Nick on 07/12/13.
 */
public class FadeBackgroundPageTransformer implements ViewPager.PageTransformer {

    private static String TAG = "FadeBackgroundPageTransformer";
    private final View _background;
    private final ViewPager _pager;
    float _previousV = 0;
    int[] _colours;
    boolean _goingForwards = true;

    public FadeBackgroundPageTransformer(View background, ViewPager pager, int[] colours) {
        _background = background;
        _pager = pager;
        _colours = colours;
    }

    @Override
    public void transformPage(View view, float v) {
        Logger.v(TAG, "Value = " + v);
        if (_previousV == 1.0 && v == 0.0)
            return;

        int pagePosition = _pager.getCurrentItem();
        Fragment fragment = ((SlidePagerAdapter)_pager.getAdapter()).getItem(pagePosition);
        boolean correctPage = _pager.getAdapter().isViewFromObject(view, fragment);

        if(pagePosition < 0 || !correctPage)
            return;

        int fadeFrom = _colours[pagePosition];

        float[] transitionModifiers = new float[3];

        if (v > _previousV && pagePosition > 0) {
            int fadeToPrevious = _colours[pagePosition - 1];
            transitionModifiers = calculateColourTransition(fadeFrom, fadeToPrevious);
            _goingForwards = false;
            Logger.v(TAG, "Going Backwards");
        }
        else if (v <=_previousV && (pagePosition < _colours.length - 1)) {
            int fadeToNext = _colours[pagePosition + 1];
            transitionModifiers = calculateColourTransition(fadeFrom, fadeToNext);
            _goingForwards = true;
            Logger.v(TAG, "rgb Going Forwards");
        }
        if (v >= 0 && v <= 1) {

            float modifier = _goingForwards ? (100 - (v * 100)) : (v * 100);

            float r = Color.red(fadeFrom) - (modifier * transitionModifiers[0]);
            float g = Color.green(fadeFrom) - (modifier * transitionModifiers[1]);
            float b = Color.blue(fadeFrom) - (modifier * transitionModifiers[2]);

            _background.setBackgroundColor(Color.argb(120, (int)r, (int)g, (int)b));
            Logger.v(TAG, "rgb = " + r + " " + g + " " + b + " V = " + (v * 100) + " previousV = " + _previousV + " v = " + v);
            _previousV = v;
        }
        _previousV = v;

    }

    private float[] calculateColourTransition(int from, int to) {
        float[] results = new float[3];
        int[] fadeFrom = { Color.red(from), Color.green(from), Color.blue(from)};
        int[] fadeTo = { Color.red(to), Color.green(to), Color.blue(to)};

        for (int i = 0 ; i < fadeFrom.length ; i++) {
            float diff = fadeFrom[i] - fadeTo[i];
            results[i] = diff/100;
        }
        if (results[0] == 0.0 && results[1] == 0.0 && results[2] == 0.0)
            results = calculateColourTransition(to, from); // TODO: HACK!!
        Logger.v(TAG, "Results: " + results[0] + " " + results[1] + " " + results[2]);
        return results;
    }
}
