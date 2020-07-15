package ngc._resources.actions;

import ngc._resources.Items;
import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Magic;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class HighAlch extends BaseAction<ClientContext> {
    private int[] alchableItemIds;
    private int natureRuneId;
    private int fireRuneId;
    private boolean backToInventory;
    private boolean usingStaff;

    public HighAlch(ClientContext ctx, String status, int[] alchableItemIds, boolean usingStaff, boolean backToInventory) {
        super(ctx, status);
        this.alchableItemIds = alchableItemIds;
        this.natureRuneId = Items.NATURE_RUNE_561;
        this.fireRuneId = Items.FIRE_RUNE_554;
        this.backToInventory = backToInventory;
        this.usingStaff = usingStaff;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(alchableItemIds).count() > 0 &&
                ctx.inventory.select().id(natureRuneId).count() == 1 &&
                ((ctx.inventory.select().id(fireRuneId).count() == 1 &&
                        ctx.inventory.select().id(fireRuneId).poll().stackSize() >= 5) || usingStaff);
    }


    @Override
    public void execute() {
        // Open Magic Tab
        ctx.game.tab(Game.Tab.MAGIC);

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

                sleep();
                ctx.game.tab(Game.Tab.INVENTORY);
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
