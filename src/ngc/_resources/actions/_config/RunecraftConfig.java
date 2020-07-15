package ngc._resources.actions._config;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;

public class RunecraftConfig {
    private Tile[] pathToAltar;
    private Tile[] pathFromAltar;
    private int ruinsId;
    private int altarId;
    private int ruinsCoord;
    private Area bankArea;

    public RunecraftConfig() {
    }

    public RunecraftConfig(Tile[] pathToAltar, Tile[] pathFromAltar, int ruinsId, int altarId, int ruinsCoord, Area bankArea) {
        this.pathToAltar = pathToAltar;
        this.pathFromAltar = pathFromAltar;
        this.ruinsId = ruinsId;
        this.altarId = altarId;
        this.ruinsCoord = ruinsCoord;
        this.bankArea = bankArea;
    }

    public Tile[] getPathToAltar() {
        return pathToAltar;
    }

    public Tile[] getPathFromAltar() {
        return pathFromAltar;
    }

    public int getRuinsId() {
        return ruinsId;
    }

    public int getAltarId() {
        return altarId;
    }

    public int getRuinsCoord() {
        return ruinsCoord;
    }

    public Area getBankArea() {
        return bankArea;
    }
}
