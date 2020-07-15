package ngc._resources.actions;


import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Actor;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class WaitForCombatLoot extends BaseAction<ClientContext> {

    public WaitForCombatLoot(ClientContext ctx) {
        super(ctx, "Loot Pause");
    }

    @Override
    public boolean activate() {
        return ctx.players.local().interacting().valid() && ctx.players.local().interacting().healthPercent() == 0;
    }

    @Override
    public void execute() {
        Actor npc = ctx.players.local().interacting();
        Tile npcTile = npc.tile();


        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !npc.valid();
            }
        }, 100, 20);

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !ctx.groundItems.select().at(npcTile).isEmpty();
            }
        }, 100, Random.nextInt(4,8));

    }

}
