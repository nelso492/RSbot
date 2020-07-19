package shared.actions;

import shared.constants.Items;
import shared.models.BaseAction;
import shared.tools.CommonActions;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Magic;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

/**
 * High alch setup with options for staff use and inventory swap.
 */
public class HighAlch extends BaseAction<ClientContext> {
    private int[] alchableItemIds;
    private boolean backToInventory;
    private boolean usingStaff;

    public HighAlch(ClientContext ctx, String status, int[] alchableItemIds, boolean usingStaff, boolean backToInventory) {
        super(ctx, status);
        this.alchableItemIds = alchableItemIds;
        this.backToInventory = backToInventory;
        this.usingStaff = usingStaff;
    }

    public HighAlch(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(alchableItemIds).count() > 0 &&
                ctx.inventory.select().id(Items.NATURE_RUNE_561).count() == 1 &&
                ((ctx.inventory.select().id(Items.FIRE_RUNE_554).count() == 1 &&
                        ctx.inventory.select().id(Items.FIRE_RUNE_554).poll().stackSize() >= 5) || usingStaff);
    }


    @Override
    public void execute() {
        // Open Magic Tab
        CommonActions.openTab(ctx, Game.Tab.MAGIC);

        // Get a list of the items to cast high alch on
        for( Item i : ctx.inventory.select().id(alchableItemIds) ) {
            if( !ctx.magic.casting(Magic.Spell.HIGH_ALCHEMY) ) {
                ctx.magic.cast(Magic.Spell.HIGH_ALCHEMY);
            }

            // Cast Spell
            i.click();
            if( backToInventory ) {

                // Wait for animation to complete
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() == -1;
                    }
                }, 250, 20);

                CommonActions.openTab(ctx, Game.Tab.INVENTORY);
            }else{
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.game.tab() == Game.Tab.MAGIC;
                    }
                }, 250, 15);
            }


        }


    }

}
