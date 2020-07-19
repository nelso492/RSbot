package scripts.mining_rune_ess;


import shared.models.BaseAction;
import shared.enums.OBJECT_IDS;
import shared.tools.GaussianProbability;
import shared.tools.RsLookup;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class MineEssence extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();
    private GaussianProbability p = new GaussianProbability();

    private final int essRockId = lookup.getId(OBJECT_IDS.RuneEssence_7471);

    public MineEssence(ClientContext ctx) {
        super(ctx, "Mining");
    }

    @Override
    public boolean activate() {
        boolean invNotFull = ctx.inventory.select().count() < 28;
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean inEssArea = ctx.players.local().tile().y() > 5800 || ctx.players.local().tile().x() > 5800; //Essence Mine

        return invNotFull && playerNotAnimated && inEssArea;
    }

    @Override
    public void execute() {
        // Travel to fishing location
        GameObject essRock = ctx.objects.select().id(essRockId).nearest().poll();

        if( essRock.inViewport() ) {
            if( essRock.interact("Mine") ) {
                // Toggle Mouse Move
                if( p.takeActionLikely() ) {
                    int x = ctx.input.getLocation().x + Random.nextInt(-25, 50);
                    int y = ctx.input.getLocation().y + Random.nextInt(-25, 50);

                    ctx.input.move(new Point(x, y));
                }
                sleep(2000);
                if( ctx.players.local().animation() == -1 ) {
                    // Nothing happened, drop the camera pitch
                    ctx.camera.pitch(Random.nextInt(0, 50));
                } else {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.players.local().animation() != -1;
                        }
                    }, Random.nextInt(450, 800), 100);
                }
            } else {
                ctx.camera.turnTo(essRock);
            }
        } else {
            if( essRock.tile().distanceTo(ctx.players.local()) <= 5 ) {
                ctx.camera.turnTo(essRock);
                sleep(2000);
            } else {
                ctx.movement.step(essRock);
                sleep(2000);
            }
        }
    }
}

