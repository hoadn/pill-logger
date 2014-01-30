package uk.co.pilllogger.tutorial;

import android.app.Activity;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
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
                _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Bottom);
                _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Left);
                _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Left);
                _display.setArrowDirection(TutorialDisplay.ArrowDirection.Down);
                break;

            case 2:
                _display.setText(_activity, R.string.consumptionlist_tut_3);
                _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Bottom);
                _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Middle);
                _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Middle);
                _display.setArrowDirection(TutorialDisplay.ArrowDirection.Down);
                break;

            default:
                _layout.setVisibility(View.GONE);
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
        _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Left);
        _display.setArrowDirection(TutorialDisplay.ArrowDirection.Up);
        moveTutorialTextView(_display, false);
    }
}
