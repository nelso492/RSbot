package shared.actions;

import shared.templates.AbstractAction;
import shared.models.LootList;
import shared.tools.AntibanTools;
import shared.tools.CommonActions;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import shared.tools.GaussianTools;

import java.awt.*;
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
        boolean interacting = (ctx.players.local().interacting().valid() && ctx.players.local().interacting().name().equals(this.npcName))
                || ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.name().equals(npcName) && npc.interacting().valid() && !npc.interacting().name().equalsIgnoreCase(ctx.players.local().name());
            }
        }).poll().valid();
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
                return npc.interacting().valid() && npc.name().equals(npcName) && npc.interacting().name().equalsIgnoreCase(ctx.players.local().name()) && npc.healthPercent() > 5;
            }
        }).poll();


        if (!target.valid()) {
            // None Found. Find nearest valid target
            target = ctx.npcs.select().select(new Filter<Npc>() {
                @Override
                public boolean accept(Npc npc) {
                    return (npc.name().equals(npcName) && validNpcForCombat(npc));
                }
            }).nearest().poll();
        }

        Npc npc = target;


        if(GaussianTools.takeActionRarely()){
            ctx.input.move(new Point(npc.nextPoint().x + Random.nextInt(-20, 20), npc.nextPoint().y + Random.nextInt(-20, 20)));
            sleep();
        }

        // Add a touch of AFK
        AntibanTools.sleepDelay(Random.nextInt(0, 3));

        // Target Npc
        if (npc.inViewport() && (!npc.interacting().valid() || npc.interacting().name().equals(ctx.players.local().name()))) {
            if (npc.interact("Attack", npc.name())) {
                // Chill for a sec
               AntibanTools.sleepDelay(2);

                // Wait for the first hit
                waitForCombat();

                // Triple check we've got a target
                if (ctx.players.local().interacting().valid()) {
                    if (this.npcDeathAnimation > 0) {
                        // Wait for drop like a good human
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return !npc.valid() || validNpcForCombat(npc);
                            }
                        }, Random.nextInt(250, 500), 60);
                    }
                }
            }
        } else {
            if (this.safeTile == null) {
                ctx.camera.turnTo(npc);

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return npc.inViewport();
                    }
                }, 100, 20);

                if (!npc.inViewport()) {
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

    private boolean validNpcForCombat(Npc npc) {
        boolean approved =
                npc.healthPercent() > 0 &&
                        !npc.interacting().valid()
                        && (this.minDistanceToTarget <= 0 || npc.tile().distanceTo(ctx.players.local()) >= this.minDistanceToTarget);
        return approved;
        //        return (isMultiCombatArea() ||  || (getSafeTile() != null && getSafeTile().distanceTo(ctx.players.local()) == 0))) && npc.healthPercent() > 1;
    }

    private void waitForCombat() {
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().interacting().valid();
            }
        }, 200, 10);
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
