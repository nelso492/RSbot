package resources.actions;

import resources.models.BaseAction;
import resources.tools.CommonActions;
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

/**
 * Step to a min distance from interacting NPC.
 */
public class MoveAwayFromTarget extends BaseAction<ClientContext> {

    private final int minDistanceToTarget;

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

        // Closest to player
        Collections.sort(destinationTiles, new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                return (int) ((o1.distanceTo(playerTile) - o2.distanceTo(playerTile)) * 100);
            }
        });

        for (Tile t : destinationTiles) {

            // move to "safe" tile
            if (t.matrix(ctx).reachable()) {
                CommonActions.walkToSafespot(ctx, t);
                sleep();

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.players.local().inMotion();
                    }
                }, 150, 30);

                if (ctx.players.local().tile().distanceTo(npc) >= minDistanceToTarget) {
                    break;
                }
            }

        }
    }


}
