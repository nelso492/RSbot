package ngc.runecrafter;


import resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class ExitRuins extends BaseAction<ClientContext> {

    private int ruinsYCoord;

    public ExitRuins(ClientContext ctx, int ruinsYCoord) {
        super(ctx, "Exit Ruins");
        this.ruinsYCoord = ruinsYCoord;
    }

    @Override
    public boolean activate() {
        boolean runesCrafted = ctx.inventory.count() == 1;
        boolean inRuins = ctx.players.local().tile().y() > ruinsYCoord;

        return runesCrafted && inRuins;
    }

    @Override
    public void execute() {
        GameObject portal = ctx.objects.select().select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject gameObject) {
                return gameObject.name().contains("Portal") && gameObject.tile().distanceTo(ctx.players.local()) <= 10;
            }
        }).poll();

        if( portal.inViewport() ) {
            ctx.input.move(portal.centerPoint());
            sleep(600);
            String action = ctx.menu.items()[0].split(" ")[0];
            if( action.equalsIgnoreCase("use") || action.equalsIgnoreCase("exit") ) {
                if( portal.interact(action) ) {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().tile().y() < ruinsYCoord;
                        }
                    }, 250, 8);

                }
            }
        } else {
            if( portal.tile().distanceTo(ctx.players.local()) > 5 ) {
                ctx.movement.step(portal);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return portal.inViewport();
                    }
                }, 250, 20);
            } else {
                // Recenter camera
                ctx.camera.pitch(Random.nextInt(50, 99));
                ctx.camera.turnTo(portal);
            }
        }
    }
}

