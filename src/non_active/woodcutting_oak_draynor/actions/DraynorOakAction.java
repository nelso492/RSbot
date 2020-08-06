package scripts.woodcutting_oak_draynor.actions;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import shared.templates.StructuredAction;
import shared.tools.CommonAreas;
import shared.tools.GaussianTools;

import java.util.concurrent.Callable;

/**
 * [Description]
 */
public class DraynorOakAction extends StructuredAction {

    private GameObject oakTree;

    public DraynorOakAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        this.oakTree = ctx.objects.select().id(10820).nearest().poll();

        return ctx.players.local().animation() == -1 && !ctx.inventory.isFull() && this.oakTree.valid() && this.oakTree.tile().y() < 3246;
    }

    @Override
    public void execute() {
        if (oakTree.inViewport()) {
            oakTree.click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() != -1;
                }
            }, 250, 6);
        } else if (CommonAreas.getDraynorBank().contains(ctx.players.local())) {
            if (GaussianTools.takeActionLikely())
                ctx.camera.turnTo(oakTree);

            ctx.movement.step(oakTree);
        }
    }

    @Override
    public boolean isComplete() {
        return ctx.inventory.isFull();
    }
}
