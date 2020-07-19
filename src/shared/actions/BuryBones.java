package shared.actions;

import shared.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

/**
 * Bury bones for Prayer leveling. Will toggle quick prayers if enabled for use in Kourend dungeon.
 */
public class BuryBones extends BaseAction<ClientContext> {

    private int boneId;
    private boolean togglePrayers;

    public BuryBones(ClientContext ctx, String status, int boneId, boolean togglePrayers) {
        super(ctx, status);
        this.boneId = boneId;
        this.togglePrayers = togglePrayers;
    }

    public BuryBones(ClientContext ctx, String status) {
        super(ctx, status);
        this.boneId = -1;
        this.togglePrayers = false;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(boneId).count() > 0;
    }

    @Override
    public void execute() {
        if(ctx.prayer.prayersActive() && !ctx.players.local().healthBarVisible() && togglePrayers){
            // shut off prayers
            ctx.prayer.quickPrayer(false);
        }

        ctx.game.tab(Game.Tab.INVENTORY);
        Item bone = ctx.inventory.select().id(boneId).poll();

        if(bone.valid()){
            bone.interact("Bury");

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !bone.valid();
                }
            }, 100, 10);
        }

        if(!ctx.prayer.prayersActive() && togglePrayers){
            // turn on prayers
            ctx.prayer.quickPrayer(true);
        }
    }

    public int getBoneId() {
        return boneId;
    }

    public void setBoneId(int boneId) {
        this.boneId = boneId;
    }

    public boolean isTogglePrayers() {
        return togglePrayers;
    }

    public void setTogglePrayers(boolean togglePrayers) {
        this.togglePrayers = togglePrayers;
    }
}
