package scripts.cooking_lumby_castle.phases;

import org.powerbot.script.rt4.ClientContext;
import scripts.cooking_lumby_castle.actions.DescendStairs;
import shared.templates.StructuredPhase;

public class DescendStairsPhase extends StructuredPhase {

    public DescendStairsPhase(ClientContext ctx) {
        super(ctx, "CLIMB DOWN");

        DescendStairs descendStairsAction = new DescendStairs(ctx);
        this.setInitialAction(descendStairsAction);
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.game.floor() == 0;
    }
}
