package ngc.misc_warriors_guild_animator;

import ngc._resources.models.BaseAction;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class NavigateAnimatorDoor extends BaseAction<ClientContext> {

    private int[] doorIds;

    private int helmId, bodyId, legsId, foodId;
    private Area animatorArea;

    public NavigateAnimatorDoor(ClientContext ctx, int helmId, int bodyId, int legsId, int foodId) {
        super(ctx, "Animator Door");
        this.doorIds = new int[] {34309, 34306};
        this.helmId = helmId;
        this.bodyId = bodyId;
        this.legsId = legsId;
        this.foodId = foodId;
        this.animatorArea = new Area(new Tile(2849, 3454), new Tile(2861, 3545));
    }

    @Override
    public boolean activate() {
        boolean entering = ctx.inventory.select().id(helmId).count() == 1
                && ctx.inventory.select().id(bodyId).count() == 1
                && ctx.inventory.select().id(legsId).count() == 1
                && ctx.inventory.select().id(foodId).count() > 0
                && !animatorArea.contains(ctx.players.local());


        boolean leaving =
                ctx.inventory.select().id(helmId).count() == 1
                        && ctx.inventory.select().id(bodyId).count() == 1
                        && ctx.inventory.select().id(legsId).count() == 1
                        && ctx.inventory.select().id(foodId).count() == 0
                        && animatorArea.contains(ctx.players.local());

        return (entering || leaving) && ctx.objects.select().id(doorIds).nearest().peek().inViewport();
    }

    @Override
    public void execute() {
        // walk over to door
        GameObject door = ctx.objects.poll();

        if( door.valid() ) {
            // Open Door
            door.interact("Open", door.name());

            // Wait to pass through
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().tile().y() == 3546;
                }
            }, 250, 20);
        }
    }
}
