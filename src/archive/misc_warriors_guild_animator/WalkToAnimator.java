package ngc.misc_warriors_guild_animator;

import resources.models.BaseAction;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToAnimator extends BaseAction<ClientContext> {

    private int[] doorIds;
    private Tile[] pathToAnimator;

    private int helmId, bodyId, legsId, foodId;
    private Area animatorArea;

    public WalkToAnimator(ClientContext ctx, int helmId, int bodyId, int legsId, int foodId) {
        super(ctx, "Bank to Animator");
        this.doorIds = new int[] {34309, 34306};
        this.helmId = helmId;
        this.bodyId = bodyId;
        this.legsId = legsId;
        this.foodId = foodId;
        this.animatorArea = new Area(new Tile(2849, 3454), new Tile(2861, 3545));
        this.pathToAnimator = new Tile[] {new Tile(2843, 3543, 0), new Tile(2847, 3544, 0), new Tile(2851, 3546, 0)};
    }

    @Override
    public boolean activate() {

/*        System.out.println("------Walk-To-Animator-------");
        System.out.println(ctx.inventory.select().id(helmId).count() == 1);
        System.out.println(ctx.inventory.select().id(bodyId).count() == 1);
        System.out.println(ctx.inventory.select().id(legsId).count() == 1);
        System.out.println(ctx.inventory.select().id(foodId).count() == 1);
        System.out.println(!animatorArea.contains(ctx.players.local()));
        System.out.println(!ctx.objects.select().id(doorIds).nearest().poll().inViewport());*/

        return ctx.inventory.select().id(helmId).count() == 1
                && ctx.inventory.select().id(bodyId).count() == 1
                && ctx.inventory.select().id(legsId).count() == 1
                && ctx.inventory.select().id(foodId).count() > 0
                && !animatorArea.contains(ctx.players.local())
                && !ctx.objects.select().id(doorIds).nearest().poll().inViewport();

    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(pathToAnimator).traverse();
        sleep();
    }
}
