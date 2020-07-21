package scripts.woodcutting_oak_draynor.phases;

import org.powerbot.script.rt4.ClientContext;
import scripts.woodcutting_oak_draynor.actions.DraynorWalkToBankAction;
import shared.actions.BankAction;
import shared.constants.Items;
import shared.templates.StructuredPhase;
import shared.tools.CommonAreas;

public class DraynorBankPhase extends StructuredPhase {

    public DraynorBankPhase(ClientContext ctx, String name, int axeId) {
        super(ctx, name);

        DraynorWalkToBankAction walkToBankAction = new DraynorWalkToBankAction(ctx, "Walking");
        BankAction action = new BankAction(ctx, "Bank", Items.OAK_LOGS_1521,0,axeId,0,0,0,true,false,false, CommonAreas.getGeAreaEast());

        walkToBankAction.setNextAction(action);

        this.setInitialAction(walkToBankAction);
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.select().id(Items.OAK_LOGS_1521).count() == 0;
    }
}
