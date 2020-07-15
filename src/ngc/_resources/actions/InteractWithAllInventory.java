package ngc._resources.actions;

import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class InteractWithAllInventory extends BaseAction<ClientContext> {
    private int objectId;
    private String action;
    private int sleepMid;
    private int sleepDev;


    public InteractWithAllInventory(ClientContext ctx, String status, String action, int objectId, int sleepMid, int sleepDev) {
        super(ctx, status);
        this.action = action;
        this.objectId = objectId;
        this.sleepDev = sleepDev;
        this.sleepMid = sleepMid;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(objectId).count() > 0;
    }


    @Override
    public void execute() {
        actOnInventory();
    }

    private void actOnInventory() {

        int pattern = Random.nextInt(0, 100);

        if( pattern > 80 ) {
            runPattern(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28});
        }
        if( pattern > 60 ) {
            runPattern(new int[] {1, 5, 9, 13, 17, 21, 25, 2, 6, 10, 14, 18, 22, 26, 3, 7, 11, 15, 19, 23, 27, 4, 8, 12, 16, 20, 24, 28});
        }
        if( pattern > 40 ) {
            runPattern(new int[] {28, 24, 20, 16, 12, 8, 4, 27, 23, 19, 15, 11, 7, 3, 26, 22, 18, 14, 10, 6, 2, 25, 21, 17, 13, 9, 5, 1});
        }
        if( pattern > 20 ) {
            runPattern(new int[] {1, 2, 5, 3, 6, 9, 4, 7, 10, 13, 17, 14, 11, 8, 12, 15, 18, 21, 25, 22, 19, 16, 20, 23, 26, 24, 27, 28});
        }
        if( pattern > 10 ) {
            runPattern(new int[] {28, 27, 26, 25, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1});
        } else {
            // 0 - 10 RANDOM
            int[] i = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28};
            ArrayList list = new ArrayList();
            list.addAll(Arrays.asList(i));

            Collections.shuffle(list);

            for( int index = 0; index > i.length; index++ ) {
                i[index] = (int) list.get(index);
            }
            runPattern(i);
        }


    }

    private void runPattern(int[] indexes) {
        for( int i = 0; i < indexes.length; i++ ) {

            if(!ctx.controller.isStopping() && !ctx.controller.isSuspended()) {
                int itemIndex = indexes[i] - 1;
                if(ctx.inventory.selectedItem().valid()){
                    ctx.inventory.items()[itemIndex].click(); // treat this as a misclick
                }
                if( ctx.inventory.items()[itemIndex].valid() && ctx.inventory.items()[itemIndex].id() == objectId ) {
                    if( ctx.inventory.items()[itemIndex].interact(action) ) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return !ctx.inventory.items()[itemIndex].valid() || ctx.inventory.items()[itemIndex].id() != objectId; // gone or changed
                            }
                        }, 100, 10);
                        sleep((int) Random.nextGaussian() * sleepMid + sleepDev);

                    }
                }
            }
        }
    }

}
