package ngc.mining_rune_ess;


import ngc._resources.models.BaseAction;
import ngc._resources.enums.NPC_IDS;
import ngc._resources.tools.RsLookup;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class PortalTeleport extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();

    private int[] portalIds = {lookup.getId(NPC_IDS.EssMinePortal_5895), lookup.getId(NPC_IDS.EssMinePortal_5904), lookup.getId(NPC_IDS.EssMinePortal_5905), lookup.getId(NPC_IDS.EssMinePortal_5897), lookup.getId(NPC_IDS.EssMinePortal_5896)};

    public PortalTeleport(ClientContext ctx) {
        super(ctx, "Portal Teleport");
    }

    @Override
    public boolean activate() {
        boolean invFull = ctx.inventory.isFull();
        boolean inEssenseArea = ctx.players.local().tile().y() > 5800 || ctx.players.local().tile().x() > 5800; //Essence Mine

        return invFull && inEssenseArea;
    }

    @Override
    public void execute() {
        // Travel to fishing location
        Npc essPortal = ctx.npcs.select().id(portalIds).nearest().poll();

        if( essPortal.inViewport() && essPortal.tile().distanceTo(ctx.players.local()) < 5 ) {
            ctx.input.move(essPortal.centerPoint());
            sleep(600);
            String action = ctx.menu.items()[0].split(" ")[0];
            if( action.equalsIgnoreCase("use") || action.equalsIgnoreCase("exit") ) {
                if( essPortal.interact(action) ) {
                    sleep(Random.nextInt(1000, 2000));
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().tile().x() < 6000 && ctx.players.local().tile().y() < 6000;
                        }
                    }, 350, 8);

                }
            } else {
                // Recenter camera
                ctx.camera.pitch(Random.nextInt(50, 99));
                ctx.camera.turnTo(essPortal);
            }
        } else {
            ctx.movement.step(essPortal);
            sleep(2000);
        }
    }
}

