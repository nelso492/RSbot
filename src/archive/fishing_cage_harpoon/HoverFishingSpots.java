package ngc.fishing_cage_harpoon;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.GaussianTools;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class HoverFishingSpots extends BaseAction<ClientContext> {

    public HoverFishingSpots(ClientContext ctx) {
        super(ctx, "Random Hover");
    }

    @Override
    public boolean activate() {
        return GaussianTools.takeActionNever();
    }

    @Override
    public void execute() {
        Npc fishingSpot = ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.name().equalsIgnoreCase("fishing spot") && npc.tile().distanceTo(ctx.players.local()) > Random.nextInt(0, 5);
            }
        }).viewable().poll();

        fishingSpot.hover();

        sleep(500);
    }
}

