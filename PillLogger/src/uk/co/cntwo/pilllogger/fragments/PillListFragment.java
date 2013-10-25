package uk.co.cntwo.pilllogger.fragments;

import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    private EditText _addPillName;
    private EditText _addPillSize;

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
        _addPillName = (EditText) v.findViewById(R.id.pill_fragment_add_pill_name);
        _addPillSize = (EditText) v.findViewById(R.id.pill_fragment_add_pill_size);
        addPillTitle.setTypeface(_openSans);
        _addPillName.setTypeface(_openSans);
        _addPillSize.setTypeface(_openSans);

        View completed = v.findViewById(R.id.pill_fragment_add_pill_completed);
        completed.setOnClickListener(new AddPillClickListener(this));

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
        if (_list.getAdapter() == null)
            _list.setAdapter(new PillsListAdapter(getActivity(), R.layout.pill_list_item, pills));
        else
            ((PillsListAdapter)_list.getAdapter()).updateAdapter(pills);
    }

    private class AddPillClickListener implements View.OnClickListener {

        GetPillsTask.ITaskComplete _listener;
        public AddPillClickListener(GetPillsTask.ITaskComplete listener) {
            _listener = listener;
        }

        @Override
        public void onClick(View view) {
            Pill newPill = new Pill();
            newPill.setName(_addPillName.getText().toString());
            newPill.setSize(Integer.parseInt(_addPillSize.getText().toString()));
            PillHelper.addPill(getActivity(), newPill);

            new GetPillsTask(getActivity(), _listener).execute();
            _addPillName.setText("");
            _addPillSize.setText("");
            _addPillSize.clearFocus();

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
