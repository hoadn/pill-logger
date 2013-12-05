package uk.co.pilllogger.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.SlidePagerAdapter;
import uk.co.pilllogger.fragments.MainFragment;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends Activity {

    private ViewPager _fragmentPager;
    private PagerAdapter _fragmentPagerAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");

        ActionBar bar = getActionBar();
        if(bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            Fragment fragment = new MainFragment();
            Fragment fragment2 = new MainFragment();

            _fragmentPager = (ViewPager)findViewById(R.id.fragment_pager);

            _fragmentPagerAdapter = new SlidePagerAdapter(getFragmentManager(),
                    fragment,
                    fragment2);

            _fragmentPager.setAdapter(_fragmentPagerAdapter);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}