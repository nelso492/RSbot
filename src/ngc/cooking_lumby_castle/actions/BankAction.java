package ngc.cooking_lumby_castle.actions;


import resources.models.BaseAction;
import resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class BankAction extends BaseAction<ClientContext> {
    private Area bankArea;
    private int rawFoodId;


    public BankAction(ClientContext ctx, int _rawFoodId) {
        super(ctx, "Banking");
        this.rawFoodId = _rawFoodId;
        this.bankArea = CommonAreas.lumbridgeCastleBank();
    }

    @Override
    public boolean activate() {
        // Full inventory or no net.
        boolean noRawFish = ctx.inventory.select().id(rawFoodId).count() == 0;
        boolean inBank = bankArea.contains(ctx.players.local());
        boolean secondFloor = ctx.game.floor() == 2;


        return noRawFish && !inBank && secondFloor;
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

            if(ctx.bank.select().id(rawFoodId).count() == 0){
                ctx.controller.stop();
            }

            ctx.bank.withdraw(rawFoodId, Bank.Amount.ALL);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.isFull();
                }
            }, 150, 4);
        } else {
            ctx.movement.step(bankArea.getRandomTile());
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.inViewport();
                }
            }, 350, 10);
        }
    }
}
