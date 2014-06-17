package uk.co.pilllogger.activities;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.dialogs.ConsumptionInfoDialog;
import uk.co.pilllogger.dialogs.PillInfoDialog;
import uk.co.pilllogger.fragments.ExportMainFragment;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.ExportSettings;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.services.IExportService;
import uk.co.pilllogger.state.FeatureType;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetMaxDosagesTask;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by Alex on 22/05/2014
 * in uk.co.pilllogger.activities.
 */
public class DialogActivity extends FragmentActivity{

    private static final String TAG = "DialogActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dialog);

        Display display = getWindowManager().getDefaultDisplay();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        Typeface roboto = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");

        View container = findViewById(R.id.export_container);

        if(container != null) {
            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
            layoutParams.width = (int) (width * 0.9);
        }


        Fragment fragment = null;
        Intent intent = getIntent();
        if (intent != null) {
            int dialogTypeInt = intent.getIntExtra("DialogType", DialogType.Consumption.ordinal());

            DialogType dialogType = DialogType.values()[dialogTypeInt];

            switch(dialogType){

                case Consumption:
                    int consumptionId = intent.getIntExtra("ConsumptionId", -1);

                    if(consumptionId >= 0){
                        Consumption consumption = ConsumptionRepository.getSingleton(this).get(consumptionId);
                        setFragment(new ConsumptionInfoDialog(this, consumption));
                    }
                    break;
                case Pill:
                    int pillId = intent.getIntExtra("PillId", -1);
                    if(pillId >= 0){
                        Pill pill = PillRepository.getSingleton(this).get(pillId);
                        setFragment(new PillInfoDialog(pill));
                    }
                    break;
            }
        }
    }

    private void setFragment(Fragment fragment){
        getFragmentManager()
                .beginTransaction()
                .add(R.id.export_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        android.app.FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return;
        }
        super.onBackPressed();
    }

    public enum DialogType{
        Consumption,
        Pill
    }
}
