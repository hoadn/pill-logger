package uk.co.pilllogger.animations;

import android.app.Fragment;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import uk.co.pilllogger.adapters.SlidePagerAdapter;
import uk.co.pilllogger.helpers.Logger;

/**
 * Created by Nick on 07/12/13.
 */
public class FadeBackgroundPageTransformer implements ViewPager.PageTransformer {

    private static String TAG = "FadeBackgroundPageTransformer";
    private final View _background;
    float[] _transitionModifiers = new float[3];
    int _fadeFrom;
    int _fadeTo;

    public FadeBackgroundPageTransformer(View background) {
        _background = background;
    }

    @Override
    public void transformPage(View view, float position) {

        if(position <= 1 && position > -1){ // page is visible
            int colour = (Integer)view.getTag();
            if(position < 0)
                _fadeTo = colour;
            else
                _fadeFrom = colour;

            if(position == 0) // if page takes up full screen, both colours are equal
                _fadeTo = colour;

            _transitionModifiers = calculateColourTransition(_fadeFrom, _fadeTo);

            float modifier = position * 100;

            float r = Color.red(_fadeFrom) - (modifier * _transitionModifiers[0]);
            float g = Color.green(_fadeFrom) - (modifier * _transitionModifiers[1]);
            float b = Color.blue(_fadeFrom) - (modifier * _transitionModifiers[2]);

            if(position > 0)
                _background.setBackgroundColor(Color.argb(120, (int)r, (int)g, (int)b));
        }
    }

    private float[] calculateColourTransition(int from, int to) {
        float[] results = new float[3];
        int[] fadeFrom = { Color.red(from), Color.green(from), Color.blue(from)};
        int[] fadeTo = { Color.red(to), Color.green(to), Color.blue(to)};

        for (int i = 0 ; i < fadeFrom.length ; i++) {
            float diff = fadeFrom[i] - fadeTo[i];
            results[i] = diff/100.0f;
        }
        return results;
    }
}
