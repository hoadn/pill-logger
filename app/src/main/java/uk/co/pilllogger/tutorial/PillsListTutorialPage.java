package uk.co.pilllogger.tutorial;

import android.animation.Animator;
import android.app.Activity;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.tasks.SetTutorialSeenTask;

/**
 * Created by nick on 29/01/14.
 */
public class PillsListTutorialPage extends TutorialPage {

    public PillsListTutorialPage(Activity activity, View layout) {
        super(activity, layout);
    }

    @Override
    public void nextHint() {
        switch(_shownHints){ // next hint ( 0 based index )
            case 0:
                _display.setText(_activity, R.string.pillslist_tut_1);
                _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Top);
                _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Middle);
                _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Middle);
                _display.setArrowDirection(TutorialDisplay.ArrowDirection.Down);
                break;

            case 1:
                _display.setText(_activity, R.string.pillslist_tut_2);
                _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Custom);
                _display.setTextTop(_activity.getResources().getDimensionPixelSize(R.dimen.pillslist_tut_1_texttop));
                _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Middle);
                _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Middle);
                _display.setArrowDirection(TutorialDisplay.ArrowDirection.Down);
                break;

            default:
                animateLayout();

                new SetTutorialSeenTask(_activity, PillListFragment.TAG).execute();
                _isFinished = true;
                return;
        }
        _shownHints++;

        moveTutorialTextView(_display, true);
    }

    @Override
    public void resetPage() {
        _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Custom);
        _display.setTextTop(0);
        _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Custom);
        _display.setTextLeft(0);
        _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Custom);
        _display.setArrowLeft(0);
        _lastDisplay = _display.createCopy();
        moveTutorialTextView(_display, false);

        _display.setText(_activity, R.string.pillslist_tut_0);
        _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Top);
        if(_tabBarHeight > 0){
            _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Middle);
            _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Middle);
        }
        else{
            _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Left);
            _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Custom);
            _display.setArrowLeft(_activity.getResources().getDimensionPixelSize(R.dimen.pillslist_tut_0_arrow_left));
        }
        _display.setArrowDirection(TutorialDisplay.ArrowDirection.Up);

        moveTutorialTextView(_display, false);
    }
}
