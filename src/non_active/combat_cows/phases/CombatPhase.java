package disabled.combat_cows.phases;

import org.powerbot.script.rt4.ClientContext;
import shared.templates.PollingPhase;

public class CombatPhase extends PollingPhase {
    public CombatPhase(ClientContext ctx) {
        super(ctx, "Combat");
    }

    @Override
    public boolean moveToNextPhase() {
       return false;
    }
}
