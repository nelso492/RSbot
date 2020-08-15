package scripts.woodcutting_oak_draynor.phases;

import org.powerbot.script.rt4.ClientContext;
import scripts.woodcutting_oak_draynor.actions.DraynorOakAction;
import scripts.woodcutting_oak_draynor.actions.DraynorWalkToOakAction;
import shared.templates.StructuredPhase;

public class DraynorOakPhase extends StructuredPhase {
    private final DraynorOakAction action;
    private final DraynorWalkToOakAction walkToOakAction;
    public DraynorOakPhase(ClientContext ctx, String name) {
        super(ctx, name);
        walkToOakAction = new DraynorWalkToOakAction(ctx, "Walk");
        action = new DraynorOakAction(ctx, "Oaks");

        walkToOakAction.setNextAction(action);

        this.setInitialAction(walkToOakAction);
    }

    @Override
    public boolean moveToNextPhase() {
        return this.action.isComplete();
    }
}
