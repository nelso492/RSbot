package disabled.woodcutting_oak_varrock.phases;

import org.powerbot.script.rt4.ClientContext;
import disabled.woodcutting_oak_varrock.actions.WalkToOakAction;
import shared.templates.StructuredPhase;

public class WalkToOaksPhase extends StructuredPhase {
    private WalkToOakAction action;

    public WalkToOaksPhase(ClientContext ctx, String name) {
        super(ctx, name);

        action = new WalkToOakAction(ctx, "ToOaks");

        this.setInitialAction(action);
    }

    @Override
    public boolean moveToNextPhase() {
        return this.action.isComplete();
    }
}
