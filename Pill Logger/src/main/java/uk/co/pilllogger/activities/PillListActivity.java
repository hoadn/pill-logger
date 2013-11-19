package uk.co.cntwo.pilllogger.activities;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.fragments.PillDetailFragment;
import uk.co.cntwo.pilllogger.fragments.PillListFragment;

import android.content.Intent;
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


	public void onItemSelected(int id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(PillDetailFragment.ARG_ITEM_ID, id);
			PillDetailFragment fragment = new PillDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.pill_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, PillDetailActivity.class);
			detailIntent.putExtra(PillDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
