package ngc._resources.actions;

import ngc._resources.actions._config.CombatConfig;
import ngc._resources.models.BaseAction;
import ngc._resources.tools.AntibanTools;
import ngc._resources.tools.CommonActions;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class CombatAction extends BaseAction<ClientContext> {
    private CombatConfig config;

    public CombatAction(ClientContext ctx, String status, CombatConfig _config) {
        super(ctx, status);
        config = _config;
    }

    @Override
    public boolean activate() {
        boolean hasMinHealth = ctx.combat.healthPercent() >= config.getMinHealthPercent();
        boolean interacting = (ctx.players.local().interacting().valid() && ctx.players.local().interacting().name().equals(config.getNpcName()))
                || ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.name().equals(config.getNpcName()) && npc.interacting().valid() && npc.interacting().name().equalsIgnoreCase(ctx.players.local().name());
            }
        }).poll().valid();
        boolean validNpcNearby =
                ctx.npcs.select().select(new Filter<Npc>() {
                    @Override
                    public boolean accept(Npc npc) {
                        return npc.name().equals(config.getNpcName()) && validNpcForCombat(npc);
                    }
                }).nearest().peek().valid();

        // printConditions(noAlchables, hasMinHealth, !interacting, !lootNearby, validNpcNearby, (config.getSafeTile() == null || config.getSafeTile().distanceTo(ctx.players.local()) == 0));


        return hasMinHealth && !interacting && validNpcNearby && (config.getSafeTile() == null || config.getSafeTile().distanceTo(ctx.players.local()) == 0);
    }

    @Override
    public void execute() {
        // Add a touch of AFK
        AntibanTools.sleepDelay(Random.nextInt(0, 3));

        // Check for npc interacting with player
        Npc target = ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.interacting().valid() && npc.name().equals(config.getNpcName()) && npc.interacting().name().equalsIgnoreCase(ctx.players.local().name()) && npc.healthPercent() > 5;
            }
        }).poll();


        if (!target.valid()) {
            // None Found. Find nearest valid target
            target = ctx.npcs.select().select(new Filter<Npc>() {
                @Override
                public boolean accept(Npc npc) {
                    return (npc.name().equals(config.getNpcName()) && validNpcForCombat(npc));
                }
            }).nearest().poll();
        }

        Npc npc = target;


        // Target Npc
        if (npc.inViewport() && (!npc.interacting().valid() || npc.interacting().name().equals(ctx.players.local().name()))) {
            if (npc.interact("Attack", npc.name())) {
                // Chill for a sec
                sleep(500);

                // Wait for the first hit
                waitForCombat();

                // Triple check we've got a target
                if (ctx.players.local().interacting().valid()) {
                    if (config.getNpcDeathAnimation() > 0) {
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
            if (config.getSafeTile() == null) {
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
                    destinationTiles.add(new Tile(npc.tile().x() + secondaryOffset, npc.tile().y() + config.getMinDistanceToTarget() + 1)); // N
                    destinationTiles.add(new Tile(npc.tile().x() + secondaryOffset, npc.tile().y() - config.getMinDistanceToTarget())); // S
                    destinationTiles.add(new Tile(npc.tile().x() - config.getMinDistanceToTarget(), npc.tile().y() + secondaryOffset + 1)); // E
                    destinationTiles.add(new Tile(npc.tile().x() + config.getMinDistanceToTarget(), npc.tile().y() + secondaryOffset)); // W

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

                            if (ctx.players.local().tile().distanceTo(npc) >= config.getMinDistanceToTarget()) {
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
                        && (config.getMinDistanceToTarget() <= 0 || npc.tile().distanceTo(ctx.players.local()) >= config.getMinDistanceToTarget());
        return approved;
        //        return (config.isMultiCombatArea() ||  || (config.getSafeTile() != null && config.getSafeTile().distanceTo(ctx.players.local()) == 0))) && npc.healthPercent() > 1;
    }

    private void waitForCombat() {
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().interacting().valid();
            }
        }, 200, 10);
    }

    private void printConditions(boolean alchs, boolean minHealth, boolean noInteracting, boolean noLootNearby, boolean validNPC, boolean safetile) {
        System.out.println(("---------Combat Checks-----------"));
        System.out.println("Alch: " + alchs);
        System.out.println("Health: " + minHealth);
        System.out.println("Interacting: " + noInteracting);
        System.out.println("Loot: " + noLootNearby);
        System.out.println("Target: " + validNPC);
        System.out.println("Safetile: " + safetile);
    }

}
