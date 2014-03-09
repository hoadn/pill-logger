package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.PillsListAdapter;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.tasks.InsertPillTask;
import uk.co.pilllogger.views.ColourIndicator;


public class PillListFragment extends PillLoggerFragmentBase implements
        GetPillsTask.ITaskComplete,
        InsertPillTask.ITaskComplete,
        Observer.IPillsUpdated{

    public static final String TAG = "PillListFragment";
    private ListView _list;
    private EditText _addPillName;
    private EditText _addPillSize;
    private Spinner _unitSpinner;
    ColourIndicator _colour;

	public PillListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    public void onStart(){
        super.onStart();

        Observer.getSingleton().registerPillsUpdatedObserver(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Observer.getSingleton().unregisterPillsUpdatedObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.pill_list_fragment, container, false);
        v.setTag(R.id.tag_page_colour, Color.argb(120, 204, 51, 153));
        v.setTag(R.id.tag_tab_icon_position, 1);

        _list = (ListView) v.findViewById(R.id.pill_list);
        //_list.setOnItemClickListener(new PillItemClickListener(getActivity()));

        new GetPillsTask(getActivity(), this).execute();

        Typeface typeface = State.getSingleton().getTypeface();

        TextView addPillTitle = (TextView) v.findViewById(R.id.pill_fragment_add_pill_title);
        _addPillName = (EditText) v.findViewById(R.id.pill_fragment_add_pill_name);
        _addPillSize = (EditText) v.findViewById(R.id.pill_fragment_add_pill_size);

        TextView title = (TextView)v.findViewById(R.id.pill_fragment_title);
        TextView colourText = (TextView)v.findViewById(R.id.pill_fragment_add_pill_colour);
        TextView create = (TextView)v.findViewById(R.id.pill_fragment_add_pill_create);
        addPillTitle.setTypeface(typeface);
        _addPillName.setTypeface(typeface);
        _addPillSize.setTypeface(typeface);
        title.setTypeface(typeface);
        colourText.setTypeface(typeface);
        create.setTypeface(typeface);

        _colour = (ColourIndicator)v.findViewById(R.id.pill_fragment_colour);
        final View mainView = v;
        _colour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View colourHolder = mainView.findViewById(R.id.pill_fragment_colour_picker_container);
                final ViewGroup colourContainer = (ViewGroup) colourHolder.findViewById(R.id.colour_container);
                if (colourHolder.getVisibility() == View.VISIBLE) {
                    int colourCount = colourContainer.getChildCount();
                    for (int i = 0; i < colourCount; i++) {
                        View colourView = colourContainer.getChildAt(i);
                        if (colourView != null) {
                            colourView.setOnClickListener(null);
                        }
                    }
                    colourHolder.setVisibility(View.GONE);
                } else {
                    colourHolder.setVisibility(View.VISIBLE);
                    int colourCount = colourContainer.getChildCount();
                    for (int i = 0; i < colourCount; i++) {
                        View colourView = colourContainer.getChildAt(i);
                        if (colourView != null) {
                            colourView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int colour = ((ColourIndicator) view).getColour();
                                    _colour.setColour(colour);
                                    colourHolder.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
            }
        });

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
            Activity activity = getActivity();

            if(activity == null) // it's not gonna work without this
                return;

            PillsListAdapter adapter = new PillsListAdapter(activity, R.layout.pill_list_item, pills);

            _list.setAdapter(adapter);
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

        LayoutHelper.hideKeyboard(getActivity());
    }

    @Override
    public void pillsUpdated(Pill pill) {
        new GetPillsTask(this.getActivity(), this).execute();
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
            newPill.setColour(_colour.getColour());

            int pillSize = 0;
            if (!_addPillSize.getText().toString().matches("")) {
                pillSize = Integer.parseInt(_addPillSize.getText().toString());
            }
            newPill.setSize(pillSize);

            new InsertPillTask(getActivity(), newPill, this).execute();

            TrackerHelper.createPillEvent(getActivity(), TAG);
        }
    }

}
