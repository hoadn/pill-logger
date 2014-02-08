package uk.co.pilllogger.tutorial;

import android.animation.Animator;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 25/01/2014.
 */
public abstract class TutorialPage {
    protected final View _layout;
    protected final int _actionBarHeight;
    protected final int _tabBarHeight;
    Activity _activity;
    String _fragmentTag = "";
    int _shownHints = 0;
    int _leftMargin = 0;
    int _topMargin = 0;
    boolean _isFinished = false;
    int _animationDuration = 0;
    boolean _animate = true;
    TutorialDisplay _lastDisplay;
    TutorialDisplay _display;
    TextView _tutorialText;
    ImageView _arrow;

    public TutorialPage(Activity activity,  View layout){
        _layout = layout;
        if(activity == null)
            throw new IllegalArgumentException("activity null");

        if(layout == null)
            throw new IllegalArgumentException("layout null");

        _activity = activity;
        _tutorialText = (TextView)layout.findViewById(R.id.tutorial_text);
        _arrow = (ImageView)layout.findViewById(R.id.tutorial_arrow);
        _arrow.setVisibility(View.INVISIBLE);

        _tutorialText.setTypeface(State.getSingleton().getScriptTypeface());

        _leftMargin = _activity.getResources().getDimensionPixelSize(R.dimen.tutorial_text_left_margin);
        _topMargin = 0;// _activity.getResources().getDimensionPixelSize(R.dimen.tutorial_text_top_margin);
        _actionBarHeight = _activity.getResources().getDimensionPixelSize(R.dimen.action_bar_height);
        _tabBarHeight = _activity.getResources().getDimensionPixelSize(R.dimen.tab_bar_height);

        _lastDisplay = new TutorialDisplay(_tutorialText, _layout, _activity);
        _display = new TutorialDisplay(_tutorialText, _layout, _activity);
    }

    public View getLayout() {
        return _layout;
    }

    public TextView getTutorialText() {

        return _tutorialText;
    }

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

    protected void animateLayout(){
        ViewPropertyAnimator viewPropertyAnimator = _layout.animate();
        if(viewPropertyAnimator != null){
            viewPropertyAnimator.alpha(0f)
            .setDuration(_animationDuration)
            .setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    _layout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public abstract void nextHint();

    public abstract void resetPage();

    protected void moveTutorialTextView(final TutorialDisplay display, boolean animate) {
        _animate = animate;
        moveTutorialTextView(display);
    }

    protected void moveTutorialTextView(final TutorialDisplay display) {
        if (!_animate)
            _animationDuration = 0;
        else
            _animationDuration = _activity.getResources().getInteger(R.integer.tutorial_animation_duration);

        ViewTreeObserver vto = _tutorialText.getViewTreeObserver();
        if (vto != null) {

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    _tutorialText.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    setArrowParams(_arrow);

                    startAnimation(display);
                    _lastDisplay = display.createCopy();
                }

                private void startAnimation(TutorialDisplay display){
                    TranslateAnimation animText = new TranslateAnimation(_lastDisplay.getTextPosition(), display.getTextPosition(), _lastDisplay.getTextTopPosition(), display.getTextTopPosition());
                    TranslateAnimation animArrow = new TranslateAnimation(_lastDisplay.getArrowXPosition(), display.getArrowXPosition(), _lastDisplay.getArrowTopPosition(), display.getArrowTopPosition());
                    animText.setFillAfter(true);
                    animText.setDuration(_animationDuration);
                    animArrow.setFillAfter(true);
                    animArrow.setDuration(_animationDuration);
                    final TutorialDisplay display1 = display;
                    animArrow.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() { //Half way through animation rotate arrow - keeps rotation hidden from user
                                @Override
                                public void run() {
                                    _arrow.setRotation(display1.getArrowRotation());

                                }
                            }, _animationDuration/2);

                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    _tutorialText.startAnimation(animText);
                    _arrow.startAnimation(animArrow);
                }

                private void setArrowParams(final ImageView arrow){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) arrow.getLayoutParams();
                    if (params != null) {
                        params.setMargins(_leftMargin, _topMargin, 0, 0);
                    }
                    arrow.setLayoutParams(params);
                }

            });
        }

        _tutorialText.setText(display.getText());
    }


}
