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
    int getListItemBackgroundResourceId();
    Integer getWindowBackgroundResourceId();
    int getGradientBackgroundResourceId();

    int getStackBarGraphLineColourResourceId();

    int getDefaultTextColourResourceId();

    int getSecondaryTextColourResourceId();

    int getDefaultChartColourResourceId();

    int getSelectedBackgroundColourResourceId();

    GraphHighlightMode getGraphHighlightMode();

    boolean isChartTranslucent();

    public enum GraphHighlightMode{
        Lighten,
        Darken
    }
}
