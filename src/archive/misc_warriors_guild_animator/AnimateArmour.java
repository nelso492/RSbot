package ngc.misc_warriors_guild_animator;


import ngc._resources.Items;
import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class AnimateArmour extends BaseAction<ClientContext> {
    private int helmId;
    private int bodyId;
    private int legsId;
    private int foodId;
    private int animatorId;
    private Area animatorArea;

    public AnimateArmour(ClientContext ctx, int helmId, int bodyId, int legsId, int foodId) {
        super(ctx, "Animating Armor");
        this.helmId = helmId;
        this.bodyId = bodyId;
        this.legsId = legsId;
        this.foodId = foodId;
        this.animatorId = 23955; // Magical Animator
        this.animatorArea = new Area(new Tile(2849, 3454), new Tile(2861, 3545));
    }

    @Override
    public boolean activate() {
/*        System.out.println("------Animate-Armor-------");
        System.out.println(ctx.inventory.select().id(helmId).count() == 1);
        System.out.println(ctx.inventory.select().id(bodyId).count() == 1);
        System.out.println(ctx.inventory.select().id(legsId).count() == 1);
        System.out.println(ctx.inventory.select().id(foodId).count() > 0);
        System.out.println(animatorArea.contains(ctx.players.local()));*/

        return ctx.inventory.select().id(helmId).count() == 1
                && ctx.inventory.select().id(bodyId).count() == 1
                && ctx.inventory.select().id(legsId).count() == 1
                && ctx.inventory.select().id(foodId).count() > 0
                && animatorArea.contains(ctx.players.local())
                && !ctx.groundItems.select().id(Items.WARRIOR_GUILD_TOKEN_8851).nearest().poll().valid();
    }

    @Override
    public void execute() {
        GameObject obj = ctx.objects.select().id(animatorId).nearest().poll();

        if( obj.inViewport() ) {
            obj.interact("Animate", obj.name());

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().id(helmId).count() == 0;
                }
            }, 250, 10);
        } else {
            ctx.movement.step(obj);
        }
    }
}

