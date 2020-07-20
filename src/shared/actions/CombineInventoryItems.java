package shared.actions;

import shared.templates.AbstractAction;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

/**
 * Use one inventory item on another. Useful for potions, crafting, fletching, etc
 */
public class CombineInventoryItems extends AbstractAction<ClientContext> {
    private int primaryItemId;
    private int secondaryItemId;
    private boolean hasPrompt;
    private int secondsTimeout;

    public CombineInventoryItems(ClientContext ctx, int primaryItemId, int secondaryItemId, boolean hasPrompt, int secondsTimeout) {
        super(ctx, "Working");
        this.primaryItemId = primaryItemId;
        this.secondaryItemId = secondaryItemId;
        this.hasPrompt = hasPrompt;
        this.secondsTimeout = secondsTimeout;
    }

    public CombineInventoryItems(ClientContext ctx, String status) {
        super(ctx, status);

        this.primaryItemId = -1;
        this.secondaryItemId = -1;
        this.hasPrompt = false;
        this.secondsTimeout = -1;
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

    public int getPrimaryItemId() {
        return primaryItemId;
    }

    public int getSecondaryItemId() {
        return secondaryItemId;
    }

    public boolean isHasPrompt() {
        return hasPrompt;
    }

    public int getSecondsTimeout() {
        return secondsTimeout;
    }

    public void setPrimaryItemId(int primaryItemId) {
        this.primaryItemId = primaryItemId;
    }

    public void setSecondaryItemId(int secondaryItemId) {
        this.secondaryItemId = secondaryItemId;
    }

    public void setHasPrompt(boolean hasPrompt) {
        this.hasPrompt = hasPrompt;
    }

    public void setSecondsTimeout(int secondsTimeout) {
        this.secondsTimeout = secondsTimeout;
    }
}
