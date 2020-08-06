package scripts.woodcutting_oak_draynor.actions;

import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredAction;
import shared.tools.AntibanTools;
import shared.tools.CommonAreas;

public class DraynorWalkToBankAction extends StructuredAction {

    public DraynorWalkToBankAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean isComplete() {
        return ctx.bank.inViewport() || CommonAreas.getDraynorBank().contains(ctx.players.local());
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !CommonAreas.getDraynorBank().contains(ctx.players.local());
    }

    @Override
    public void execute() {
        ctx.movement.step(CommonAreas.getDraynorBank().getRandomTile());
        AntibanTools.sleepDelay(AntibanTools.getRandomInRange(0, 2));

    }
}
