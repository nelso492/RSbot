package scripts.fishing_net_draynor.phases;

import shared.models.BasePhase;
import org.powerbot.script.rt4.ClientContext;

public class BankingPhase extends BasePhase<ClientContext> {
    public BankingPhase(ClientContext ctx) {
        super(ctx, "Banking");
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.count() == 1;
    }
}
