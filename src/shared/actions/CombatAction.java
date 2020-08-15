package shared.actions;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import shared.models.LootList;
import shared.templates.AbstractAction;
import shared.tools.CommonActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

/**
 * Combat interactions with a single NPC by name
 */
public class CombatAction extends AbstractAction<ClientContext> {
    private String npcName;
    private int npcDeathAnimation;
    private int minHealthPercent;
    private LootList loot;
    private boolean multiCombatArea;
    private Tile safeTile;
    private int minDistanceToTarget;

    public CombatAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

//    public CombatAction(ClientContext ctx, CombatConfig c) {
//        super(ctx, "Combat");
//        this.npcName = c.getNpcName();
//        this.npcDeathAnimation = c.getNpcDeathAnimation();
//        this.minDistanceToTarget = c.getMinDistanceToTarget();
//        this.loot = c.getLoot();
//        this.minHealthPercent = c.getMinHealthPercent();
//        this.safeTile = c.getSafeTile();
//        this.multiCombatArea = c.isMultiCombatArea();
//
//    }

    public CombatAction(ClientContext ctx, String status, String npcName, int npcDeathAnimation, int minHealthPercent, LootList loot, boolean multiCombatArea, Tile safeTile, int minDistanceToTarget) {
        super(ctx, status);
        this.npcName = npcName;
        this.npcDeathAnimation = npcDeathAnimation;
        this.minHealthPercent = minHealthPercent;
        this.loot = loot;
        this.multiCombatArea = multiCombatArea;
        this.safeTile = safeTile;
        this.minDistanceToTarget = minDistanceToTarget;
    }

    @Override
    public boolean activate() {
        boolean hasMinHealth = ctx.combat.healthPercent() >= this.minHealthPercent;
        boolean interacting = (ctx.players.local().interacting().valid() && ctx.players.local().interacting().name().equals(this.npcName));
        boolean validNpcNearby =
                ctx.npcs.select().select(new Filter<Npc>() {
                    @Override
                    public boolean accept(Npc npc) {
                        return npc.name().equals(npcName) && validNpcForCombat(npc);
                    }
                }).nearest().peek().valid();

        return hasMinHealth && !interacting && validNpcNearby && (this.safeTile == null || this.safeTile.distanceTo(ctx.players.local()) == 0);
    }

    @Override
    public void execute() {


        // Check for npc interacting with player
        Npc target = ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.name().equals(npcName) && npc.interacting().name().equalsIgnoreCase(ctx.players.local().name());
            }
        }).poll();


        if (target.valid() && target.tile().matrix(ctx).reachable()) {
            waitForCombat();
        } else {
            // None Found. Find nearest valid target
            target = ctx.npcs.select().select(new Filter<Npc>() {
                @Override
                public boolean accept(Npc npc) {
                    return (npc.name().equals(npcName) && validNpcForCombat(npc));
                }
            }).nearest().poll();

            Npc npc = target;

            // Target Npc && double check no interaction
            if (npc.inViewport() && !npc.interacting().valid()) {
                if (npc.interact("Attack", npc.name())) {

                    // Wait for the first hit
                    waitForCombat();
                }
            } else {
                if (this.safeTile == null) {
                    ctx.camera.turnTo(npc);

                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return npc.inViewport();
                        }
                    }, 100, 15);

                    if (!npc.inViewport() && this.minDistanceToTarget > 1) {
                        Tile playerTile = ctx.players.local().tile();
                        ArrayList<Tile> destinationTiles = new ArrayList<>();
                        int secondaryOffset = Random.nextInt(-3, 3);

                        // Add new destination tiles
                        destinationTiles.add(new Tile(npc.tile().x() + secondaryOffset, npc.tile().y() + this.minDistanceToTarget + 1)); // N
                        destinationTiles.add(new Tile(npc.tile().x() + secondaryOffset, npc.tile().y() - this.minDistanceToTarget)); // S
                        destinationTiles.add(new Tile(npc.tile().x() - this.minDistanceToTarget, npc.tile().y() + secondaryOffset + 1)); // E
                        destinationTiles.add(new Tile(npc.tile().x() + this.minDistanceToTarget, npc.tile().y() + secondaryOffset)); // W

                        // Closest to player
                        Collections.sort(destinationTiles, new Comparator<Tile>() {
                            @Override
                            public int compare(Tile o1, Tile o2) {
                                return (int) ((o1.distanceTo(playerTile) - o2.distanceTo(playerTile)) * 100);
                            }
                        });

                        for (Tile t : destinationTiles) {

                            // move to "safe" tile
                            if (t.matrix(ctx).reachable()) {
                                CommonActions.walkToSafespot(ctx, t);
                                sleep();

                                Condition.wait(new Callable<Boolean>() {
                                    @Override
                                    public Boolean call() throws Exception {
                                        return !ctx.players.local().inMotion();
                                    }
                                }, 150, 30);

                                if (ctx.players.local().tile().distanceTo(npc) >= this.minDistanceToTarget) {
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        }

    }

    private boolean validNpcForCombat(Npc npc) {
        boolean approved =
                npc.healthPercent() > 10 &&
                        !npc.interacting().valid();
        return approved;
    }

    private void waitForCombat() {
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().interacting().valid() && !ctx.players.local().inMotion();
            }
        }, 200, 5);
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public int getNpcDeathAnimation() {
        return npcDeathAnimation;
    }

    public void setNpcDeathAnimation(int npcDeathAnimation) {
        this.npcDeathAnimation = npcDeathAnimation;
    }

    public int getMinHealthPercent() {
        return minHealthPercent;
    }

    public void setMinHealthPercent(int minHealthPercent) {
        this.minHealthPercent = minHealthPercent;
    }

    public LootList getLoot() {
        return loot;
    }

    public void setLoot(LootList loot) {
        this.loot = loot;
    }

    public boolean isMultiCombatArea() {
        return multiCombatArea;
    }

    public void setMultiCombatArea(boolean multiCombatArea) {
        this.multiCombatArea = multiCombatArea;
    }

    public Tile getSafeTile() {
        return safeTile;
    }

    public void setSafeTile(Tile safeTile) {
        this.safeTile = safeTile;
    }

    public int getMinDistanceToTarget() {
        return minDistanceToTarget;
    }

    public void setMinDistanceToTarget(int minDistanceToTarget) {
        this.minDistanceToTarget = minDistanceToTarget;
    }
}
