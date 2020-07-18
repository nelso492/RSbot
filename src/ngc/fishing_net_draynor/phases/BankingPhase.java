package ngc.fishing_net_draynor.phases;

import ngc._resources.actions._template.BasePhase;
import org.powerbot.script.rt4.ClientContext;

public class BankingPhase extends BasePhase<ClientContext> {
    public BankingPhase(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.count() == 1;
    }
}
