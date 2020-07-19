package shared.actions;

import shared.models.BaseAction;
import org.powerbot.script.rt4.ClientContext;

/**
 * Toggle run status based on min energy percentage.
 */
public class ToggleRunAction extends BaseAction<ClientContext> {
    private int minPercent;

    public ToggleRunAction(ClientContext ctx, String status, int minPercent) {
        super(ctx, status);
        this.minPercent = minPercent;
    }

    @Override
    public boolean activate() {
        return ctx.movement.energyLevel() > this.minPercent && !ctx.movement.running();
    }


    @Override
    public void execute() {
        ctx.movement.running(true);
    }

}
