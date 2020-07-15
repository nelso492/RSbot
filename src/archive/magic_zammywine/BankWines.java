package ngc.magic_zammywine;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.enums.ITEM_IDS;
import ngc._resources.functions.CommonAreas;
import ngc._resources.functions.RsLookup;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class BankWines extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();
    private CommonAreas areas = new CommonAreas();

    private int zammyWineId = lookup.getId(ITEM_IDS.WineOfZamorak_245);



    public BankWines(ClientContext ctx) {
        super(ctx, "Banking");
    }

    @Override
    public boolean activate() {
         return ctx.bank.inViewport() && ctx.inventory.isFull();
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
            ctx.bank.deposit(zammyWineId, Bank.Amount.ALL);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.count() == 2;
                }
            }, 150, 4);
        } else {
            ctx.movement.step(ctx.bank.nearest());
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.inViewport();
                }
            }, 350, 10);
        }
    }
}
