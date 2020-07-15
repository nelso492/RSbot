package ngc._resources.actions._config;

public class MergeConfig {
    private int primaryItemId;
    private int secondaryItemId;
    private int mergedItemId;
    private int widgetId;
    private int componentd;

    public MergeConfig(int primaryItemId, int secondaryItemId, int mergedItemId, int widgetId, int componentd) {
        this.primaryItemId = primaryItemId;
        this.secondaryItemId = secondaryItemId;
        this.mergedItemId = mergedItemId;
        this.widgetId = widgetId;
        this.componentd = componentd;
    }

    public int getPrimaryItemId() {
        return primaryItemId;
    }

    public int getSecondaryItemId() {
        return secondaryItemId;
    }

    public int getMergedItemId() {
        return mergedItemId;
    }

    public int getWidgetId() {
        return widgetId;
    }

    public int getComponentd() {
        return componentd;
    }
}
