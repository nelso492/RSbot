package archive.combat_cows.phases;

import shared.templates.AbstractPhase;
import org.powerbot.script.rt4.ClientContext;

public class CombatPhase extends AbstractPhase<ClientContext> {
    public CombatPhase(ClientContext ctx) {
        super(ctx, "Combat");
    }

    @Override
    public boolean moveToNextPhase() {
        boolean hasNoFood = ctx.inventory.isEmpty();
        boolean hasNoHealth = ctx.players.local().healthPercent() < 10;
        boolean invFull = ctx.inventory.isFull();

        return hasNoFood || hasNoHealth || invFull;
    }
}
