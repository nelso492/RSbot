package ngc._resources.actions;

import ngc._resources.actions._config.WalkConfig;
import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.rt4.ClientContext;


import static org.powerbot.script.Condition.sleep;

public class WalkToAreaAction extends BaseAction<ClientContext> {
    private final WalkConfig walkConfig;

    public WalkToAreaAction(ClientContext ctx, WalkConfig walkConfig) {
        super(ctx, "Walking");

        this.walkConfig = walkConfig;
    }

    @Override
    public boolean activate() {
        return !this.walkConfig.getTargetArea().contains(ctx.players.local()) && (!this.walkConfig.isActivateOnInvCount() || this.walkConfig.getActivationInvCount() == ctx.inventory.count());
    }

    @Override
    public void execute() {
        this.walkConfig.getPath()[this.walkConfig.getPath().length - 1] = this.walkConfig.getTargetArea().getRandomTile();
        ctx.movement.newTilePath(this.walkConfig.getPath()).traverse();
        sleep();
    }
}

