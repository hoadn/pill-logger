package uk.co.cntwo.pilllogger.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.PillsListAdapter;
import uk.co.cntwo.pilllogger.animations.HeightAnimation;
import uk.co.cntwo.pilllogger.helpers.LayoutHelper;
import uk.co.cntwo.pilllogger.listeners.WidgetPillsClickListener;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.tasks.GetPillsTask;
import uk.co.cntwo.pilllogger.widget.MyAppWidgetProvider;

/**
 * Created by nick on 01/11/13.
 */
public class AppWidgetConfigure extends Activity implements GetPillsTask.ITaskComplete {

    public static String CLICK_ACTION = "ClickAction";
    int _appWidgetId = -1;
    Pill _chosenPill;
    Typeface _openSans;
    TextView _selectedPillName, _selectedPillSize;
    ListView _pillsList;
    View _selectedPillLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_widget_configure);

        _openSans = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Light.ttf");

        this.setResult(RESULT_CANCELED);
        new GetPillsTask(this, this).execute();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            _appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        View finishButton = findViewById(R.id.widget_finish);
        finishButton.setOnClickListener(new finishConfigureClickListener(this));

        _selectedPillName = (TextView) findViewById(R.id.widget_configure_selected_pill_name);
        _selectedPillSize = (TextView) findViewById(R.id.widget_configure_selected_pill_size);
        _selectedPillName.setTypeface(_openSans);
        _selectedPillSize.setTypeface(_openSans);
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        ListView pillsList = (ListView) findViewById(R.id.widget_configure_pill_list);
        if (pillsList != null) {
            PillsListAdapter adapter = new PillsListAdapter(this, R.layout.widget_pill_list_item, pills);
            pillsList.setAdapter(adapter);
            pillsList.setOnItemClickListener(new WidgetPillsClickListener(this));
        }
    }

    public void setChosenPill(Pill pill) {
        _chosenPill = pill;
        _selectedPillName.setText(pill.getName());
        _selectedPillSize.setText(String.valueOf(pill.getSize()));
        _selectedPillLayout = findViewById(R.id.widget_configure_selected_pill);


        _pillsList = (ListView)findViewById(R.id.widget_configure_pill_list);
        _pillsList.setVisibility(View.GONE);
        //HeightAnimation animationShow = new HeightAnimation(_selectedPillLayout, (int)LayoutHelper.dpToPx(this, 50), true, this);
        //animationShow.setDuration(200);
        _selectedPillLayout.setVisibility(View.VISIBLE);
    }

    public void cancelPillSelection(View view) {
        _pillsList.setVisibility(View.VISIBLE);`
        _selectedPillLayout.setVisibility(View.GONE);
    }

    public class finishConfigureClickListener implements View.OnClickListener {

        Context _context;

        public finishConfigureClickListener(Context context) {
            _context = context;
        }

        @Override
        public void onClick(View view) {
            Intent newIntent = new Intent(_context, MyAppWidgetProvider.class);
            newIntent.setAction(CLICK_ACTION);

            //Create a pending intent from our intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 0, newIntent, 0);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(_context);

            RemoteViews views = new RemoteViews(_context.getPackageName(),
                    R.layout.appwidget);
            views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
            appWidgetManager.updateAppWidget(_appWidgetId, views);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }
}