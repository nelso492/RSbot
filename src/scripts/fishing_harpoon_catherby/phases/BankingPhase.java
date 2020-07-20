package scripts.fishing_harpoon_catherby.phases;

import org.powerbot.script.rt4.ClientContext;
import shared.models.BasePhase;

public class BankingPhase extends BasePhase<ClientContext> {
    public BankingPhase(ClientContext ctx) {
        super(ctx, "Banking");
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.count() == 1;
    }
}
