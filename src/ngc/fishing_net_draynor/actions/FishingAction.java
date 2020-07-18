package ngc.fishing_net_draynor.actions;


import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class FishingAction extends BaseAction<ClientContext> {
    private final int[] FISHINGID = {1525};
    private final int SMALLNETID = 303;

    public FishingAction(ClientContext ctx) {
        super(ctx, "Fishing");
    }

    @Override
    public boolean activate() {
        boolean invNotFull = ctx.inventory.select().count() < 28;
        boolean playerInteracting = ctx.players.local().interacting().valid();
        boolean playerAnimated = ctx.players.local().animation() != -1;
        boolean netInInventory = ctx.inventory.select().id(SMALLNETID).count() > 0;

         // System.out.println("Interacting: " + (playerInteracting ? "Y" : "N"));
         // System.out.println("INV Full: " + (invNotFull ? "N" : "Y"));
        return invNotFull && (!playerInteracting || !playerAnimated);// && netInInventory;//&& playerNotRunning;// && netInInventory;
    }

    @Override
    public void execute() {
        // Travel to fishing location
        Npc fishingSpot = ctx.npcs.select().id(FISHINGID).nearest().poll();

        if( fishingSpot.inViewport() ) {
            fishingSpot.interact("Small Net");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() != -1 && !ctx.players.local().inMotion();
                }
            }, 250, 4);
        } else {
            ctx.movement.step(fishingSpot);
            sleep();
            sleep();

           /* Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion();
                }
            }, Random.nextInt(250, 400), 4);*/
        }
    }


}
