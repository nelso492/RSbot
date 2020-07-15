package ngc.slayer_simple;

import ngc._resources.actions._config.CombatConfig;
import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.GaussianTools;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class GargoyleCombat extends BaseAction<ClientContext> {
    private CombatConfig config;

    public GargoyleCombat(ClientContext ctx, String status, CombatConfig _config) {
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
                return npc.tile().distanceTo(ctx.players.local()) < 2 && npc.name().equals(config.getNpcName()) && npc.inViewport() && npc.interacting().valid() && npc.interacting().name().equalsIgnoreCase(ctx.players.local().name());
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


        return hasMinHealth && !interacting && validNpcNearby;
    }

    @Override
    public void execute() {
        // Add a touch of AFK
        sleep(Math.abs(GaussianTools.getRandomGaussian(0, 500)));

        // Check for npc interacting with player
        Npc target = ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.interacting().valid() && npc.name().equals(config.getNpcName()) && npc.interacting().name().equalsIgnoreCase(ctx.players.local().name()) && npc.healthPercent() > 9;
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
        if( npc.inViewport() && (!npc.interacting().valid() || npc.interacting().name().equals(ctx.players.local().name())) ) {
            if( npc.interact("Attack", npc.name()) ) {
                // Chill for a sec
                sleep(500);

                // Wait for the first hit
                waitForCombat();
            }
        } else {
            if( config.getSafeTile() == null ) {
                ctx.camera.turnTo(npc);

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return npc.inViewport();
                    }
                }, 200, 20);

                if( !npc.inViewport() ) {
                    ctx.movement.step(npc);
                    sleep(Random.nextInt(500, 750)); //Allows target switching if needed
                }
            }
        }

    }

    private boolean validNpcForCombat(Npc npc) {
        return (npc.healthPercent() > 9) && !npc.interacting().valid() && npc.animation() != 1520; // death animation
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
