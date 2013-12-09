package uk.co.pilllogger.animations;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.Logger;

/**
 * Created by Nick on 07/12/13.
 */
public class FadeBackgroundPageTransformer implements ViewPager.PageTransformer {

    Activity _activity;
    int change;
    float r = 255;
    float g = 209;
    float b = 0;
    int[] colour1 = {255, 209, 0};
    int[] colour2 = {123, 224, 255};
    float[] _results;

    public FadeBackgroundPageTransformer(Activity activity) {
        _activity = activity;
        _results = calculateColourTransition();
    }

    @Override
    public void transformPage(View view, float v) {
        View colourBackground = _activity.findViewById(R.id.colour_background);
        if (v >= 0 && v <= 1) {
            change = (int) v * 100;
            r = colour1[0] - ((100-(v*100)) * _results[0]);
            g = colour1[1] - ((100-(v*100)) * _results[1]);
            b = colour1[2] - ((100-(v*100)) * _results[2]);
            colourBackground.setBackgroundColor(Color.argb(120, (int)r, (int)g, (int)b));
            Logger.v("Test", "rgb = " + r + " " + g + " " + b + "V = " + (v * 100));
        }
    }

    private float[] calculateColourTransition() {
        float[] results = new float[3];
        for (int i = 0 ; i < colour1.length ; i++) {
            float diff = colour1[i] - colour2[i];
            results[i] = diff/100;
        }
        Logger.v("Test", "Results: " + results[0] + " " + results[1] + " " + results[2]);
        return results;
    }
}
