package scripts.fishing_net_draynor.phases;

import resources.models.BasePhase;
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
