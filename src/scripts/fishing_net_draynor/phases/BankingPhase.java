package scripts.fishing_net_draynor.phases;

import org.powerbot.script.rt4.ClientContext;
import scripts.fishing_net_draynor.actions.WalkToDraynorBankAction;
import scripts.fishing_net_draynor.actions.WalkToFishingAction;
import shared.actions.BankAction;
import shared.constants.Items;
import shared.templates.StructuredPhase;
import shared.tools.CommonAreas;

/**
 * Walk to, bank at, and walk from Draynor bank
 */
public class BankingPhase extends StructuredPhase {
    public BankingPhase(ClientContext ctx, String name) {
        super(ctx, name);

        WalkToDraynorBankAction walkToDraynorBankAction = new WalkToDraynorBankAction(ctx, "To Bank");
        BankAction bankAction = new BankAction(ctx, "Deposit Fish", Items.RAW_SHRIMPS_317, Items.RAW_ANCHOVIES_321, -1, -1, -1, -1, true, false, false, CommonAreas.getDraynorBank());
        WalkToFishingAction walkToFishingAction = new WalkToFishingAction(ctx, "To Fishing");

        walkToDraynorBankAction.setNextAction(bankAction);
        bankAction.setNextAction(walkToFishingAction);

        this.setInitialAction(walkToDraynorBankAction);
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.npcs.select().id(1525).nearest().poll().inViewport() && ctx.inventory.select().count() == 1;
    }
}
