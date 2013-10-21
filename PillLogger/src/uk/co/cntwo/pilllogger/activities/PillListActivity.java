package uk.co.cntwo.pilllogger.activities;

import java.util.List;
import java.util.UUID;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.fragments.PillDetailFragment;
import uk.co.cntwo.pilllogger.fragments.PillListFragment;
import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.models.Pill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * An activity representing a list of Pills. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link PillDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PillListFragment} and the item details (if present) is a
 * {@link PillDetailFragment}.
 * <p>
 * This activity also implements the required {@link PillListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class PillListActivity extends FragmentActivity implements
		PillListFragment.Callbacks {

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
			((PillListFragment) getSupportFragmentManager().findFragmentById(
					R.id.pill_list)).setActivateOnItemClick(true);
		}

        Logger.v("TestLogger", "Before opening database");
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Pill> pills = dbHelper.getAllPills();
        Logger.v("TestLogger", "Before opening database, pill: " + pills.get(0).getName());

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link PillListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(PillDetailFragment.ARG_ITEM_ID, String.valueOf(id));
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
