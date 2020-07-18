package ngc._resources.actions._config;

import org.powerbot.script.Tile;
import org.powerbot.script.Area;

public class WalkConfig {
    private Tile[] path;
    private Area targetArea;

    // Used for accumulating or using all of a resource
    private boolean activateOnInvCount;
    private int activationInvCount;

    public WalkConfig() {
        this.activateOnInvCount = false;
        this.activationInvCount = -1;
    }

    public Tile[] getPath() {
        return path;
    }

    public void setPath(Tile[] path) {
        this.path = path;
    }

    public Area getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(Area targetArea) {
        this.targetArea = targetArea;
    }

    public boolean isActivateOnInvCount() {
        return activateOnInvCount;
    }

    public void setActivateOnInvCount(boolean activateOnInvCount) {
        this.activateOnInvCount = activateOnInvCount;
    }

    public int getActivationInvCount() {
        return activationInvCount;
    }

    public void setActivationInvCount(int activationInvCount) {
        this.activationInvCount = activationInvCount;
    }
}
