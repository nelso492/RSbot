package ngc.cmb_brutal_black_dragons;

import ngc._resources.Items;
import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.GaussianTools;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class BbdBankAction extends BaseAction<ClientContext> {

    public BbdBankAction(ClientContext ctx) {
        super(ctx, "");
    }

    @Override
    public boolean activate() {
        return ctx.bank.inViewport() && ctx.players.local().tile().y() < 3285;
    }


    @Override
    public void execute() {

        // Check for bank deposit box open
        if(ctx.depositBox.opened()){
            ctx.depositBox.close();
            sleep(500);
        }

        // Open Bank
        if( !ctx.bank.opened() ) {
            ctx.bank.open();
        }

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.bank.open();
            }
        }, 200, 10);

        if( ctx.bank.opened() ) {

            if( ctx.bank.currentTab() != 0 ) {
                ctx.bank.currentTab(0);
            }

            // Set quantity to ALL
            if( ctx.inventory.select().count() > 7 ) {
                ctx.bank.withdrawModeQuantity(Bank.Amount.ALL);

                for( Item item : ctx.inventory.select() ) {
                    if( item.valid() && (item.inventoryIndex() == 0 || ctx.inventory.itemAt(item.inventoryIndex() - 1).id() != item.id()) ) {
                        if( !item.name().contains("bolt") && !item.name().contains("teleport") && item.id() != Items.XERICS_TALISMAN_13393 ) {
                            item.click();
                            sleep(Random.nextInt(100, 275));
                        }
                    }
                }
            }

            // set quantity to 1
            if( ctx.bank.withdrawModeQuantity() != Bank.Amount.ONE ) {
                ctx.bank.withdrawModeQuantity(Bank.Amount.ONE);
                sleep(GaussianTools.getRandomGaussian(120, 45));
            }

            // Withdraw Pots
            if( ctx.inventory.select().id(Items.RANGING_POTION1_173).count() == 0 ) {
                ctx.bank.withdraw(Items.RANGING_POTION1_173, Bank.Amount.ONE);
            }
            if( ctx.inventory.select().id(Items.EXTENDED_ANTIFIRE1_11957).count() == 0 ) {
                ctx.bank.select().id(Items.EXTENDED_ANTIFIRE1_11957).poll().click();
                sleep(GaussianTools.getRandomGaussian(200, 45));
            }
            if( ctx.inventory.select().id(Items.PRAYER_POTION3_139).count() != 1 ) {
                ctx.bank.select().id(Items.PRAYER_POTION3_139).poll().click();
                //ctx.bank.select().id(Items.PRAYER_POTION1_143).poll().click();
                sleep(GaussianTools.getRandomGaussian(200, 45));
            }

            // Withdraw Extras
            if( ctx.inventory.select().id(Items.XERICS_TALISMAN_13393).count() != 1 ) {
                ctx.bank.withdraw(Items.XERICS_TALISMAN_13393, Bank.Amount.ONE);
                sleep(GaussianTools.getRandomGaussian(100, 45));

            }
            if( ctx.inventory.select().id(Items.FALADOR_TELEPORT_8009).count() != 1 ) {
                ctx.bank.withdraw(Items.FALADOR_TELEPORT_8009, Bank.Amount.ALL);
                sleep(GaussianTools.getRandomGaussian(112, 14));
            }


            // If needed, withdraw food
            //ctx.bank.withdrawModeQuantity(Bank.Amount.TEN);
            //sleep();

            if(ctx.combat.healthPercent() > 60){
                ctx.bank.select().id(Items.JUG_OF_WINE_1993).poll().click();
                ctx.bank.select().id(Items.JUG_OF_WINE_1993).poll().click();
                ctx.bank.select().id(Items.JUG_OF_WINE_1993).poll().click();
            }else{
                ctx.bank.withdraw(Items.JUG_OF_WINE_1993, Bank.Amount.FIVE);
            }

            sleep(GaussianTools.getRandomGaussian(870, 36));


            // Close Bank
            ctx.bank.close();
        }
    }

}
