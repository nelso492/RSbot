package ngc.cooking_lumby_castle.phases;

import ngc._resources.actions._template.BasePhase;
import org.powerbot.script.rt4.ClientContext;

public class CookingPhase extends BasePhase<ClientContext> {
    public CookingPhase(ClientContext ctx, int rawId) {
        super(ctx);
        this.rawId = rawId;
    }

    private int rawId;

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.select().id(rawId).count() == 0;
    }
}
