package uk.co.pilllogger.models;

import dagger.Module;
import uk.co.pilllogger.activities.MainActivity;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.fragments.StatsFragment;

/**
 * Created by Alex on 19/08/2014
 * in uk.co.pilllogger.models.
 */
@Module(
        injects = {
                Pill.class,
                Consumption.class,
                ExportSettings.class
        },
        complete = false,
        library = true
)
public class ModelModule {
}
