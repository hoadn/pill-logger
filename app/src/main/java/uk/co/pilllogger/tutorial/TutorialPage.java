package uk.co.pilllogger.tutorial;

import android.app.Activity;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 25/01/2014.
 */
public abstract class TutorialPage {
    protected final View _layout;
    protected final int _actionBarHeight;
    Activity _activity;
    String _fragmentTag = "";
    int _shownHints = 0;
    int _leftMargin = 0;
    int _topMargin = 0;
    boolean _isFinished = false;
    int _animationDuration = 0;

    TutorialDisplay _lastDisplay;

    public View getLayout() {
        return _layout;
    }

    public TextView getTutorialText() {

        return _tutorialText;
    }

    TextView _tutorialText;
    ImageView _arrow;

    public String getFragmentTag() {
        return _fragmentTag;
    }

    public void setFragmentTag(String tag) {
        _fragmentTag = tag;
    }

    public int getShownHints() {
        return _shownHints;
    }

    public void setShownHints(int shownHints) {
        _shownHints = shownHints;
    }

    public boolean isFinished(){ return _isFinished;}

    public TutorialPage(Activity activity,  View layout){
        _layout = layout;
        if(activity == null)
            throw new IllegalArgumentException("activity null");

        if(layout == null)
            throw new IllegalArgumentException("layout null");

        _activity = activity;
        _tutorialText = (TextView)layout.findViewById(R.id.tutorial_text);
        _arrow = (ImageView)layout.findViewById(R.id.tutorial_arrow);

        _tutorialText.setTypeface(State.getSingleton().getTypeface());

        _leftMargin = _activity.getResources().getDimensionPixelSize(R.dimen.tutorial_text_left_margin);
        _topMargin = _activity.getResources().getDimensionPixelSize(R.dimen.tutorial_text_top_margin);
        _actionBarHeight = _activity.getResources().getDimensionPixelSize(R.dimen.action_bar_height);

        _animationDuration = _activity.getResources().getInteger(R.integer.tutorial_animation_duration);

        _lastDisplay = new TutorialDisplay(_tutorialText, _layout, _activity);
    }

    public abstract void nextHint();

    protected void moveTutorialTextView(final TutorialDisplay display) {

        final ImageView arrow = _arrow;

        ViewTreeObserver vto = _tutorialText.getViewTreeObserver();
        if (vto != null) {

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    _tutorialText.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    arrow.setRotation(display.getArrowRotation());
                    setArrowParams(arrow);

                    startAnimation(display);

                    _lastDisplay = display.createCopy();
                }

                private void setArrowParams(final ImageView arrow){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) arrow.getLayoutParams();
                    if (params != null) {
                        params.setMargins(_leftMargin, _topMargin, 0, 0);
                    }
                    arrow.setLayoutParams(params);
                }

                private void startAnimation(TutorialDisplay display){
                    TranslateAnimation animText = new TranslateAnimation(_lastDisplay.getTextLeftPosition(), display.getTextLeftPosition(), _lastDisplay.getTextTopPosition(), display.getTextTopPosition());
                    TranslateAnimation animArrow = new TranslateAnimation(_lastDisplay.getArrowLeftPosition(), display.getArrowLeftPosition(), _lastDisplay.getArrowTopPosition(), display.getArrowTopPosition());
                    animText.setFillAfter(true);
                    animText.setDuration(_animationDuration);
                    animArrow.setFillAfter(true);
                    animArrow.setDuration(_animationDuration);

                    _tutorialText.startAnimation(animText);
                    arrow.startAnimation(animArrow);
                }
            });
        }

        _tutorialText.setText(display.getText());
    }
}
