package resources.actions;

import resources.models.BaseAction;
import resources.tools.CommonActions;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

public class HealAction extends BaseAction<ClientContext> {
    private int[] foodIds;
    private int healthPercent;

    public HealAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    public HealAction(ClientContext ctx, String status, int[] foodIds, int healthPercent) {
        super(ctx, status);
        this.foodIds = foodIds;
        this.healthPercent = healthPercent;
    }

    @Override
    public boolean activate() {
        boolean minHealth = ctx.combat.healthPercent() <= this.healthPercent;
        boolean foodInInventory = ctx.inventory.select().id(this.foodIds).count() > 0;
        return minHealth && foodInInventory;
    }

    @Override
    public void execute() {
        CommonActions.openTab(ctx, Game.Tab.INVENTORY);

        Item i = ctx.inventory.select().id(this.foodIds).first().poll();

        if (i.name().contains("Jug") || i.name().contains("brew")) {
            i.interact("Drink");
        } else {
            i.interact("Eat", i.name());
        }
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !i.valid();
            }
        }, 100, 30);
    }

    public int[] getFoodIds() {
        return foodIds;
    }

    public void setFoodIds(int[] foodIds) {
        this.foodIds = foodIds;
    }

    public int getHealthPercent() {
        return healthPercent;
    }

    public void setHealthPercent(int healthPercent) {
        this.healthPercent = healthPercent;
    }
}
