package uk.co.pilllogger.tutorial;

import android.app.Activity;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
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
                _display.setTextTop((int)LayoutHelper.dpToPx(_activity, 125));
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
        _shownHints++;

        moveTutorialTextView(_display, true);
    }

    @Override
    public void resetPage() {
        _display.setText(_activity, R.string.pillslist_tut_0);
        _display.setVerticalTextPosition(TutorialDisplay.VerticalPosition.Top);
        _display.setHorizontalTextPosition(TutorialDisplay.HorizontalPosition.Middle);
        _display.setHorizontalArrowPosition(TutorialDisplay.HorizontalPosition.Middle);
        _display.setArrowDirection(TutorialDisplay.ArrowDirection.Up);

        _lastDisplay = new TutorialDisplay(_tutorialText, _layout, _activity);
        moveTutorialTextView(_display, false);
    }
}
