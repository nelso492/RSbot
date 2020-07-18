package ngc.cooking_lumby_castle.phases;

import ngc._resources.actions._template.BasePhase;
import org.powerbot.script.rt4.ClientContext;

public class BankingPhase extends BasePhase<ClientContext> {
    public BankingPhase(ClientContext ctx, int rawId) {
        super(ctx);
    }

    private int rawId;

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.select().id(this.rawId).count() != 0;
    }
}
