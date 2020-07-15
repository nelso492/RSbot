package ngc._resources.actions;

import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonFunctions;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Actor;
import org.powerbot.script.rt4.ClientContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class MoveAwayFromTarget extends BaseAction<ClientContext> {

    private int minDistanceToTarget;

    public MoveAwayFromTarget(ClientContext ctx, int minDistanceToTarget) {
        super(ctx, "Moving");
        this.minDistanceToTarget = minDistanceToTarget;

    }

    @Override
    public boolean activate() {
        return ctx.players.local().interacting().valid() && ctx.players.local().tile().distanceTo(ctx.players.local().interacting()) < minDistanceToTarget;
    }


    @Override
    public void execute() {
        Actor npc = ctx.players.local().interacting();
        Tile playerTile = ctx.players.local().tile();

        ArrayList<Tile> destinationTiles = new ArrayList<>();
        int secondaryOffset = Random.nextInt(-3, 3);

        // Add new destination tiles
        destinationTiles.add(new Tile(npc.tile().x() + secondaryOffset, npc.tile().y() + minDistanceToTarget + 1)); // N
        destinationTiles.add(new Tile(npc.tile().x() + secondaryOffset, npc.tile().y() - minDistanceToTarget)); // S
        destinationTiles.add(new Tile(npc.tile().x() - minDistanceToTarget, npc.tile().y() + secondaryOffset + 1)); // E
        destinationTiles.add(new Tile(npc.tile().x() + minDistanceToTarget, npc.tile().y() + secondaryOffset)); // W

        // Sort by distance from player
        //GroundItem i = ctx.groundItems.select().id(this.lootIds).nearest().poll();

       /* if( i.valid() ) {
            // Sort closest to loot drop
            Collections.sort(destinationTiles, new Comparator<Tile>() {
                @Override
                public int compare(Tile o1, Tile o2) {
                    return (int) ((o1.distanceTo(i.tile()) - o2.distanceTo(i.tile())) * 100);
                }
            });
        } else {*/
        // Closest to player
        Collections.sort(destinationTiles, new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                return (int) ((o1.distanceTo(playerTile) - o2.distanceTo(playerTile)) * 100);
            }
        });
        //}
        for( Tile t : destinationTiles ) {

            // move to "safe" tile
            if( t.matrix(ctx).reachable() ) {
                CommonFunctions.walkToSafespot(ctx, t);
                sleep();

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.players.local().inMotion();
                    }
                }, 150, 30);

                if( ctx.players.local().tile().distanceTo(npc) >= minDistanceToTarget ) {
                    break;
                }
            }

        }
    }


}
