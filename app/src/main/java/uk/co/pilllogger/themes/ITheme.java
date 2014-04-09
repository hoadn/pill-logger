package uk.co.pilllogger.themes;

/**
 * Created by Alex on 05/04/2014
 */
public interface ITheme {
    int getStyleResourceId();
    int getConsumptionListBackgroundResourceId();
    int getPillListBackgroundResourceId();
    int getStatsBackgroundResourceId();
    int getTabMaskColourResourceId();
    int getConsumptionListItemBackgroundResourceId();

    int getStackBarGraphLineColourResourceId();

    int getDefaultTextColourResourceId();

    int getSecondaryTextColourResourceId();

    int getDefaultChartColourResourceId();

    GraphHighlightMode getGraphHighlightMode();

    public enum GraphHighlightMode{
        Lighten,
        Darken
    }
}
