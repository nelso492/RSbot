package scripts.cooking_lumby_castle.phases;

import org.powerbot.script.rt4.GameObject;
import shared.constants.GameObjects;
import shared.models.BasePhase;
import org.powerbot.script.rt4.ClientContext;

public class BankingPhase extends BasePhase<ClientContext> {
    public BankingPhase(ClientContext ctx, int rawId) {
        super(ctx, "Banking");
        this.rawId = rawId;
    }

    private int rawId;

    @Override
    public boolean moveToNextPhase() {
        var stairs = ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16673).poll();
        return ctx.inventory.select().id(this.rawId).count() != 0 && ctx.game.floor() == 2 && stairs.valid() && stairs.inViewport();
    }


    public int getRawId() {
        return rawId;
    }

    public void setRawId(int rawId) {
        this.rawId = rawId;
    }
}
