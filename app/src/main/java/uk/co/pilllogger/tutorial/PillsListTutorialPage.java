package uk.co.pilllogger.tutorial;

import android.app.Activity;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
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


        TutorialDisplay display = new TutorialDisplay(_tutorialText, _layout, _activity);

        switch(_shownHints){ // next hint ( 0 based index )
            case 0:
                display.setText(_activity, R.string.pillslist_tut_1);
                display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Top);
                display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Middle);
                display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Middle);
                display.setArrowDirection(TutorialDisplay.ArrowDirection.Down);
                break;

            case 1:
                display.setText(_activity, R.string.pillslist_tut_2);
                display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Bottom);
                display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Middle);
                display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Middle);
                display.setArrowDirection(TutorialDisplay.ArrowDirection.Down);
                break;

            default:
                _layout.setVisibility(View.GONE);
                new SetTutorialSeenTask(_activity, ConsumptionListFragment.TAG).execute();
                _isFinished = true;
                return;
        }
        _shownHints++;

        moveTutorialTextView(display);
    }
}
