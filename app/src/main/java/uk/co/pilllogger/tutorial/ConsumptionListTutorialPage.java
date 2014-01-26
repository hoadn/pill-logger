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

        int textTop = _tutorialText.getTop();
        int bottom = _layout.getHeight();
        int textTopTo;
        int textLeftTo;
        int arrowLeftTo;
        int textResId;
        ArrowDirection arrowDirection;

        switch(_shownHints){ // next hint ( 0 based index )
            case 1:
                textTopTo = bottom - textTop - _tutorialText.getHeight() - _actionBarHeight - 40;
                textLeftTo = 0;
                arrowLeftTo = 0;

                textResId = R.string.consumptionlist_tut_2;
                arrowDirection = ArrowDirection.Down;
                break;

            case 2:
                textTopTo = bottom - textTop - _tutorialText.getHeight() - _actionBarHeight - 40;
                textLeftTo = 200;
                arrowLeftTo = 420;

                textResId = R.string.consumptionlist_tut_3;
                arrowDirection = ArrowDirection.Down;
                break;

            default:
                _layout.setVisibility(View.GONE);
                new SetTutorialSeenTask(_activity, ConsumptionListFragment.TAG).execute();
                _isFinished = true;
                return;
        }

        moveTutorialTextView(textResId, textTopTo, textLeftTo, arrowLeftTo, arrowDirection);
    }
}
