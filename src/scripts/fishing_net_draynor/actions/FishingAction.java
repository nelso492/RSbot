package scripts.fishing_net_draynor.actions;


import org.powerbot.script.Random;
import shared.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import shared.tools.AntibanTools;

import java.util.concurrent.Callable;

public class FishingAction extends BaseAction<ClientContext> {
    private final int[] FISHINGID = {1525};

    public FishingAction(ClientContext ctx) {
        super(ctx, "Fishing");
    }

    @Override
    public boolean activate() {
        boolean invNotFull = ctx.inventory.select().count() < 28;
        boolean playerInteracting = ctx.players.local().interacting().valid();
        boolean playerAnimated = ctx.players.local().animation() != -1;

        return invNotFull && (!playerInteracting || !playerAnimated);
    }

    @Override
    public void execute() {
        // Travel to fishing location
        Npc fishingSpot = ctx.npcs.select().id(FISHINGID).nearest().poll();

        if (fishingSpot.inViewport()) {
            fishingSpot.interact("Small Net");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() != -1 && !ctx.players.local().inMotion();
                }
            }, 250, 4);
        } else {
            ctx.movement.step(fishingSpot);
            AntibanTools.sleepDelay(Random.nextInt(0, 2));
        }
    }


}
