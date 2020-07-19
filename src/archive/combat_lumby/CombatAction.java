package scripts.combat_lumby;


import shared.models.BaseAction;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

import static org.powerbot.script.Condition.sleep;

public class CombatAction extends BaseAction<ClientContext> {
    private final int[] COW_IDS = {2807, 2806, 2808, 2809, 2805};


    public CombatAction(ClientContext ctx) {
        super(ctx, "Combat");
    }

    @Override
    public boolean activate() {
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean playerNotInCombat = !ctx.players.local().inCombat();
        boolean playerNotMoving = !ctx.players.local().inMotion();
        boolean minHealth = ctx.players.local().healthPercent() > 20;
        //  System.out.println("Animated: " + (playerNotAnimated ? "Y" : "N"));
        //  System.out.println("INV Full: " + (invNotFull ? "N" : "Y"));
        return playerNotAnimated && minHealth && playerNotInCombat && playerNotMoving;
    }

    @Override
    public void execute() {
        //find nearest valid cow and slaughter it
        Npc cow = ctx.npcs.select().id(COW_IDS).select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return !npc.inCombat();
            }
        }).nearest().poll();

        if(!ctx.players.local().inCombat()){
            if(cow.inViewport()) {
                cow.interact("Attack");
            }else{
                ctx.camera.turnTo(cow);
            }
        }

        sleep();
        sleep();
    }
}
