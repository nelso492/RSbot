package ngc._resources.actions._config;

import org.powerbot.script.Tile;
import org.powerbot.script.Area;

public class WalkConfig {
    private Tile[] path;
    private Area targetArea;

    public WalkConfig(Area area, Tile[] path) {
        this.targetArea = area;
        this.path = path;
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

}
