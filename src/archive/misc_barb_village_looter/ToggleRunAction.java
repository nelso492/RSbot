package ngc.misc_barb_village_looter;


import ngc._resources.models.BaseAction;
import org.powerbot.script.rt4.ClientContext;

public class ToggleRunAction extends BaseAction<ClientContext> {

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
