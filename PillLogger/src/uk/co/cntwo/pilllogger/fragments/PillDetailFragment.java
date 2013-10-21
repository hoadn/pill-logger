package uk.co.cntwo.pilllogger.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.activities.PillDetailActivity;
import uk.co.cntwo.pilllogger.activities.PillListActivity;
import uk.co.cntwo.pilllogger.helpers.PillHelper;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * A fragment representing a single Pills detail screen. This fragment is either
 * contained in a {@link PillListActivity} in two-pane mode (on tablets) or a
 * {@link PillDetailActivity} on handsets.
 */
public class PillDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Pill _item;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PillDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			_item = PillHelper.getPillMap(getActivity()).get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_pill_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (_item != null) {
			((TextView) rootView.findViewById(R.id.pill_detail))
					.setText(_item.getName());
		}

		return rootView;
	}
}
