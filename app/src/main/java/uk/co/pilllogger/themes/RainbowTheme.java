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

    @Override
    public int getListItemBackgroundResourceId() {
        return R.drawable.list_selector;
    }

    @Override
    public int getGradientBackgroundResourceId() {
        return R.drawable.orange_gradient;
    }

    @Override
    public int getStackBarGraphLineColourResourceId() {
        return R.color.stackbar_graph_line;
    }

    @Override
    public int getDefaultTextColourResourceId() {
        return R.color.default_text;
    }

    @Override
    public int getSecondaryTextColourResourceId() {
        return R.color.secondary_text;
    }

    @Override
    public GraphHighlightMode getGraphHighlightMode() {
        return GraphHighlightMode.Lighten;
    }

    @Override
    public boolean isChartTranslucent() {
        return true;
    }

    @Override
    public int getDefaultChartColourResourceId() {
        return R.color.default_chart;
    }

    @Override
    public int getSelectedBackgroundColourResourceId() {
        return R.color.pill_selection_background;
    }
}
