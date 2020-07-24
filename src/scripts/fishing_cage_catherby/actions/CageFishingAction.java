package scripts.fishing_cage_catherby.actions;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import shared.templates.StructuredAction;

import java.util.concurrent.Callable;

/**
 * Net fishing at Catherby fishing spots.
 */
public class CageFishingAction extends StructuredAction {

    public CageFishingAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        return !ctx.inventory.isFull() && ctx.players.local().animation() == -1 && !ctx.players.local().interacting().valid();
    }

    @Override
    public void execute() {
        Npc fishingSpot = ctx.npcs.select().id(1519).nearest().poll();

        if (fishingSpot.inViewport()) {
            fishingSpot.interact("Cage");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().interacting().valid() && !ctx.players.local().inMotion();
                }
            }, 500, 10);
        } else {
            ctx.movement.step(fishingSpot);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion();
                }
            }, 100, 20);
        }
    }

    @Override
    public boolean isComplete() {
        return ctx.inventory.isFull();
    }
}
