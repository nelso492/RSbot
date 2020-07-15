package ngc._resources.actions;

import ngc._resources.actions._config.RunConfig;
import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.rt4.ClientContext;

public class ToggleRunAction extends BaseAction<ClientContext> {
    private RunConfig config;

    public ToggleRunAction(ClientContext ctx, String status, RunConfig _config) {
        super(ctx, status);
        config = _config;
    }

    @Override
    public boolean activate() {
        return ctx.movement.energyLevel() > config.getEnergyPercent() && !ctx.movement.running();
    }


    @Override
    public void execute() {
        ctx.movement.running(true);
    }

}
