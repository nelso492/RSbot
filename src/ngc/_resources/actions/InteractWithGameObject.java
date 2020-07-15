package ngc._resources.actions;

import ngc._resources.Items;
import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class InteractWithGameObject extends BaseAction<ClientContext> {
    private int objectId;
    private String action;


    public InteractWithGameObject(ClientContext ctx, String status, String action, int objectId) {
        super(ctx, status);
        this.action = action;
        this.objectId = objectId;
    }

    @Override
    public boolean activate() {
        return ctx.objects.select().id(objectId).nearest().poll().valid();
    }


    @Override
    public void execute() {
        sleep(Random.nextInt(400, 1200));
        GameObject obj = ctx.objects.select().id(objectId).nearest().poll();

        if(obj.inViewport()) {
            obj.interact(action);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !obj.valid();
                }
            }, 250, 10);
        }else{

            if(obj.tile().distanceTo(ctx.players.local()) > 10){
                ctx.movement.step(new Tile(obj.tile().x() + Random.nextInt(-3, 3), obj.tile().y() + Random.nextInt(-3, 3)));
            }else {
                ctx.camera.turnTo(obj);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return obj.inViewport();
                    }
                }, 250, 20);
            }
        }
    }
}
