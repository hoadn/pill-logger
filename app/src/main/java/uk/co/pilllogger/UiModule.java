package uk.co.pilllogger;

import android.webkit.WebView;

import dagger.Module;
import uk.co.pilllogger.activities.AddConsumptionActivity;
import uk.co.pilllogger.activities.AppWidgetConfigure;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.activities.MainActivity;
import uk.co.pilllogger.activities.SettingsActivity;
import uk.co.pilllogger.activities.WebViewActivity;
import uk.co.pilllogger.fragments.ConsumptionInfoDialogFragment;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.fragments.EditPillFragment;
import uk.co.pilllogger.fragments.ExportMainFragment;
import uk.co.pilllogger.fragments.ExportSelectDateFragment;
import uk.co.pilllogger.fragments.ExportSelectDosageFragment;
import uk.co.pilllogger.fragments.ExportSelectPillsFragment;
import uk.co.pilllogger.fragments.ExportSelectTimeFragment;
import uk.co.pilllogger.fragments.InfoDialogFragment;
import uk.co.pilllogger.fragments.NewPillDialogFragment;
import uk.co.pilllogger.fragments.PillInfoDialogFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.fragments.PillRecurringFragment;
import uk.co.pilllogger.fragments.SettingsFragment;
import uk.co.pilllogger.fragments.StatsFragment;

/**
 * Created by Alex on 19/08/2014
 */
@Module(
        injects = {
                MainActivity.class,
                AddConsumptionActivity.class,
                AppWidgetConfigure.class,
                DialogActivity.class,
                ExportActivity.class,
                SettingsActivity.class,
                WebViewActivity.class,
                ConsumptionInfoDialogFragment.class,
                ConsumptionListFragment.class,
                EditPillFragment.class,
                ExportMainFragment.class,
                ExportSelectDateFragment.class,
                ExportSelectDosageFragment.class,
                ExportSelectPillsFragment.class,
                ExportSelectTimeFragment.class,
                InfoDialogFragment.class,
                NewPillDialogFragment.class,
                PillInfoDialogFragment.class,
                PillListFragment.class,
                PillRecurringFragment.class,
                SettingsFragment.class,
                StatsFragment.class
        },
        complete = false,
        library = true
)
public class UiModule {
}
