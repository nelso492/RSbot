package scripts.cooking_lumby_castle.phases;

import scripts.cooking_lumby_castle.actions.CookFood;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredPhase;

public class CookingPhase extends StructuredPhase {

    private int rawId;

    public CookingPhase(ClientContext ctx, int rawId) {
        super(ctx, "COOK");
        this.rawId = rawId;

        CookFood cookFoodAction = new CookFood(ctx, rawId);

        this.setInitialAction(cookFoodAction);

    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.select().id(rawId).count() == 0 && ctx.game.floor() == 0;
    }

    public void setRawId(int rawId) {
        this.rawId = rawId;
    }
}
