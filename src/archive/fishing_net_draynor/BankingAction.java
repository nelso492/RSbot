package ngc.fishing_net_draynor;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.enums.ITEM_IDS;
import ngc._resources.functions.CommonAreas;
import ngc._resources.functions.RsLookup;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class BankingAction extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();
    private CommonAreas areas = new CommonAreas();

    private final int NETID = lookup.getId(ITEM_IDS.SmallNet_303);
    private int[] fishIds = {lookup.getId(ITEM_IDS.RawAchovy_321), lookup.getId(ITEM_IDS.RawShrimps_317)};

    private Area draynorBank = areas.getDraynorBank();


    public BankingAction(ClientContext ctx) {
        super(ctx, "Banking");
    }

    @Override
    public boolean activate() {
        // Full inventory or no net.
        return ctx.inventory.select().count() == 28 && draynorBank.contains(ctx.players.local());
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
                    return ctx.inventory.select().id(fishIds).count() == 0;
                }
            }, 150, 4);
            ctx.bank.withdraw(NETID, 1);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().id(NETID).count() == 1;
                }
            }, 150, 4);
/*
            if( ctx.inventory.isEmpty() ) {
                ctx.bank.withdraw(NETID, 1);
                sleep();
            }*/
        }/* else {
            ctx.movement.step(bankBooth);
        }*/

    }
}
