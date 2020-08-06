package scripts.cooking_lumby_castle.actions;


import shared.constants.GameObjects;
import shared.templates.AbstractAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import shared.templates.StructuredAction;
import shared.tools.AntibanTools;
import shared.tools.GaussianTools;

import java.util.concurrent.Callable;

public class CookFood extends StructuredAction {

    private final int rangeId;
    private final int rawFoodId;


    public CookFood(ClientContext ctx, int _rawFoodId) {
        super(ctx, "Food");
        rawFoodId = _rawFoodId;
        this.rangeId = GameObjects.COOKING_RANGE_LUMBRIDGE;
    }

    @Override
    public boolean activate() {
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean playerNotInteracting = !ctx.players.local().interacting().valid();
        boolean hasRawFood = ctx.inventory.select().id(rawFoodId).count() > 0;

        return hasRawFood && playerNotAnimated && playerNotInteracting && ctx.game.floor() == 0;
    }

    @Override
    public void execute() {
        // Travel to fishing location
        GameObject cookingRange = ctx.objects.select().id(rangeId).nearest().poll();

        if (cookingRange.inViewport()) {
            if (cookingRange.interact("Cook")) {
                // Interact with Range prompt
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.players.local().inMotion() && ctx.widgets.component(270, 13).valid();
                    }
                }, AntibanTools.getRandomInRange(500, 800), 10);

                if (ctx.widgets.component(270, 13).valid()) {
                    ctx.input.send(" ");

                    // Antiban actions
                    if (GaussianTools.takeActionLikely()) {
                        AntibanTools.moveMouseOffScreen(ctx, (GaussianTools.takeActionNormal()));
                    }

                    AntibanTools.sleepDelay(AntibanTools.getRandomInRange(0, 5));
                }
            } else {
                ctx.movement.step(cookingRange);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.players.local().inMotion();
                    }
                }, AntibanTools.getRandomInRange(500, 777), 4);
            }
        } else {
            ctx.movement.step(cookingRange);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion();
                }
            }, AntibanTools.getRandomInRange(500, 777), 4);
        }
    }

    @Override
    public boolean isComplete() {
        return ctx.inventory.select().id(this.rawFoodId).count() == 0;
    }
}
