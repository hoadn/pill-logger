package uk.co.pilllogger.themes;

import uk.co.pilllogger.R;

/**
 * Created by Alex on 05/04/2014
 * in uk.co.pilllogger.themes.
 */
public class ProfessionalTheme implements ITheme {
    @Override
    public int getStyleResourceId() {
        return R.style.Professional;
    }

    @Override
    public int getConsumptionListBackgroundResourceId() {
        return R.color.professional_background;
    }

    @Override
    public int getPillListBackgroundResourceId() {
        return R.color.professional_background;
    }

    @Override
    public int getStatsBackgroundResourceId() {
        return R.color.professional_background;
    }


    @Override
    public int getTabMaskColourResourceId() {
        return R.color.tab_mask_professional;
    }

    @Override
    public int getConsumptionListItemBackgroundResourceId() {
        return R.drawable.list_selector_professional;
    }

    @Override
    public int getStackBarGraphLineColourResourceId() {
        return R.color.stackbar_graph_line_professional;
    }

    @Override
    public int getDefaultTextColourResourceId() {
        return R.color.default_text_professional;
    }

    @Override
    public int getSecondaryTextColourResourceId() {
        return R.color.secondary_text_professional;
    }

    @Override
    public GraphHighlightMode getGraphHighlightMode() {
        return GraphHighlightMode.Darken;
    }

    @Override
    public int getDefaultChartColourResourceId() {
        return R.color.default_chart_professional;
    }
}
