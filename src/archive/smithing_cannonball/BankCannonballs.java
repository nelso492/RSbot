package ngc.smithing_cannonball;


import resources.models.BaseAction;
import resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class BankCannonballs extends BaseAction<ClientContext> {
    private int resourceId;
    private int craftedItemId;
    private Area bankArea;


    public BankCannonballs(ClientContext ctx, int resourceId, int craftedItemId) {
        super(ctx, "Banking");
        this.resourceId = resourceId;
        this.craftedItemId = craftedItemId;
        this.bankArea = CommonAreas.edgevilleBankNorth();
    }

    @Override
    public boolean activate() {
        boolean noResources = ctx.inventory.select().id(resourceId).count() == 0;
        boolean bankInView = ctx.bank.inViewport();

        return noResources && bankInView;
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

            ctx.bank.deposit(craftedItemId, Bank.Amount.ALL);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().id(resourceId).count() == 0;
                }
            }, 450, 8);

            // Stop if no more resources
            if( ctx.bank.select().id(resourceId).count() == 0 ) {
                ctx.controller.stop();
            }

            ctx.bank.withdraw(resourceId, Bank.Amount.ALL);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.isFull();
                }
            }, 155, 10);

        } else {
            ctx.movement.step(bankArea.getRandomTile());
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return bankArea.contains(ctx.players.local());
                }
            }, Random.nextInt(250, 450), 10);
        }
    }
}
