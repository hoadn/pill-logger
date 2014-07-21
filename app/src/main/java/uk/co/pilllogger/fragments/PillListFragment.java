package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.List;

import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.PillsListAdapter;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.tasks.InsertPillTask;
import uk.co.pilllogger.views.ColourIndicator;


public class PillListFragment extends PillLoggerFragmentBase implements
        InsertPillTask.ITaskComplete,
        SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = "PillListFragment";
    private ListView _list;
    private EditText _addPillName;
    private EditText _addPillSize;
    private Spinner _unitSpinner;
    ColourIndicator _colour;

    @DebugLog
	public PillListFragment() {
	}

    @Override
    public void onStart(){
        super.onStart();
        Timber.d("onStart");
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
        Activity activity = getActivity();
        if(activity == null || v == null){
            return null;
        }
        int color = activity.getResources().getColor(State.getSingleton().getTheme().getPillListBackgroundResourceId());
        v.setTag(R.id.tag_page_colour, color);
        v.setTag(R.id.tag_tab_icon_position, 1);

        _list = (ListView) v.findViewById(R.id.pill_list);
        //_list.setOnItemClickListener(new PillItemClickListener(getActivity()));

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
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completed();
            }
        });

        _addPillSize.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
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
        _addPillSize.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        String[] units = activity.getResources().getStringArray(R.array.units_array);
        UnitAdapter adapter = new UnitAdapter(activity, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _unitSpinner.setAdapter(adapter);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);

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

    @Subscribe
    public void pillsLoaded(LoadedPillsEvent event) {
        updatePills(event.getPills());
    }

    private void updatePills(List<Pill> pills){
        if(_list == null || pills.size() == 0)
            return;

        if (_list.getAdapter() == null){ //we need to init the adapter
            Activity activity = getActivity();

            if(activity == null) // it's not gonna work without this
                return;

            Timber.d("Creating PillsListAdapter");
            PillsListAdapter adapter = new PillsListAdapter(activity, R.layout.pill_list_item, pills);

            _list.setAdapter(adapter);
        }
        else
        {
            PillsListAdapter adapter = (PillsListAdapter)_list.getAdapter();
            adapter.updateAdapter(pills);
        }
    }

    @Override
    public void pillInserted(Pill pill) {
        new GetPillsTask(getActivity()).execute();
        _addPillName.setText("");
        _addPillSize.setText("");
        _addPillSize.clearFocus();

        LayoutHelper.hideKeyboard(getActivity());
    }

    @Subscribe
    public void pillsUpdated(UpdatedPillEvent event) {
        new GetPillsTask(this.getActivity()).execute();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(isAdded() && getActivity() != null) {
            if (key.equals(getActivity().getResources().getString(R.string.pref_key_medication_list_order)) || key.equals(getActivity().getResources().getString(R.string.pref_key_reverse_order)))
                new GetPillsTask(getActivity()).execute();
        }
    }

    public static Fragment newInstance(int num) {
        PillListFragment f = new PillListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    public void completed() {
        if (!_addPillName.getText().toString().equals("")) {
            Pill newPill = new Pill();
            String pillName = _addPillName.getText().toString();
            String units = _unitSpinner.getSelectedItem().toString();
            newPill.setUnits(units);
            newPill.setName(pillName);
            newPill.setColour(_colour.getColour());

            float pillSize = 0f;
            if (!_addPillSize.getText().toString().matches("")) {
                pillSize = Float.parseFloat(_addPillSize.getText().toString());
            }
            newPill.setSize(pillSize);

            new InsertPillTask(getActivity(), newPill, this).execute();

            TrackerHelper.createPillEvent(getActivity(), TAG);
        }
    }

}
