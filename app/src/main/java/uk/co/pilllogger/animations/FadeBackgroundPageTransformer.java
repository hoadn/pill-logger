package uk.co.pilllogger.animations;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.SlidePagerAdapter;
import uk.co.pilllogger.helpers.Logger;

/**
 * Created by Nick on 07/12/13.
 */
public class FadeBackgroundPageTransformer implements ViewPager.PageTransformer {

    private static String TAG = "FadeBackgroundPageTransformer";
    private final View _background;
    private final Activity _activity;
    float[] _transitionModifiers = new float[3];
    int _fadeFrom;
    int _fadeTo;
    ActionBar _actionBar;

    public FadeBackgroundPageTransformer(View background, Activity activity) {
        _background = background;
        _activity = activity;
        _actionBar = _activity.getActionBar();
    }

    @Override
    public void transformPage(View view, float position) {
        if(view == null)
            return;

        if(position <= 1 && position > -1){ // page is visible
            int colour = (Integer)view.getTag(R.id.tag_page_colour);
            int tabPosition = (Integer)view.getTag(R.id.tag_tab_icon_position);

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

            // transition tab bar icons
            if(_actionBar != null){
                float tabAlpha = ((1 - Math.abs(position)) / 2) + 0.5f;
                ActionBar.Tab tab = _actionBar.getTabAt(tabPosition);
                View tabCustomView = tab.getCustomView();
                View tabImage = tabCustomView.findViewById(R.id.tab_icon_image);
                tabImage.setAlpha(tabAlpha);
            }
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
