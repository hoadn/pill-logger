package uk.co.pilllogger.tutorial;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.tasks.SetTutorialSeenTask;

/**
 * Created by alex on 25/01/2014.
 */
public class ConsumptionListTutorialPage extends TutorialPage {

    public ConsumptionListTutorialPage(Context context) {
        super(context);

        _totalHints = 2;
    }

    @Override
    public void nextHint(View layout) {
        _shownHints++;
        if (_shownHints > 1) {
            layout.setVisibility(View.GONE);
            new SetTutorialSeenTask(_context, ConsumptionListFragment.TAG).execute();
            return;
        }
        final TextView tutText = (TextView)layout.findViewById(R.id.tutorial_text);
        tutText.setText(_context.getString(R.string.consumptionlist_tut_2));
        final View view = layout;
        ViewTreeObserver vto = tutText.getViewTreeObserver();
        if (vto != null) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    int bottom = view.getHeight();
                    int textTop = tutText.getTop();
                    int actionBarHeight = (int) LayoutHelper.dpToPx(_context, 48);
                    int move = bottom - textTop - tutText.getHeight() - actionBarHeight - 40;
                    ImageView tutArrow = (ImageView)view.findViewById(R.id.tutorial_arrow);

                    moveTutorialTextView(0, 0, 0, move, tutText, tutArrow, tutText.getHeight());

                    tutText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }
}
