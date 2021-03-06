package scripts.combat_alkharid_warriors;

import shared.templates.AbstractAction;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class HealAction extends AbstractAction<ClientContext> {
    private final int[] FOOD_IDS = {361};

    public HealAction(ClientContext ctx) {
        super(ctx, "Eating Food");
    }

    @Override
    public boolean activate() {
        boolean minHealth = ctx.combat.health() <= 10;
        boolean foodInInventory = ctx.inventory.id(FOOD_IDS).count() > 0;
        return minHealth && foodInInventory;
    }

    @Override
    public void execute() {
        if(ctx.inventory.select().id(FOOD_IDS).count() == 0){
            ctx.game.logout();
        }

        //Eat some meat
        ctx.inventory.id(FOOD_IDS).first().action("Eat");
        sleep();
    }
}
