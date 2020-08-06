package disabled.fishing_cage_harpoon;


import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;
import shared.templates.AbstractAction;
import shared.tools.AntibanTools;

import static org.powerbot.script.Condition.sleep;

public class DropTuna extends AbstractAction<ClientContext> {
    public DropTuna(ClientContext ctx) {
        super(ctx, "Drop Tuna");
    }

    @Override
    public boolean activate() {
        boolean tuna = ctx.inventory.select().id(359).count() > 0;
        return tuna && ctx.inventory.isFull();
    }

    @Override
    public void execute() {
        // Drop Dem Tunas
        ctx.input.send("{VK_SHIFT down}");
        for( Item i : ctx.inventory.items() ) {
            if( i.id() == 359 ) {
                i.click();
                sleep(AntibanTools.getRandomInRange(250, 350));
            }
        }
        ctx.input.send("{VK_SHIFT up}");
    }
}

