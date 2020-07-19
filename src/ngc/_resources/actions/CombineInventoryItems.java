package ngc._resources.actions;

import ngc._resources.models.BaseAction;
import ngc._resources.tools.CommonActions;
import ngc._resources.tools.GaussianTools;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class CombineInventoryItems extends BaseAction<ClientContext> {
    private final int primaryItemId;
    private final int secondaryItemId;
    private final boolean hasPrompt;
    private final int secondsTimeout;

    public CombineInventoryItems(ClientContext ctx, int primaryItemId, int secondaryItemId, boolean hasPrompt, int secondsTimeout) {
        super(ctx, "Working");
        this.primaryItemId = primaryItemId;
        this.secondaryItemId = secondaryItemId;
        this.hasPrompt = hasPrompt;
        this.secondsTimeout = secondsTimeout;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(secondaryItemId).count() > 0; // secondary id is consumable
    }


    @Override
    public void execute() {
        if (!ctx.inventory.selectedItem().valid()) {
            Item primaryItem = ctx.inventory.select().id(primaryItemId).shuffle().poll();

            primaryItem.interact("Use");
        }

        sleep();

        ctx.inventory.select().id(secondaryItemId).shuffle().poll().click(); // select random
        if (hasPrompt) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(270, 14).valid();
                }
            }, 100, 50);

            // click on prompt component (should be same for all potions)
            if (ctx.widgets.component(270, 14).valid()) {
                ctx.widgets.component(270, 14).click();
            }
        }

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.select().id(secondaryItemId).count() == 0 || ctx.widgets.component(233, 3).valid();
            }
        }, 1000, (secondsTimeout * 1000)); // allow up to x seconds of wait time


    }

}
