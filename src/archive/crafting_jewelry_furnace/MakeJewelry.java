package ngc.crafting_jewelry_furnace;


import ngc._resources.actions._config.CraftComponentConfig;
import ngc._resources.models.BaseAction;
import ngc._resources.tools.GaussianTools;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.awt.*;
import java.util.concurrent.Callable;

public class MakeJewelry extends BaseAction<ClientContext> {
    private Area furnaceArea;
    private int furnaceId;
    private CraftComponentConfig config;
    private int resourceId;

    public MakeJewelry(ClientContext ctx, int resourceId, int furnaceId, Area furnaceArea, CraftComponentConfig config) {
        super(ctx, "Crafting");
        this.resourceId = resourceId;
        this.furnaceId = furnaceId;
        this.furnaceArea = furnaceArea;
        this.config = config;
    }

    @Override
    public boolean activate() {
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean playerNotInteracting = !ctx.players.local().interacting().valid();
        boolean hasResource = ctx.inventory.select().id(resourceId).count() > 0;
        GameObject furnace = ctx.objects.select().id(furnaceId).nearest().poll();
        boolean furnaceInView = furnace.inViewport();
        boolean nearFurnace = furnace.tile().distanceTo(ctx.players.local()) <= 5;

        return hasResource && playerNotAnimated && playerNotInteracting && furnaceInView && nearFurnace;// && netInInventory;
    }

    @Override
    public void execute() {
        GameObject furnace = ctx.objects.select().id(furnaceId).nearest().poll();

        if( validPrompt() ) {
            if( clickPrompt() ) {
                // Toggle Mouse Move
                mouseMove();
                smithingWait();
            }
        } else {
            if( furnace.inViewport() ) {
                furnace.interact("Smelt");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.players.local().inMotion() && validPrompt();
                    }
                }, Random.nextInt(500, 800), 10);
                // Click The Widget
                if( clickPrompt() ) {
                    // Toggle Mouse Move
                    mouseMove();
                    smithingWait();
                }
            } else {
                ctx.movement.step(furnace);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return furnaceArea.contains(ctx.players.local());
                    }
                }, Random.nextInt(500, 777), 4);

            }
        }
    }

    private void mouseMove() {
        // Toggle Mouse Move
        if( GaussianTools.takeActionNormal() ) {
            int x = Random.nextInt(-25, -10);
            int y = Random.nextInt(0, 500);

            ctx.input.move(new Point(x, y));
        }
    }


    private boolean validPrompt() {
        if( config.getSubComponent() == -1 ) {
            return ctx.widgets.widget(config.getWidget()).component(config.getComponent()).valid();
        } else {
            return ctx.widgets.widget(config.getWidget()).component(config.getComponent()).component(config.getSubComponent()).valid();
        }
    }

    private boolean clickPrompt() {
        if( config.getSubComponent() == -1 ) {
            return ctx.widgets.widget(config.getWidget()).component(config.getComponent()).interact(config.getCommand());
        } else {
            return ctx.widgets.widget(config.getWidget()).component(config.getComponent()).component(config.getSubComponent()).interact(config.getCommand());
        }
    }

    private void smithingWait() {
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.select().id(resourceId).count() == 0 || ctx.widgets.component(233, 3).valid();
            }
        }, Random.nextInt(250, 500), 300);
    }

}
