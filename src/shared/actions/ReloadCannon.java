package shared.actions;

import shared.constants.Items;
import shared.models.BaseAction;
import shared.tools.CommonActions;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

/**
 * Locate and reload a placed multicannon
 */
public class ReloadCannon extends BaseAction<ClientContext> {

    private final Tile cannonTile;

    public ReloadCannon(ClientContext ctx, Tile cannonTile) {
        super(ctx, "Reloading Cannon");
        this.cannonTile = cannonTile;
    }

    @Override
    public boolean activate() {
        return ctx.varpbits.varpbit(1) == 0 && ctx.objects.select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject obj) {
                return obj.id() == 6 && obj.tile().distanceTo(cannonTile) < 3;
            }
        }).peek().valid();
    }


    @Override
    public void execute() {
        GameObject cannon = ctx.objects.select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject obj) {
                return obj.id() == 6 && obj.tile().distanceTo(cannonTile) < 3;
            }
        }).poll();

        if( cannon.inViewport() ) {
            if( ctx.inventory.select().id(Items.CANNONBALL_2).first().poll().stackSize() > 1 ) {
                cannon.interact(("Fire"), cannon.name());

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.varpbits.varpbit(1) > 0;
                    }
                }, 200, 20);
            } else {
                CommonActions.pickUpCannon(ctx);
            }
        } else {
            ctx.movement.step(cannon);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return cannon.inViewport();
                }
            }, 250, 20);
        }
    }

}
