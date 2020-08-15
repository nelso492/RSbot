package scripts.slayer_simple;

import org.powerbot.script.Tile;
import shared.models.LootList;

public class CombatConfig {
    private int[] targetNpcIds;
    private String npcName;
    private int npcDeathAnimation;
    private int minHealthPercent;
    private LootList loot;
    private boolean multiCombatArea;
    private Tile safeTile;
    private int minDistanceToTarget;

    public CombatConfig(String npcName, int npcDeathAnimation, int minHealthPercent, LootList loot, boolean multiCombatArea,  Tile safeTile) {
        this.npcName = npcName;
        this.npcDeathAnimation = npcDeathAnimation;
        this.minHealthPercent = minHealthPercent;
        this.loot = loot;
        this.multiCombatArea = multiCombatArea;
        this.targetNpcIds = new int[0];
        this.safeTile = safeTile;
        this.minDistanceToTarget = 1;
    }

    public CombatConfig(String npcName, int npcDeathAnimation, int minHealthPercent, LootList loot, boolean multiCombatArea,  Tile safeTile, int minDistanceToTarget) {
        this.npcName = npcName;
        this.npcDeathAnimation = npcDeathAnimation;
        this.minHealthPercent = minHealthPercent;
        this.loot = loot;
        this.multiCombatArea = multiCombatArea;
        this.targetNpcIds = new int[0];
        this.safeTile = safeTile;
        this.minDistanceToTarget = minDistanceToTarget;
    }

    public int[] getTargetNpcIds() {
        return targetNpcIds;
    }

    public String getNpcName() {
        return npcName;
    }

    public int getNpcDeathAnimation() {
        return npcDeathAnimation;
    }

    public int getMinHealthPercent() {
        return minHealthPercent;
    }

    public LootList getLoot() {
        return loot;
    }

    public boolean isMultiCombatArea() {
        return multiCombatArea;
    }

    public Tile getSafeTile() {
        return safeTile;
    }

    public int getMinDistanceToTarget() {
        return minDistanceToTarget;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }
}