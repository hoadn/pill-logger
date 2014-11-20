package uk.co.pilllogger.animations;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import uk.co.pilllogger.R;

/**
 * Created by Nick on 07/12/13.
 */
public class FadeBackgroundPageTransformer implements ViewPager.PageTransformer {

    private static String TAG = "FadeBackgroundPageTransformer";
    private final View _background;
    private final Activity _activity;
    private final int _tabColour;
    float[] _transitionModifiers = new float[4];
    int _fadeFrom;
    int _fadeTo;
    ActionBar _actionBar;

    public FadeBackgroundPageTransformer(View background, Activity activity, int tabColour) {
        _background = background;
        _activity = activity;
        _tabColour = tabColour;
        _actionBar = _activity.getActionBar();

        initTabs();
    }

    private void initTabs(){
        setColorOfTab(0, 1);
        setColorOfTab(1, 0.25f);
        setColorOfTab(2, 0.25f);
    }

    @Override
    public void transformPage(View view, float position) {
        if(view == null)
            return;

        int colour = (Integer)view.getTag(R.id.tag_page_colour);
        int tabPosition = (Integer)view.getTag(R.id.tag_tab_icon_position);
        if(position <= 1 && position > -1){ // page is visible
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
            float a = Color.alpha(_fadeFrom) - (modifier * _transitionModifiers[3]);

            if(position > 0)
                _background.setBackgroundColor(Color.argb((int)a, (int)r, (int)g, (int)b));

            // transition tab bar icons
            if(_actionBar != null){
                float tabAlpha = 1 - (Math.abs(position) * 0.75f);

                setColorOfTab(tabPosition, tabAlpha);
            }
        }
        else {
            setColorOfTab(tabPosition, 0.25f);
        }
    }

    private void setColorOfTab(int tabPosition, float alpha){

        if(tabPosition >= _actionBar.getTabCount()){
            return;
        }

        ActionBar.Tab tab = _actionBar.getTabAt(tabPosition);
        View tabCustomView = tab.getCustomView();
        ImageView tabImage = (ImageView) tabCustomView.findViewById(R.id.tab_icon_image);
        Drawable background = tabImage.getDrawable();

        background.mutate().setColorFilter(_tabColour, PorterDuff.Mode.MULTIPLY);

        if(alpha >= 0)
            tabImage.setAlpha(alpha);
    }

    private float[] calculateColourTransition(int from, int to) {
        float[] results = new float[4];
        int[] fadeFrom = { Color.red(from), Color.green(from), Color.blue(from), Color.alpha(from)};
        int[] fadeTo = { Color.red(to), Color.green(to), Color.blue(to), Color.alpha(to)};

        for (int i = 0 ; i < fadeFrom.length ; i++) {
            float diff = fadeFrom[i] - fadeTo[i];
            results[i] = diff/100.0f;
        }
        return results;
    }
}
