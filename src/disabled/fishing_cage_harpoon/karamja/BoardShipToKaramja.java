package disabled.fishing_cage_harpoon.karamja;

import shared.templates.AbstractAction;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class BoardShipToKaramja extends AbstractAction<ClientContext> {
    private int SEAMAN_ID;

    public BoardShipToKaramja(ClientContext ctx, int SEAMAN_ID) {
        super(ctx, "To Ship");
        this.SEAMAN_ID = SEAMAN_ID;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 2 && ctx.npcs.select().id(SEAMAN_ID).poll().inViewport();
    }

    @Override
    public void execute() {
        ctx.npcs.select().id(SEAMAN_ID).poll().interact("Pay-fare");
        sleep();
    }
}
