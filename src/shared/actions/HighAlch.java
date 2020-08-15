package shared.actions;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Magic;
import shared.constants.Items;
import shared.templates.AbstractAction;
import shared.tools.AntibanTools;
import shared.tools.CommonActions;
import shared.tools.GaussianTools;

import java.util.concurrent.Callable;

/**
 * High alch setup with options for staff use and inventory swap.
 */
public class HighAlch extends AbstractAction<ClientContext> {
    private int[] alchableItemIds;
    private boolean backToInventory;
    private boolean usingStaff;

    private Item alchTarget;

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
        this.alchTarget = ctx.inventory.select().id(alchableItemIds).first().poll();

        return ctx.inventory.select().id(alchableItemIds).count() > 0 &&
                ctx.inventory.select().id(Items.NATURE_RUNE_561).count() == 1 &&
                ((ctx.inventory.select().id(Items.FIRE_RUNE_554).count() == 1 &&
                        ctx.inventory.select().id(Items.FIRE_RUNE_554).poll().stackSize() >= 5) || usingStaff);
    }


    @Override
    public void execute() {
        // Open Magic Tab
        if (ctx.game.tab() != Game.Tab.MAGIC) {
            CommonActions.openTab(ctx, Game.Tab.MAGIC);
        }

        // Cast High Alch if not already
        if (!ctx.magic.casting(Magic.Spell.HIGH_ALCHEMY)) {
            ctx.magic.cast(Magic.Spell.HIGH_ALCHEMY);
        }

        // Confirm target exists
        if (alchTarget.valid()) {

            // Cast Spell
            ctx.input.click(alchTarget.nextPoint(), 1);

            // Return to inventory
            if (backToInventory) {
                // Wait for animation to complete
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() == -1;
                    }
                }, 250, 20);

                CommonActions.openTab(ctx, Game.Tab.INVENTORY);
            } else {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.game.tab() == Game.Tab.MAGIC;
                    }
                }, 250, 15);

                if (GaussianTools.takeActionNever()) {
                    AntibanTools.sleepDelay(AntibanTools.getRandomInRange(0, 5));
                }
            }
        }
    }
}
