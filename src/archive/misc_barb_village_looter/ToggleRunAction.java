package scripts.misc_barb_village_looter;


import shared.templates.AbstractAction;
import org.powerbot.script.rt4.ClientContext;

public class ToggleRunAction extends AbstractAction<ClientContext> {

    public ToggleRunAction(ClientContext ctx) {
        super(ctx, "Toggling Run");
    }

    @Override
    public boolean activate() {
        return !ctx.movement.running() && ctx.movement.energyLevel() > 50;
    }

    @Override
    public void execute() {
        ctx.movement.running(true);
    }
}
