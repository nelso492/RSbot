package archive.fishing_net_draynor.phases;

import shared.templates.AbstractPhase;
import org.powerbot.script.rt4.ClientContext;

public class BankingPhase extends AbstractPhase<ClientContext> {
    public BankingPhase(ClientContext ctx) {
        super(ctx, "Banking");
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.count() == 1;
    }
}
