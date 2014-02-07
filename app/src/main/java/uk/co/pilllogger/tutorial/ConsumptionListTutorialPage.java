package uk.co.pilllogger.tutorial;

import android.animation.Animator;
import android.app.Activity;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.tasks.SetTutorialSeenTask;

/**
 * Created by alex on 25/01/2014.
 */
public class ConsumptionListTutorialPage extends TutorialPage {

    public ConsumptionListTutorialPage(Activity activity, View layout) {
        super(activity, layout);
    }

    @Override
    public void nextHint() {
        _shownHints++;

        switch(_shownHints){ // next hint ( 0 based index )
            case 1:
                _display.setText(_activity, R.string.consumptionlist_tut_2);
                _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Top);
                _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Right);
                _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Custom);
                _display.setIgnoreActionBar(true);
                _display.setArrowLeft(_activity.getResources().getDimensionPixelSize(R.dimen.consumptionlist_tut_1_arrow));
                _display.setArrowDirection(TutorialDisplay.ArrowDirection.Up);
                break;

            case 2:
                _display.setText(_activity, R.string.consumptionlist_tut_3);
                _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Top);
                _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Right);
                _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Custom);
                _display.setIgnoreActionBar(true);
                _display.setArrowLeft(_activity.getResources().getDimensionPixelSize(R.dimen.consumptionlist_tut_2_arrow));
                _display.setArrowDirection(TutorialDisplay.ArrowDirection.Up);
                break;

            default:
                animateLayout();
                new SetTutorialSeenTask(_activity, ConsumptionListFragment.TAG).execute();
                _isFinished = true;
                return;
        }

        moveTutorialTextView(_display, true);
    }

    @Override
    public void resetPage() {
        _display.setText(_activity, R.string.consumptionlist_tut_1);
        _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Top);
        _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Left);
        _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Custom);
        _display.setArrowLeft(_activity.getResources().getDimensionPixelSize(R.dimen.consumptionlist_tut_0_arrow));
        _display.setArrowDirection(TutorialDisplay.ArrowDirection.Up);

        _lastDisplay = new TutorialDisplay(_tutorialText, _layout, _activity);
        moveTutorialTextView(_display, false);
    }
}
