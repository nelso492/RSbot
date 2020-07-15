package ngc._resources.actions._config;

public class CraftComponentConfig {
    private int widget = 0;
    private int component = 0;
    private int subComponent = 0;
    private String command = "";

    public CraftComponentConfig(int widget, int component, int subComponent, String command) {
        this.widget = widget;
        this.component = component;
        this.subComponent = subComponent;
        this.command = command;
    }

    public int getWidget() {
        return widget;
    }

    public int getComponent() {
        return component;
    }

    public int getSubComponent() {
        return subComponent;
    }

    public String getCommand() {
        return command;
    }
}
