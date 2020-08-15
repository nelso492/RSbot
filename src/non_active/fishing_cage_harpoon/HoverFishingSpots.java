package disabled.fishing_cage_harpoon;


import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import shared.templates.AbstractAction;
import shared.tools.AntibanTools;
import shared.tools.GaussianTools;

import static org.powerbot.script.Condition.sleep;

public class HoverFishingSpots extends AbstractAction<ClientContext> {

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
                return npc.name().equalsIgnoreCase("fishing spot") && npc.tile().distanceTo(ctx.players.local()) > AntibanTools.getRandomInRange(0, 5);
            }
        }).viewable().poll();

        fishingSpot.hover();

        sleep(500);
    }
}

