package disabled.woodcutting_oak_varrock.phases;

import org.powerbot.script.rt4.ClientContext;
import disabled.woodcutting_oak_varrock.actions.ChopOakAction;
import shared.templates.StructuredPhase;

public class OakPhase extends StructuredPhase {
    private final ChopOakAction action;

    public OakPhase(ClientContext ctx, String name) {
        super(ctx, name);

        action = new ChopOakAction(ctx, "Oaks");

        this.setInitialAction(action);
    }

    @Override
    public boolean moveToNextPhase() {
        return this.action.isComplete();
    }
}
