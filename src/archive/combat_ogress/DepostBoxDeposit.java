package scripts.combat_ogress;


import shared.constants.Items;
import shared.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class DepostBoxDeposit extends BaseAction<ClientContext> {

    private int DEPOSIT_BOX;
    private int[] retainOnDepositIds;

    public DepostBoxDeposit(ClientContext ctx, int depositBoxId) {
        super(ctx, "Banking");
        this.DEPOSIT_BOX = depositBoxId;
        this.retainOnDepositIds = new int[] {Items.NATURE_RUNE_561, Items.FIRE_RUNE_554, Items.SALMON_329};
    }

    @Override
    public boolean activate() {
        // Full inventory or no net.
        return ctx.inventory.isFull() && ctx.objects.select().id(DEPOSIT_BOX).nearest().peek().inViewport();
    }

    @Override
    public void execute() {
        ctx.objects.select().id(DEPOSIT_BOX).poll().interact("Deposit");

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.depositBox.opened();
            }
        }, 150, 20);
        ctx.depositBox.depositAllExcept(retainOnDepositIds);

    }
}
