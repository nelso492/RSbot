package ngc._resources.actions;

import ngc._resources.Items;
import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

public class SkullScepterTeleport extends BaseAction<ClientContext> {
    private int foodId;
    private int minHealth;

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

}
