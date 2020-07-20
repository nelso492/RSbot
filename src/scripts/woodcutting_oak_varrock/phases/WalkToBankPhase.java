package scripts.woodcutting_oak_varrock.phases;

import org.powerbot.script.rt4.ClientContext;
import scripts.woodcutting_oak_varrock.actions.WalkToGeBank;
import shared.templates.StructuredPhase;

public class WalkToBankPhase extends StructuredPhase {
    private final WalkToGeBank action;
    public WalkToBankPhase(ClientContext ctx, String name) {
        super(ctx, name);

        action = new WalkToGeBank(ctx, "ToBank");

        this.setInitialAction(action);
    }

    @Override
    public boolean moveToNextPhase() {
        return action.isComplete();
    }
}
