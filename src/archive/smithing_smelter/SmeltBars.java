package scripts.smithing_smelter;


import shared.constants.GameObjects;
import shared.constants.Items;
import shared.templates.AbstractAction;
import shared.tools.CommonAreas;
import shared.tools.GaussianProbability;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.awt.*;
import java.util.concurrent.Callable;

public class SmeltBars extends AbstractAction<ClientContext> {
    private GaussianProbability probability = new GaussianProbability();

    private Area furnaceArea = CommonAreas.edgevilleSmelter();
    private int furnaceId = GameObjects.FURNACE_EDGEVILLE;
    private int silverId = Items.SILVER_ORE_442;


    public SmeltBars(ClientContext ctx) {
        super(ctx, "Smelting");
    }

    @Override
    public boolean activate() {
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean playerNotInteracting = !ctx.players.local().interacting().valid();
        boolean hasSilver = ctx.inventory.select().id(silverId).count() > 0;
        GameObject furnace = ctx.objects.select().id(furnaceId).nearest().poll();
        boolean furnaceInView = furnace.inViewport();
        boolean nearFurnace = furnace.tile().distanceTo(ctx.players.local()) <= 5;

        return hasSilver && playerNotAnimated && playerNotInteracting && furnaceInView && nearFurnace;// && netInInventory;
    }

    @Override
    public void execute() {
        // Travel to fishing location
        GameObject furnace = ctx.objects.select().id(furnaceId).nearest().poll();

        if( validSilver() ) {
            if( clickSilver() ) {
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
                        return !ctx.players.local().inMotion() && validSilver();
                    }
                }, Random.nextInt(500, 800), 10);
                // Click The Widget
                if( clickSilver() ) {
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
        if( probability.takeActionNormal() ) {
            int x = Random.nextInt(-50, -40);
            int y = Random.nextInt(0, 550);

            ctx.input.move(new Point(x, y));
        }
    }


    private boolean clickBronze() {
        return ctx.widgets.component(270, 14).click();
    }

    private boolean validBronze() {
        return ctx.widgets.component(270, 14).valid();
    }


    private boolean clickSilver() {
        return ctx.widgets.component(270, 16).click();
    }

    private boolean validSilver() {
        return ctx.widgets.component(270, 16).valid();
    }

    private boolean smithingWait() {
        return Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.select().id(silverId).count() == 0 && !ctx.widgets.component(233, 3).valid();
            }
        }, Random.nextInt(2500, 5000), 30);
    }

}
