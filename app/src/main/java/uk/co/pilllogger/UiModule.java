package uk.co.pilllogger;

import dagger.Module;
import uk.co.pilllogger.activities.MainActivity;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.fragments.StatsFragment;

/**
 * Created by Alex on 19/08/2014
 */
@Module(
        injects = {
                MainActivity.class,
                ConsumptionListFragment.class,
                PillListFragment.class,
                StatsFragment.class
        },
        complete = false,
        library = true
)
public class UiModule {
}
