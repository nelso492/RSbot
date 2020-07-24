package scripts.fishing_cage_catherby.phases;

import org.powerbot.script.rt4.ClientContext;
import scripts.fishing_cage_catherby.actions.WalkToCatherbyBankAction;
import scripts.fishing_cage_catherby.actions.WalkToFishingAction;
import shared.actions.BankAction;
import shared.constants.Items;
import shared.templates.StructuredPhase;
import shared.tools.CommonAreas;

/**
 * Walk to, bank at, and walk from Catherby bank
 */
public class BankingPhase extends StructuredPhase {
    public BankingPhase(ClientContext ctx, String name) {
        super(ctx, name);

        WalkToCatherbyBankAction walkToCatherbyBankAction = new WalkToCatherbyBankAction(ctx, "To Bank");
        BankAction bankAction = new BankAction(ctx, "Deposit Fish", 0, 0, Items.LOBSTER_POT_301, -1, -1, -1, true, false, false, CommonAreas.catherbyBank());
        WalkToFishingAction walkToFishingAction = new WalkToFishingAction(ctx, "To Fishing");

        walkToCatherbyBankAction.setNextAction(bankAction);
        bankAction.setNextAction(walkToFishingAction);

        this.setInitialAction(walkToCatherbyBankAction);
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.npcs.select().id(1519).nearest().poll().inViewport() && ctx.inventory.select().count() == 1;
    }
}
