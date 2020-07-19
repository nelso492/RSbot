package ngc.mining_rune_ess;


import resources.models.BaseAction;
import resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class BankAction extends BaseAction<ClientContext> {
    private CommonAreas areas = new CommonAreas();
    private Area eastBank = areas.getVarrockBankEast();


    public BankAction(ClientContext ctx) {
        super(ctx, "Banking");
    }

    @Override
    public boolean activate() {
        // Full inventory or no net.
        return ctx.inventory.isFull() && eastBank.contains(ctx.players.local()) && ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        if( ctx.bank.inViewport() ) {
            ctx.bank.open();

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.opened();
                }
            }, 150, 4);
            ctx.bank.depositInventory();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.isEmpty();
                }
            }, 150, 4);
        }
    }
}
