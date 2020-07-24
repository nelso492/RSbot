package scripts.fishing_cage_catherby.phases;

import org.powerbot.script.rt4.ClientContext;
import scripts.fishing_cage_catherby.actions.CageFishingAction;
import shared.templates.StructuredPhase;

public class CatherbyCageFishingPhase extends StructuredPhase {

    public CatherbyCageFishingPhase(ClientContext ctx, String name) {
        super(ctx, name);

        CageFishingAction cageFishingAction = new CageFishingAction(ctx, "Cage");

        this.setInitialAction(cageFishingAction);
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.isFull();
    }
}
