package uk.co.pilllogger.tutorial;

import android.app.Activity;
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

    int _lastTextLeft = 0;
    int _lastTextTop = 0;
    int _lastArrowLeft = 0;
    int _lastArrowTop = 0;

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
    }

    public abstract void nextHint();

    protected void moveTutorialTextView(int newTextId, int top, int left, int arrowLeft, ArrowDirection arrowDirection){
        if(_activity == null)
            return;
        moveTutorialTextView(_activity.getString(newTextId), top, left, arrowLeft, arrowDirection);
    }

    protected void moveTutorialTextView(String newText, final int textTop, final int textLeft, final int arrowLeft, final ArrowDirection arrowDirection) {

        final ImageView arrow = _arrow;


        ViewTreeObserver vto = _tutorialText.getViewTreeObserver();
        if (vto != null) {

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    _tutorialText.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) _tutorialText.getLayoutParams();

                    final int tutTextHeight = _tutorialText.getHeight();
                    float tutorialTextX = _tutorialText.getLeft();
                    final float tutorialTextY = _tutorialText.getTop();

                    float arrowX = arrow.getLeft();
                    float arrowY = arrow.getTop();

                    Logger.v("Testing", "move = " + tutTextHeight);

                    int rotation = getRotationFromDirection(arrowDirection);
                    arrow.setRotation(rotation);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) arrow.getLayoutParams();
                    if (params != null) {
                        params.setMargins(_leftMargin, _topMargin, 0, 0);
                    }
                    arrow.setLayoutParams(params);
                    TranslateAnimation animText = new TranslateAnimation(_lastTextLeft, textLeft, _lastTextTop, textTop);

                    // TODO: unknown height modifier... what is that 10dp? Arrow height?
                    final int modifier = (int)LayoutHelper.dpToPx(_activity, 10);

                    final int bottomOfText = (textTop + tutTextHeight - modifier);
                    final int arrowTop = arrowDirection == ArrowDirection.Down ? bottomOfText : textTop - (arrow.getHeight() + modifier);

                    TranslateAnimation animArrow = new TranslateAnimation(_lastArrowLeft, arrowLeft, _lastArrowTop, arrowTop);
                    animText.setFillAfter(true);
                    animText.setDuration(350);
                    animArrow.setFillAfter(true);
                    animArrow.setDuration(350);

                    _tutorialText.startAnimation(animText);
                    arrow.startAnimation(animArrow);

                    _lastTextLeft = textLeft;
                    _lastTextTop = textTop;
                    _lastArrowLeft = arrowLeft;
                    _lastArrowTop = arrowTop;
                }
            });
        }

        _tutorialText.setText(newText);
    }

    private int getRotationFromDirection(ArrowDirection direction){
        int rotation = 0;
        switch(direction){
            case Up:
                rotation = 0;
                break;

            case Right:
                rotation = 90;
                break;

            case Down:
                rotation = 180;
                break;

            case Left:
                rotation = 270;
                break;
        }

        return rotation;
    }

    protected enum ArrowDirection{
        Up,
        Right,
        Down,
        Left
    }
}
