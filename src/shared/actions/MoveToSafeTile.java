package shared.actions;

import shared.templates.AbstractAction;
import shared.tools.CommonActions;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

/**
 * Move to a predefined safe tile when interacting.
 */
public class MoveToSafeTile extends AbstractAction<ClientContext> {

    private final Tile safeTile;

    public MoveToSafeTile(ClientContext ctx, Tile safeTile) {
        super(ctx, "Moving");
        this.safeTile = safeTile;
    }

    @Override
    public boolean activate() {
        return ctx.players.local().interacting().valid() && safeTile.distanceTo(ctx.players.local()) > 0 && !ctx.players.local().inMotion();
    }


    @Override
    public void execute() {
        if( safeTile.matrix(ctx).inViewport() ) {
            CommonActions.walkToSafespot(ctx, safeTile);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return safeTile.distanceTo(ctx.players.local()) == 0;
                }
            }, 250, 20);
        } else {
            ctx.movement.step(safeTile);
        }
    }

}
