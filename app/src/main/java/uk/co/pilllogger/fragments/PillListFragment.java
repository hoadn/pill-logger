package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.PillsListAdapter;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.tasks.InsertPillTask;


public class PillListFragment extends Fragment implements GetPillsTask.ITaskComplete, InsertPillTask.ITaskComplete {
    private String TAG = "PillListFragment";
    private ListView _list;
    private Typeface _openSans;
    private EditText _addPillName;
    private EditText _addPillSize;
    private Spinner _unitSpinner;

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
        v.setTag(R.id.tag_page_colour, Color.argb(120, 161, 72, 173));
        v.setTag(R.id.tag_tab_icon_position, 1);

        _list = (ListView) v.findViewById(R.id.pill_list);
        //_list.setOnItemClickListener(new PillItemClickListener(getActivity()));

        _openSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");

        new GetPillsTask(getActivity(), this).execute();

        TextView addPillTitle = (TextView) v.findViewById(R.id.pill_fragment_add_pill_title);
        _addPillName = (EditText) v.findViewById(R.id.pill_fragment_add_pill_name);
        _addPillSize = (EditText) v.findViewById(R.id.pill_fragment_add_pill_size);
        addPillTitle.setTypeface(_openSans);
        _addPillName.setTypeface(_openSans);
        _addPillSize.setTypeface(_openSans);

        View completed = v.findViewById(R.id.pill_fragment_add_pill_completed);
        completed.setOnClickListener(new AddPillClickListener(this, this));

        _addPillSize.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            completed();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        _addPillSize.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    completed();
                    handled = true;
                }
                return handled;
            }
        });

        _unitSpinner = (Spinner) v.findViewById(R.id.units_spinner);
        String[] units = { "mg", "ml" };
        UnitAdapter adapter = new UnitAdapter(getActivity(), android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _unitSpinner.setAdapter(adapter);
        return v;
    }


	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
//		_list.setChoiceMode(
//				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
//						: ListView.CHOICE_MODE_NONE);
	}

    @Override
    public void pillsReceived(List<Pill> pills) {
        if (_list.getAdapter() == null){ //we need to init the adapter
            PillsListAdapter adapter = new PillsListAdapter(getActivity(), R.layout.pill_list_item, pills);

           // ContextualUndoAdapter undoAdapter = new ContextualUndoAdapter(adapter, R.layout.pill_list_item_delete, R.id.pill_list_undo);
            //undoAdapter.setAbsListView(_list);
            _list.setAdapter(adapter);

            //undoAdapter.setDeleteItemCallback(this);
        }
        else
        {
            PillsListAdapter adapter = (PillsListAdapter)_list.getAdapter();
           adapter.updateAdapter(pills);
        }
    }

//    @Override
//    public void deleteItem(int i) {
//        PillsListAdapter adapter = ((PillsListAdapter)((ContextualUndoAdapter)_list.getAdapter()).getDecoratedBaseAdapter());
//        Pill p = adapter.getPillAtPosition(i);
//        adapter.removeAtPosition(i);
//
//        new DeletePillTask(getActivity(), p).execute();
//    }

    @Override
    public void pillInserted(Pill pill) {
        new GetPillsTask(getActivity(), this).execute();
        _addPillName.setText("");
        _addPillSize.setText("");
        _addPillSize.clearFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private class AddPillClickListener implements View.OnClickListener {

        GetPillsTask.ITaskComplete _listener;
        PillListFragment _fragment;
        public AddPillClickListener(GetPillsTask.ITaskComplete listener, PillListFragment fragment) {
            _listener = listener;
            _fragment = fragment;
        }

        @Override
        public void onClick(View view) {
            _fragment.completed();
        }
    }

    public void completed() {
        if (!_addPillName.getText().toString().equals("")) {
            Pill newPill = new Pill();
            String pillName = _addPillName.getText().toString();
            String units = _unitSpinner.getSelectedItem().toString();
            newPill.setUnits(units);
            newPill.setName(pillName);

            int pillSize = 0;
            if (!_addPillSize.getText().toString().matches("")) {
                pillSize = Integer.parseInt(_addPillSize.getText().toString());
            }
            newPill.setSize(pillSize);

            new InsertPillTask(getActivity(), newPill, this).execute();
        }
    }

}
