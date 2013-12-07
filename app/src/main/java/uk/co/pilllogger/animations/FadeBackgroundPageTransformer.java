package uk.co.pilllogger.animations;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.Logger;

/**
 * Created by Nick on 07/12/13.
 */
public class FadeBackgroundPageTransformer implements ViewPager.PageTransformer {

    Activity _activity;
    int change, r, g, b;

    public FadeBackgroundPageTransformer(Activity activity) {
        _activity = activity;
    }

    @Override
    public void transformPage(View view, float v) {
        View colourBackground = _activity.findViewById(R.id.colour_background);
        if (v > 0 && v < 1) {
            change = (int) v * 100;
            r = (int)(255 - (v * 100));
            g = (int)(209 - (v * 100));
            b = (int)(0 + (v * 100));
            colourBackground.setBackgroundColor(Color.argb(120, r, g, b));
            Logger.v("Test", "rgb = " + r + " " + g + " " + b + "V = " + (v * 100));
        }

    }
}
