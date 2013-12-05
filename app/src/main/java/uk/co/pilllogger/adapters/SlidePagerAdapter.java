package uk.co.pilllogger.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 05/12/2013.
 */
public class SlidePagerAdapter extends FragmentStatePagerAdapter {

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
    public int getCount() {
        return _fragments.size();
    }

}
