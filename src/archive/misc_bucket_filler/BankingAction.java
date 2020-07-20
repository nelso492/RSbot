package scripts.misc_bucket_filler;


import shared.templates.AbstractAction;
import shared.enums.ITEM_IDS;
import shared.tools.CommonAreas;
import shared.tools.RsLookup;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class BankingAction extends AbstractAction<ClientContext> {
    private RsLookup lookup = new RsLookup();
    private CommonAreas areas = new CommonAreas();

    private Area geArea = areas.getGeAreaEast();


    public BankingAction(ClientContext ctx) {
        super(ctx, "Banking");
    }

    @Override
    public boolean activate() {
        // Full inventory or no net.
        return ctx.inventory.select().id(lookup.getId(ITEM_IDS.BucketOfWater_1929)).count() == 28 && geArea.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        if( ctx.bank.inViewport() ) {
            ctx.bank.open();

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.opened();
                }
            }, 150, 4);
            ctx.bank.depositInventory();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.isEmpty();
                }
            }, 150, 4);

            ctx.bank.withdraw(lookup.getId(ITEM_IDS.EmptyBucket_1925), Bank.Amount.ALL);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.isFull();
                }
            }, 150, 4);


        }
    }
}
