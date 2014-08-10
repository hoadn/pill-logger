package uk.co.pilllogger.activities;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.AddConsumptionPillListAdapter;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.views.ColourIndicator;
import uk.co.pilllogger.widget.MyAppWidgetProvider;

/**
 * Created by nick on 01/11/13.
 */
public class AppWidgetConfigure extends PillLoggerActivityBase implements AddConsumptionPillListAdapter.IConsumptionSelected {

    @InjectView(R.id.widget_custom_text)
    public EditText _customText;

    @InjectView(R.id.colour_container)
    public ViewGroup _colourContainer;

    public static String CLICK_ACTION = "ClickAction";
    public static String PILL_ID = "uk.co.pilllogger.activities.AppWidgetConfigure.PILL_ID";
    public static String PILL_QUANTITY = "uk.co.pilllogger.activities.AppWidgetConfigure.PILL_QUANTITY";
    int _appWidgetId = -1;
    Typeface _typeface;
    Intent _newIntent;
    private int _selectedColour = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setTheme(State.getSingleton().getTheme().getStyleResourceId());

        setContentView(R.layout.activity_app_widget_configure);

        _typeface = State.getSingleton().getTypeface();

        ButterKnife.inject(this);

        setupColourIndicators();

        this.setResult(RESULT_CANCELED);
        new GetPillsTask(this).execute();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            _appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        _newIntent = new Intent(this, MyAppWidgetProvider.class);
    }

    @Subscribe
    public void pillsReceived(LoadedPillsEvent event) {
        ListView pillsList = (ListView) findViewById(R.id.widget_configure_pill_list);

        if (pillsList != null) {
            final AddConsumptionPillListAdapter adapter = new AddConsumptionPillListAdapter(this, this, R.layout.add_consumption_pill_list, event.getPills(), false);
            pillsList.setAdapter(adapter);

            View button = findViewById(R.id.widget_configure_add);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Pill> pillsConsumed = adapter.getPillsConsumed();
                    if(pillsConsumed.size() == 0)
                        return;

                    Map<Pill, Integer> pills = new HashMap<Pill, Integer>();
                    for(Pill pill : pillsConsumed){
                        if(pills.containsKey(pill)){
                            pills.put(pill, pills.get(pill) + 1);
                        }
                        else {
                            pills.put(pill, 1);
                        }
                    }

                    addWidget(pills, getWidgetColour(pillsConsumed), getWidgetName(pills));

                    // we have added the widgets, clear the selection, ready for next time
                    adapter.clearConsumedPills();
                }
            });
        }
    }

    private String getWidgetName(Map<Pill, Integer> pills){
        String customName = String.valueOf(_customText.getText());

        if(pills.size() == 1 && customName.isEmpty()){
            return pills.keySet().iterator().next().getName();
        }
        else{
            return customName;
        }
    }

    private int getWidgetColour(List<Pill> pills){
        int colour = -1;

        for(Pill pill : pills){
            if(colour == -1){
                colour = pill.getColour();
            }
            else{
                if(colour != pill.getColour()){
                    colour = -1;
                    break;
                }
            }
        }

        if(colour != -1 && _selectedColour == -1) {
            return colour;
        }
        else{
            return _selectedColour;
        }
    }

    private void setupColourIndicators() {
        if(_colourContainer != null) {
            for (int i = 0; i < _colourContainer.getChildCount(); i++) {
                ColourIndicator ci = (ColourIndicator) _colourContainer.getChildAt(i);

                if (ci == null)
                    continue;

                ci.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _selectedColour = ((ColourIndicator) view).getColour();
                        setupColourIndicators();
                    }
                });
                int colour = ci.getColour();

                ci.setColour(colour, false, colour == _selectedColour);
            }
        }
    }

    private void addWidget(Map<Pill, Integer> pills, int colour, String name){
        if(pills == null || pills.isEmpty())
            return;

        Timber.d("Creating widget, name: " + name + " colour: " + colour);

        Intent intent = new Intent(this, MyAppWidgetProvider.class);
        intent.setAction(CLICK_ACTION);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        SharedPreferences preferences = this.getSharedPreferences("widgets", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("widgetName" + _appWidgetId, name);
        editor.putInt("widgetColour" + _appWidgetId, colour);

        int i = 0;
        boolean found;

        // clear the preferences of old widget data
        do{
            found = false;

            String widgetIndexModifier = i > 0 ? i + "_" : "";

            String wp = "widgetPill" + widgetIndexModifier + _appWidgetId;
            String wq = "widgetQuantity" + widgetIndexModifier + _appWidgetId;

            Timber.d(wp);
            Timber.d(wq);

            if(preferences.contains(wp)) {
                editor.remove(wp);
                Timber.d("wp -> found");
                found = true;
            }
            if(preferences.contains(wq)) {
                editor.remove(wq);
                Timber.d("wq -> found");
                found = true;
            }

            i++;
        } while(found);

        int j = 0;
        for(Pill pill : pills.keySet()) {
            String widgetIndexModifier = j > 0 ? j + "_" : "";
            intent.putExtra(AppWidgetConfigure.PILL_ID + widgetIndexModifier, pill.getId());
            Integer quantity = pills.get(pill);

            intent.putExtra(AppWidgetConfigure.PILL_QUANTITY + widgetIndexModifier, quantity);

            editor.putInt("widgetPill" + widgetIndexModifier + _appWidgetId, pill.getId());
            editor.putInt("widgetQuantity" + widgetIndexModifier + _appWidgetId, quantity);

            j++;
        }

        editor.apply();

        MyAppWidgetProvider.updateWidget(this, _appWidgetId, appWidgetManager);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetId);
        setResult(RESULT_OK, resultValue);
        TrackerHelper.widgetCreatedEvent(AppWidgetConfigure.this, "AppWidgetConfigure");
        finish();
    }

    @Override
    public void setDoneEnabled(boolean enabled) {

    }
}