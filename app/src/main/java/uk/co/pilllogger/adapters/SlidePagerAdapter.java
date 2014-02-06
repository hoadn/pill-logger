package uk.co.pilllogger.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

import java.util.Arrays;
import java.util.List;

import uk.co.pilllogger.R;

/**
 * Created by alex on 05/12/2013.
 */
public class SlidePagerAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {

    private List<Fragment> _fragments;


    public SlidePagerAdapter(FragmentManager fragmentManager, Fragment... fragments) {
        super(fragmentManager);
        if(fragments != null && fragments.length > 0)
            _fragments = Arrays.asList(fragments);
    }

    @Override
    public Fragment getItem(int position) {
        return _fragments.get(position);
    }

    @Override
    public int getIconResId(int i) {
        switch(i){
            case 0:
                return R.drawable.tab_consumptions;
            case 1:
                return R.drawable.tab_medicine;
            case 2:
                return R.drawable.tab_charts;
        }

        return R.drawable.cancel;
    }

    @Override
    public int getCount() {
        return _fragments.size();
    }

}
