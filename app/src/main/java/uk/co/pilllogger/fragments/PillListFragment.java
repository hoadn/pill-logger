package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.PillListAdapterFactory;
import uk.co.pilllogger.adapters.PillRecyclerAdapter;
import uk.co.pilllogger.decorators.DividerItemDecoration;
import uk.co.pilllogger.events.CreatedPillEvent;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.events.PreferencesChangedEvent;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.jobs.InsertPillJob;
import uk.co.pilllogger.jobs.LoadPillsJob;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;


public class PillListFragment extends PillLoggerFragmentBase implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    @Inject
    PillListAdapterFactory _pillListAdapterFactory;

    public static final String TAG = "PillListFragment";
    private RecyclerView _listView;
    private Activity _activity;

    @Inject
    Context _context;

    @Inject
    Provider<Pill> _pillProvider;

    @Inject JobManager _jobManager;
    private List<Pill> _pills;
    private PillRecyclerAdapter _adapter;

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
        _activity = getActivity();
        if(_activity == null || v == null){
            return null;
        }
        int color = getResources().getColor(State.getSingleton().getTheme().getPillListBackgroundResourceId());
        v.setTag(R.id.tag_page_colour, color);
        v.setTag(R.id.tag_tab_icon_position, 1);

        _listView = (RecyclerView) v.findViewById(R.id.pill_list);

        _listView.setHasFixedSize(true);
        _listView.addItemDecoration(new DividerItemDecoration(_activity, DividerItemDecoration.VERTICAL_LIST));
        //_listView.setOnItemClickListener(new PillItemClickListener(getActivity()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(_context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _listView.setLayoutManager(layoutManager);
        _listView.setItemAnimator(new DefaultItemAnimator());

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(_activity);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        updatePills(_pills);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(_activity);
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        if(_listView == null || _listView.getAdapter() == null){
            return;
        }

        try {
            _bus.unregister(_listView.getAdapter());
        }
        catch(IllegalArgumentException ignored){} // if this throws, we're not registered anyway
    }

    /**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
//		_listView.setChoiceMode(
//				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
//						: ListView.CHOICE_MODE_NONE);
    }

    @Subscribe @DebugLog
    public void pillsLoaded(LoadedPillsEvent event) {
        updatePills(event.getPills());
    }

    private void updatePills(List<Pill> pills){
        _pills = pills;
        if(_listView == null)
            return;

        if (_listView.getAdapter() == null){ //we need to init the adapter
            Activity activity = getActivity();

            if(activity == null) // it's not gonna work without this
                return;

            _adapter = _pillListAdapterFactory.create(activity, R.layout.pill_list_item, pills);

            _adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);

                    if (positionStart == 0) {
                        _listView.scrollToPosition(0);
                    }
                }
            });

            _bus.register(_adapter);
            _listView.setAdapter(_adapter);
        }
    }

    @Subscribe
    public void pillInserted(CreatedPillEvent event) {
        _jobManager.addJobInBackground(new LoadPillsJob());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(isAdded() && getActivity() != null) {
            if (key.equals(getActivity().getResources().getString(R.string.pref_key_medication_list_order)) || key.equals(getActivity().getResources().getString(R.string.pref_key_reverse_order)))
                _jobManager.addJobInBackground(new LoadPillsJob());
        }
    }

    @Subscribe
    public void preferencesChanged(PreferencesChangedEvent event){
        if(_adapter != null){
            _adapter.notifyDataSetChanged();
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
}
