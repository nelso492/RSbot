package disabled.woodcutting_oak_varrock.actions;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import shared.templates.StructuredAction;
import shared.tools.AntibanTools;

import java.util.concurrent.Callable;

/**
 * [Description]
 */
public class ChopOakAction extends StructuredAction {

    public ChopOakAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        return ctx.players.local().animation() == -1 && !ctx.inventory.isFull();
    }

    @Override
    public void execute() {
        GameObject oakTree = ctx.objects.select().id(10820).nearest().poll();

        if (oakTree.inViewport()) {
            AntibanTools.sleepDelay(AntibanTools.getRandomInRange(0, 3));
            oakTree.click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() != -1;
                }
            }, 250, 6);
        } else {
            ctx.camera.turnTo(oakTree);
        }
    }

    @Override
    public boolean isComplete() {
        return ctx.inventory.isFull() && ctx.objects.select().id(10820).nearest().poll().inViewport();
    }
}
