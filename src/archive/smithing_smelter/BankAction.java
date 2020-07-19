package ngc.smithing_smelter;


import resources.constants.Items;
import resources.models.BaseAction;
import resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class BankAction extends BaseAction<ClientContext> {
    private int oreId = Items.SILVER_ORE_442;

    private Area bankArea = CommonAreas.edgevilleBankNorth();


    public BankAction(ClientContext ctx) {
        super(ctx, "Banking");
    }

    @Override
    public boolean activate() {
        // Full inventory or no net.
        boolean noOres = ctx.inventory.select().id(oreId).count() == 0;
        boolean bankInView = ctx.bank.inViewport();

        return noOres && bankInView;
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
            }, 250, 8);

            ctx.bank.depositInventory();

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.isEmpty();
                }
            }, 450, 8);

            if( ctx.bank.select().id(oreId).count() == 0 ) {
                ctx.controller.stop();
            }

            ctx.bank.withdraw(oreId, Bank.Amount.ALL);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.isFull();
                }
            }, 155, 10);

        } else {
            ctx.movement.step(ctx.bank.nearest());
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return bankArea.contains(ctx.players.local());
                }
            }, Random.nextInt(250, 450), 10);
        }
    }
}
