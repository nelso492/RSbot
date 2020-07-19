package scripts.misc_barb_village_looter;


import resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class BankingAction extends BaseAction<ClientContext> {
    public BankingAction(ClientContext ctx) {
        super(ctx, "Banking");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 28 && ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        if(!ctx.bank.opened()) {
            ctx.objects.select().id(6943).nearest().poll().interact("Bank");
        }
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.bank.opened();
            }
        }, Random.nextInt(666, 1111), 10);
        ctx.bank.depositInventory();
        sleep();
    }
}
