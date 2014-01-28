package uk.co.pilllogger.tutorial;

import android.app.Activity;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
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

        TutorialDisplay display = new TutorialDisplay(_tutorialText, _layout, _activity);

        switch(_shownHints){ // next hint ( 0 based index )
            case 1:
                display.setText(_activity, R.string.consumptionlist_tut_2);
                display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Bottom);
                display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Left);
                display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Left);
                display.setArrowDirection(TutorialDisplay.ArrowDirection.Down);
                break;

            case 2:
                display.setText(_activity, R.string.consumptionlist_tut_3);
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

        moveTutorialTextView(display);
    }
}
