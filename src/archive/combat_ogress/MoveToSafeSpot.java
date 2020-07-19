package ngc.combat_ogress;

import resources.models.BaseAction;
import resources.tools.CommonAreas;
import resources.tools.GaussianTools;
import resources.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class MoveToSafeSpot extends BaseAction<ClientContext> {
    private Tile safeTile;
    private int[] npcs;
    private LootList lootList;

    private Area safeZone;

    public MoveToSafeSpot(ClientContext ctx, String status, Tile safeTile, int[] npcs, LootList lootList) {
        super(ctx, status);
        this.safeTile = safeTile;
        this.npcs = npcs;
        this.lootList = lootList;
        this.safeZone = CommonAreas.ogressSafeZone();
    }

    @Override
    public boolean activate() {

        Npc target = ctx.npcs.select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.interacting().name().equals(ctx.players.local().name());
            }
        }).peek();


        boolean waiting = !ctx.players.local().interacting().valid() && !ctx.groundItems.select().id(lootList.allItemIds()).nearest().peek().inViewport();
        boolean inSafeTile = (safeTile.distanceTo(ctx.players.local()) == 0);
        return ctx.game.floor() == 1
                && ((target.valid() && (ctx.players.local().tile().distanceTo(target) <= 2 || (ctx.players.local().tile().y() <= 8999 && target.tile().y() < 8999)) && !inSafeTile) || ctx.players.local().healthBarVisible() || waiting)
                && (!ctx.players.local().inMotion() && ctx.players.local().tile().distanceTo(safeTile) > 0);
    }


    @Override
    public void execute() {
        Npc npc = ctx.npcs.select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.interacting().name().equals(ctx.players.local().name());
            }
        }).poll();

        if( safeTile.distanceTo(ctx.players.local()) >= 4 ) {
            if(GaussianTools.takeActionNormal()){
                ctx.camera.pitch(Random.nextInt(30, 70));
            }
            ctx.movement.step(safeZone.getRandomTile());

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().tile().y() > 9001 || (!npc.valid() || !npc.inMotion());
                }
            }, 250, 40);

        }
        int x = safeTile.matrix(ctx).centerPoint().x + Random.nextInt(-5, 5);
        int y = safeTile.matrix(ctx).centerPoint().y + Random.nextInt(-5, 5);
        ctx.input.move(new Point(x, y));
        sleep(250);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.menu != null && ctx.menu.items() != null && ctx.menu.items().length > 0;
            }
        }, 250, 20);
        String action = ctx.menu.items()[0];

        if( action.contains("Walk") ) {
            safeTile.matrix(ctx).click();
        } else {
            safeTile.matrix(ctx).interact("Walk here");
        }

        sleep(GaussianTools.getRandomGaussian(2000, 300));

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().tile().y() > 9001 || (!npc.valid() || !npc.inMotion())
                        ;//ctx.players.local().tile().x() == npc.tile().x() || ctx.players.local().tile().y() == npc.tile().y());
            }
        }, 250, 40);


        // npc.interact("Attack");

    }

}
