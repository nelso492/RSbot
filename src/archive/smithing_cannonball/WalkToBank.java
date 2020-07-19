package scripts.smithing_cannonball;


import shared.models.BaseAction;
import shared.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class WalkToBank extends BaseAction<ClientContext> {

    private int resourceId;
    private Area bankArea = CommonAreas.edgevilleBankNorth();


    public WalkToBank(ClientContext ctx, int resourceId) {
        super(ctx, "To Bank");
        this.resourceId = resourceId;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(resourceId).count() == 0 && !ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        ctx.movement.step(bankArea.getRandomTile());

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.bank.inViewport();
            }
        }, 100, 50);
    }
}
