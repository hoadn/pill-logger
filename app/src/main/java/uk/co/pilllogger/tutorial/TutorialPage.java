package uk.co.pilllogger.tutorial;

import android.content.Context;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.Logger;

/**
 * Created by alex on 25/01/2014.
 */
public abstract class TutorialPage {
    Context _context;
    String _fragmentTag = "";
    int _totalHints = 0;
    int _shownHints = 0;

    public String getFragmentTag() {
        return _fragmentTag;
    }

    public void setFragmentTag(String tag) {
        _fragmentTag = tag;
    }

    public int getTotalHints() {
        return _totalHints;
    }

    public void setTotalHints(int totalHints) {
        _totalHints = totalHints;
    }

    public int getShownHints() {
        return _shownHints;
    }

    public void setShownHints(int shownHints) {
        _shownHints = shownHints;
    }

    public boolean hasHintsToShow(){
        return (_shownHints < _totalHints);
    }

    public TutorialPage(Context context){
        _context = context;
    }

    public abstract void nextHint(View layout);

    protected void moveTutorialTextView(int startX, int finishX, int startY, int finishY, TextView textView, ImageView arrow, int tutorialTextHeight) {
        Logger.v("Testing", "move = " + tutorialTextHeight);
        arrow.setRotation(180);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) arrow.getLayoutParams();
        if (params != null) {
            params.setMargins((int) LayoutHelper.dpToPx(_context, 11), (int)LayoutHelper.dpToPx(_context, 60), 0, 0);
        }
        arrow.setLayoutParams(params);
        TranslateAnimation animText = new TranslateAnimation(0, 0, 0, finishY);
        TranslateAnimation animArrow = new TranslateAnimation(0, 0, 0, (finishY + tutorialTextHeight - (int)LayoutHelper.dpToPx(_context, 10)));
        animText.setFillAfter(true);
        animText.setDuration(350);
        animArrow.setFillAfter(true);
        animArrow.setDuration(350);

        textView.startAnimation(animText);
        arrow.startAnimation(animArrow);
    }
}
