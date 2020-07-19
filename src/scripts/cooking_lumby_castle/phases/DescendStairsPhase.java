package scripts.cooking_lumby_castle.phases;

import org.powerbot.script.rt4.ClientContext;
import shared.models.BasePhase;

public class DescendStairsPhase extends BasePhase<ClientContext> {
    public DescendStairsPhase(ClientContext ctx, int rawId) {
        super(ctx, "Stairs");
        this.rawId = rawId;
    }

    private int rawId;

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.select().id(this.rawId).count() > 0 && ctx.game.floor() == 0;
    }

    public void setRawId(int rawId) {
        this.rawId = rawId;
    }
}
