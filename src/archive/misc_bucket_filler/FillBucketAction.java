package scripts.misc_bucket_filler;


import resources.models.BaseAction;
import resources.enums.ITEM_IDS;
import resources.enums.OBJECT_IDS;
import resources.tools.CommonAreas;
import resources.tools.RsLookup;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class FillBucketAction extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();
    private CommonAreas areas = new CommonAreas();

    private int fountainId = lookup.getId(OBJECT_IDS.Fountain_5125);
    private int BucketOfWaterId = lookup.getId(ITEM_IDS.BucketOfWater_1929);
    private int bucketId = lookup.getId(ITEM_IDS.EmptyBucket_1925);


    public FillBucketAction(ClientContext ctx) {
        super(ctx, "Filling");
    }

    @Override
    public boolean activate() {
        boolean startedFilling = ctx.inventory.select().id(BucketOfWaterId).count() > 1;
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean playerNotInteracting = !ctx.players.local().interacting().valid();

        return !startedFilling && playerNotAnimated && playerNotInteracting && !ctx.players.local().inMotion();// && netInInventory;
    }

    @Override
    public void execute() {
        // Travel to fishing location
        GameObject fountain = ctx.objects.select().id(fountainId).nearest().poll();

        if( fountain.inViewport() ) {
            ctx.inventory.select().id(bucketId).first().poll().interact("Use");
            sleep();
            if(fountain.click()) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.inventory.select().id(BucketOfWaterId).count() == 28;
                    }
                }, Random.nextInt(1000, 2000), 4);
            }
        }
    }


}
