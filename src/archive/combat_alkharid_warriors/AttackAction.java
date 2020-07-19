package scripts.combat_alkharid_warriors;

import resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class AttackAction extends BaseAction<ClientContext> {
    private final int[] NPC_ID = {3103};
    private final int IRON_ARROW_ID = 884;



    public AttackAction(ClientContext ctx) {
        super(ctx, "In Combat");
    }

    @Override
    public boolean activate() {
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean playerNotMoving = !ctx.players.local().inMotion();
        boolean minHealth = ctx.combat.health() > 10;
        boolean interacting = ctx.players.local().interacting().valid();
        boolean arrowStackNearby = ctx.groundItems.select().id(IRON_ARROW_ID).select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem arr) {
                return arr.stackSize() >= 2 && arr.inViewport();
            }
        }).poll().valid();
        return !interacting && minHealth && playerNotMoving && playerNotAnimated && !arrowStackNearby;
    }

    @Override
    public void execute() {
        //get your bearings, find nearest valid npc, and slaughter it
        sleep();

        Npc npc = ctx.npcs.select().id(NPC_ID).select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.tile().x() >= 3299 && npc.inViewport() && npc.healthPercent() > 50; //X bound of right side room
            }
        }).nearest().poll();

        npc.interact("Attack");

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().interacting().valid();
            }
        }, 200, 5);

/*        if(sr.takeActionLikely()){
            sr.moveMouse(ctx, -20, 20);
        }*/

    }
}
