package scripts.cooking_lumby_castle.phases;

import scripts.cooking_lumby_castle.actions.WalkLumbyBankToStairs;
import scripts.cooking_lumby_castle.actions.WalkStairsToLumbyBank;
import shared.actions.BankAction;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredPhase;
import shared.tools.CommonAreas;

/**
 * Walks from the top level stairs to the Lumby bank
 */
public class BankingPhase extends StructuredPhase {
    //region Actions
    WalkStairsToLumbyBank walkStairsToBankAction;
    BankAction bankAction;
    WalkLumbyBankToStairs walkBankToStairsAction;
    //endregion

    //region Vars
    private final int rawId;
    //endregion


    public BankingPhase(ClientContext ctx, int rawId) {
        super(ctx, "BANK");
        this.rawId = rawId;

        // Define Actions
        this.walkStairsToBankAction = new WalkStairsToLumbyBank(ctx, rawId);
        this.bankAction = new BankAction(ctx, "Banking", 0, -1, rawId, 28, -1, -1, false, true, false, CommonAreas.lumbridgeCastleBank());
        this.walkBankToStairsAction = new WalkLumbyBankToStairs(ctx, rawId);

        // Connect actions
        this.walkStairsToBankAction.setNextAction(this.bankAction);
        this.bankAction.setNextAction(this.walkBankToStairsAction);

        this.setInitialAction(this.walkStairsToBankAction);
    }


    @Override
    public boolean moveToNextPhase() {

        return this.walkBankToStairsAction.isComplete();
    }


    public int getRawId() {
        return rawId;
    }

}
