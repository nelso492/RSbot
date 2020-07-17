package ngc.cmb_cows;

import ngc._resources.actions._template.BasePhase;
import org.powerbot.script.rt4.ClientContext;

public class CowKillerAIO_Combat extends BasePhase<ClientContext> {
    public CowKillerAIO_Combat(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean moveToNextPhase() {
        boolean hasNoFood = ctx.inventory.isEmpty();
        boolean hasNoHealth = ctx.players.local().healthPercent() < 10;
        boolean invFull = ctx.inventory.isFull();

        return hasNoFood || hasNoHealth || invFull;
    }
}
