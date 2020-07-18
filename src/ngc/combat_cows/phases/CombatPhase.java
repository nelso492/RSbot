package ngc.combat_cows.phases;

import ngc._resources.actions._template.BasePhase;
import org.powerbot.script.rt4.ClientContext;

public class CombatPhase extends BasePhase<ClientContext> {
    public CombatPhase(ClientContext ctx) {
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
