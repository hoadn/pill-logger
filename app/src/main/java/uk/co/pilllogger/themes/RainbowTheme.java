package uk.co.pilllogger.themes;

import uk.co.pilllogger.R;

/**
 * Created by Alex on 05/04/2014
 * in uk.co.pilllogger.themes.
 */
public class RainbowTheme implements ITheme {
    @Override
    public int getStyleResourceId() {
        return R.style.Rainbow;
    }

    @Override
    public int getConsumptionListBackgroundResourceId() {
        return R.color.consumption_background;
    }

    @Override
    public int getPillListBackgroundResourceId() {
        return R.color.pill_background;
    }

    @Override
    public int getStatsBackgroundResourceId() {
        return R.color.stats_background;
    }

    @Override
    public int getTabMaskColourResourceId() {
        return R.color.tab_mask;
    }
}
