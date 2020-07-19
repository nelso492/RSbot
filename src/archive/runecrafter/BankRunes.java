package scripts.runecrafter;


import resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class BankRunes extends BaseAction<ClientContext> {

    private int essId;

    public BankRunes(ClientContext ctx, int essId) {
        super(ctx, "Banking");
        this.essId = essId;
    }

    @Override
    public boolean activate() {
        // Full inventory or no net.
        return ctx.inventory.count() == 1 && ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        ctx.bank.open();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.bank.opened();
            }
        }, 250, 10);

        ctx.bank.depositInventory();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.isEmpty();
            }
        }, 250, 10);

        ctx.bank.withdraw(essId, Bank.Amount.ALL);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.isFull();
            }
        }, 150, 4);
    }

}
