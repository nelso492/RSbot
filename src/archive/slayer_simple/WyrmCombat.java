package ngc.slayer_simple;

import ngc._resources.constants.Items;
import ngc._resources.actions._config.CombatConfig;
import ngc._resources.models.BaseAction;
import ngc._resources.tools.GaussianTools;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class WyrmCombat extends BaseAction<ClientContext> {
    private CombatConfig config;
    private int[] alchableIds;
    private ArrayList<Integer> directions;

    public WyrmCombat(ClientContext ctx, String status, CombatConfig _config, int[] alchableIds) {
        super(ctx, status);
        config = _config;
        this.alchableIds = alchableIds;

    }

    @Override
    public boolean activate() {
        boolean noAlchables = ctx.inventory.select().id(alchableIds).count() == 0 || ctx.inventory.select().id(Items.NATURE_RUNE_561).count() == 0;
        boolean hasMinHealth = ctx.combat.healthPercent() >= config.getMinHealthPercent();
        boolean interacting = (ctx.players.local().interacting().valid() && ctx.players.local().interacting().name().equals(config.getNpcName()))
                || ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return ctx.players.local().healthBarVisible() && npc.name().equals(config.getNpcName()) && npc.inViewport() && npc.interacting().valid() && npc.interacting().name().equalsIgnoreCase(ctx.players.local().name());
            }
        }).poll().valid();
        /*boolean lootNearby = ctx.groundItems.select().id(config.getLoot().allItemIds()).select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem groundItem) {
                LootItem i = config.getLoot().getLootItemById(groundItem.id());
                if( i != null ) {
                    // Item can be picked up, stack is above min size, item in viewport
                    return CommonFunctions.isValidLoot(ctx, groundItem, i, -1, false);
                } else {
                    // Ground Item not in our loot list
                    return false;
                }
            }
        }).poll().valid();*/
        boolean validNpcNearby =
                ctx.npcs.select().select(new Filter<Npc>() {
                    @Override
                    public boolean accept(Npc npc) {
                        return npc.name().equals(config.getNpcName()) && validNpcForCombat(npc);
                    }
                }).nearest().peek().valid();


        return noAlchables && hasMinHealth && !interacting  && validNpcNearby;
    }

    @Override
    public void execute() {
        // Add a touch of AFK
        sleep(GaussianTools.getRandomGaussian(1000, 500));

        // Check for npc interacting with player
        Npc target = ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.interacting().valid() && npc.name().equals(config.getNpcName()) && npc.interacting().name().equalsIgnoreCase(ctx.players.local().name());
            }
        }).poll();


        if( !target.valid() ) {
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
        if( npc.valid() ) {
            if( npc.inViewport() ) {
                if( npc.interact("Attack", npc.name()) ) {
                    // Wait for animation start
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().interacting().valid() && ctx.players.local().interacting().animation() == 8268;
                        }
                    }, 100, 30);

                    // Wait for animation to end
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return npc.animation() == -1 || !npc.valid() || !ctx.players.local().interacting().valid();
                        }
                    }, 100, 30);

                    // Wait for the first hit
                    waitForCombat(npc);
                }
            } else {

                // Try turning the camera
                ctx.camera.turnTo(npc);

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return npc.inViewport() || !npc.valid() || npc.interacting().valid(); // visible or engaged
                    }
                }, 100, 20);

                if( npc.valid() && !npc.interacting().valid() && !npc.inViewport() ) {

                    int secondaryOffset = Random.nextInt(-2, 2);
                    int rDirection = Random.nextInt(1, 4);
                    Tile offsetTile;

                    switch( rDirection ) {
                        case 1:
                            offsetTile = new Tile(npc.tile().x() + Random.nextInt(0, config.getMinDistanceToTarget()), npc.tile().y() + secondaryOffset);
                            break;
                        case 2:
                            offsetTile = new Tile(npc.tile().x() - Random.nextInt(0, config.getMinDistanceToTarget()), npc.tile().y() + secondaryOffset);
                            break;
                        case 3:
                            offsetTile = new Tile(npc.tile().x() + secondaryOffset, npc.tile().y() - Random.nextInt(0, config.getMinDistanceToTarget()));
                            break;
                        default:
                            offsetTile = new Tile(npc.tile().x() + secondaryOffset, npc.tile().y() + Random.nextInt(0, config.getMinDistanceToTarget()));
                            break;
                    }

                    if(offsetTile.matrix(ctx).reachable()) {
                        ctx.movement.step(offsetTile);

                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return npc.inViewport();
                            }
                        }, 100, 20);
                    }
                }
            }
        }

    }

    private boolean validNpcForCombat(Npc npc) {
        return !npc.interacting().valid() && npc.animation() != 8268 && npc.tile().distanceTo(ctx.players.local()) >= config.getMinDistanceToTarget() && npc.healthPercent() > 1; // death animation
    }

    private void waitForCombat(Npc npc) {
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().interacting().valid() && npc.animation() != 8268;
            }
        }, 500, 10);
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
