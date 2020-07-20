package scripts.fishing_net_draynor.actions;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import shared.templates.StructuredAction;

import java.util.concurrent.Callable;

/**
 * Net fishing at Draynor fishing spots.
 */
public class NetFishingAction extends StructuredAction {

    public NetFishingAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        return !ctx.inventory.isFull() && ctx.players.local().animation() == -1 && ctx.players.local().interacting().valid();
    }

    @Override
    public void execute() {
        Npc fishingSpot = ctx.npcs.select().id(1525).nearest().poll();

        if (fishingSpot.inViewport()) {
            fishingSpot.interact("Small Net");
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
