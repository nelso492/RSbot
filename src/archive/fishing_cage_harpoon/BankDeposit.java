package scripts.fishing_cage_harpoon;


import shared.constants.Items;
import shared.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

public class BankDeposit extends BaseAction<ClientContext> {

    private int COINS;
    private int HARPOON;
    private int LOBPOT;

    private int DEPOSIT_BOX;
    private boolean closeDialog;
    private int fishingId;

    public BankDeposit(ClientContext ctx, int depositBoxId, boolean closeDialog, int fishingId) {
        super(ctx, "Banking");
        this.COINS = Items.COINS_995;
        this.HARPOON = Items.HARPOON_311;
        this.LOBPOT = Items.LOBSTER_POT_301;
        this.DEPOSIT_BOX = depositBoxId;
        this.closeDialog = closeDialog;
        this.fishingId = fishingId;
    }

    @Override
    public boolean activate() {
        // Full inventory or no net.
        return ctx.inventory.isFull() && ctx.objects.select().id(DEPOSIT_BOX).nearest().peek().inViewport();
    }

    @Override
    public void execute() {

        GameObject dbox = ctx.objects.select().id(DEPOSIT_BOX).poll();

        if( dbox.inViewport() ) {
            dbox.interact("Deposit");


            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.depositBox.opened();
                }
            }, 150, 20);
            ctx.depositBox.depositAllExcept(COINS, HARPOON, LOBPOT);

            if( closeDialog ) {
                ctx.depositBox.close();
            }

            if(fishingId != 0){
                Npc fishingSpot = ctx.npcs.select().id(fishingId).nearest().poll();
                int tileX = Random.nextInt(fishingSpot.tile().x() - 2, fishingSpot.tile().x() + 2);
                int tileY = Random.nextInt(fishingSpot.tile().y() - 2, fishingSpot.tile().y() + 2);
                ctx.movement.step(new Tile(tileX, tileY));
            }
        }else{
            ctx.camera.turnTo(dbox);
        }
    }
}
