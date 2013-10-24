package uk.co.cntwo.pilllogger.fragments;

import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.PillsListAdapter;
import uk.co.cntwo.pilllogger.helpers.PillHelper;
import uk.co.cntwo.pilllogger.listeners.PillItemClickListener;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.tasks.GetPillsTask;


public class PillListFragment extends Fragment implements GetPillsTask.ITaskComplete {

    private ListView _list;
    private Typeface _openSans;

	public PillListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.pill_list_fragment, container, false);

        _list = (ListView) v.findViewById(R.id.pill_list);
        _list.setOnItemClickListener(new PillItemClickListener(getActivity()));

        _openSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");

        new GetPillsTask(getActivity(), this).execute();

        TextView addPillTitle = (TextView) v.findViewById(R.id.pill_fragment_add_pill_title);
        EditText addPillName = (EditText) v.findViewById(R.id.pill_fragment_add_pill_name);
        EditText addPillSize = (EditText) v.findViewById(R.id.pill_fragment_add_pill_size);
        addPillTitle.setTypeface(_openSans);
        addPillName.setTypeface(_openSans);
        addPillSize.setTypeface(_openSans);

        return v;
    }


	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		_list.setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

    @Override
    public void pillsReceived(List<Pill> pills) {
        // TODO: replace with a real list adapter.
        _list.setAdapter(new PillsListAdapter(getActivity(),
                R.layout.pill_list_item, pills));
    }

}
