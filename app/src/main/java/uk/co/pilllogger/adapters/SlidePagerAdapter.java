package uk.co.pilllogger.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

import java.util.Arrays;
import java.util.List;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.fragments.StatsFragment;

/**
 * Created by alex on 05/12/2013.
 */
public class SlidePagerAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {

    private final boolean _isTablet;

    public SlidePagerAdapter(FragmentManager fragmentManager, boolean isTablet) {
        super(fragmentManager);
        _isTablet = isTablet;
    }

    @Override @DebugLog
    public Fragment getItem(int position) {
        if(_isTablet){
            switch (position) {
                case 0:
                    return ConsumptionListFragment.newInstance(position);

                case 1:
                    return StatsFragment.newInstance(position);
            }
        }
        else {
            switch (position) {
                case 0:
                    return ConsumptionListFragment.newInstance(position);

                case 1:
                    return PillListFragment.newInstance(position);

                case 2:
                    return StatsFragment.newInstance(position);
            }
        }

        return null;
    }

    @Override
    public int getIconResId(int i) {
        if(_isTablet) {
            switch (i) {
                case 0:
                    return R.drawable.tab_consumptions;
                case 1:
                    return R.drawable.tab_charts;
            }
        }
        else {
            switch (i) {
                case 0:
                    return R.drawable.tab_consumptions;
                case 1:
                    return R.drawable.tab_medicine;
                case 2:
                    return R.drawable.tab_charts;
            }
        }

        return R.drawable.cancel;
    }

    @Override
    public int getCount() {
        return _isTablet ? 2 : 3;
    }

}
