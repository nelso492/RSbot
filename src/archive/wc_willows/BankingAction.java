package scripts.wc_willows;


import shared.constants.GameObjects;
import shared.templates.AbstractAction;
import shared.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class BankingAction extends AbstractAction<ClientContext> {
    private int DEPOSIT_BOX;
    private Area depositBox;

    public BankingAction(ClientContext ctx) {
        super(ctx, "Banking");
        this.DEPOSIT_BOX = GameObjects.DEPOSIT_BOX_26254;
        this.depositBox = CommonAreas.getPortSarimDepositBox();
    }

    @Override
    public boolean activate() {
        // Full inventory or no net.
        return ctx.inventory.select().count() == 28 && depositBox.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        if(!ctx.depositBox.opened()) {
            ctx.objects.select().id(DEPOSIT_BOX).poll().interact("Deposit");
        }
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.depositBox.opened();
            }
        }, 150, 20);
        ctx.depositBox.depositInventory();
    }
}
