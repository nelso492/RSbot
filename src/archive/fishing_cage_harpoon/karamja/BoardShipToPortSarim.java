package scripts.fishing_cage_harpoon.karamja;

import shared.templates.AbstractAction;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class BoardShipToPortSarim extends AbstractAction<ClientContext> {
    private int SEAMAN_ID;

    public BoardShipToPortSarim(ClientContext ctx, int customsOfficerId) {
        super(ctx, "To Ship");
        this.SEAMAN_ID = customsOfficerId;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 28 && ctx.npcs.select().id(SEAMAN_ID).poll().inViewport();
    }

    @Override
    public void execute() {
        ctx.npcs.select().id(SEAMAN_ID).poll().interact("Pay-Fare");
        sleep();
    }
}
