package shared.actions;

import shared.constants.Items;
import shared.templates.AbstractAction;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

/**
 * Teleport with Skull Sceptre (i)
 */
public class SkullScepterTeleport extends AbstractAction<ClientContext> {
    private int foodId;
    private int minHealth;

    public SkullScepterTeleport(ClientContext ctx, String status) {
        super(ctx, status);
    }

    public SkullScepterTeleport(ClientContext ctx, String status, int foodId, int minHealth) {
        super(ctx, status);
        this.foodId = foodId;
        this.minHealth = minHealth;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(foodId).count() == 0 && ctx.combat.healthPercent() < minHealth;
    }


    @Override
    public void execute() {
        Item i = ctx.inventory.select().id(Items.SKULL_SCEPTRE_I_21276).poll();

        if( i.valid() ) {
            i.interact("Invoke");
            ctx.controller.stop();
        }
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public int getMinHealth() {
        return minHealth;
    }

    public void setMinHealth(int minHealth) {
        this.minHealth = minHealth;
    }
}
