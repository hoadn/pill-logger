package uk.co.pilllogger.activities;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.PillListFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class PillListActivity extends FragmentActivity {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pill_list);

		if (findViewById(R.id.pill_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((PillListFragment) getFragmentManager().findFragmentById(
					R.id.pill_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}
}
