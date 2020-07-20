package scripts.woodcutting_oak_varrock.phases;

import org.powerbot.script.rt4.ClientContext;
import shared.actions.BankAction;
import shared.constants.Items;
import shared.templates.StructuredPhase;
import shared.tools.CommonAreas;

public class BankingPhase extends StructuredPhase {

    public BankingPhase(ClientContext ctx, String name, int axeId) {
        super(ctx, name);

        BankAction action = new BankAction(ctx, "Bank", Items.OAK_LOGS_1521,0,axeId,0,0,0,true,false,false, CommonAreas.getGeAreaEast());

        this.setInitialAction(action);
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.select().id(Items.OAK_LOGS_1521).count() == 0;
    }
}
