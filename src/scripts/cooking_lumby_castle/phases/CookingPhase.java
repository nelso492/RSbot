package scripts.cooking_lumby_castle.phases;

import shared.models.BasePhase;
import org.powerbot.script.rt4.ClientContext;

public class CookingPhase extends BasePhase<ClientContext> {
    public CookingPhase(ClientContext ctx, int rawId) {
        super(ctx, "Cooking");
        this.rawId = rawId;
    }

    private int rawId;

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.select().id(rawId).count() == 0;
    }

    public void setRawId(int rawId) {
        this.rawId = rawId;
    }
}
