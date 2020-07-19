package shared.actions;

import shared.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

/**
 * Equip Gear, useful for weapon swaps for spec
 */
public class EquipGear extends BaseAction<ClientContext> {

    private int helmId;
    private int bodyId;
    private int legId;
    private int mainHandId;
    private int offHandId;
    private int healthPercentCutoff;
    private boolean equipOnHpHigherThanCutoff;
    private int[] allIds;

    public EquipGear(ClientContext ctx, int healthPercentCutoff, boolean equipOnHpHigherThanCutoff, int helmId, int bodyId, int legId, int mainHandId, int offHandId) {
        super(ctx, "");

        this.mainHandId = mainHandId;
        this.offHandId = offHandId;
        this.healthPercentCutoff = healthPercentCutoff;
        this.equipOnHpHigherThanCutoff = equipOnHpHigherThanCutoff;

        this.allIds = new int[]{helmId, bodyId, legId, mainHandId, offHandId};
    }

    public EquipGear(ClientContext ctx, String status) {
        super(ctx, status);

        this.healthPercentCutoff = 0;
        this.equipOnHpHigherThanCutoff = true;

        this.allIds = new int[]{helmId, bodyId, legId, mainHandId, offHandId};

    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(allIds).count() > 0 &&
                ((equipOnHpHigherThanCutoff && ctx.combat.healthPercent() >= healthPercentCutoff) || (!equipOnHpHigherThanCutoff && ctx.combat.healthPercent() <= healthPercentCutoff));
    }

    @Override
    public void execute() {
        for (Item i : ctx.inventory.select().id(allIds)
        ) {
            if (i.id() == mainHandId || i.id() == offHandId)
                i.interact("Wield");
            else {
                i.interact("Wear");
            }

            sleep(Random.nextInt(200, 480));

        }
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.select().id(allIds).count() == 0;
            }
        }, 250, 10);
    }

    public int getHelmId() {
        return helmId;
    }

    public void setHelmId(int helmId) {
        this.helmId = helmId;
        this.allIds = new int[]{helmId, bodyId, legId, mainHandId, offHandId};
    }

    public int getBodyId() {
        return bodyId;
    }

    public void setBodyId(int bodyId) {
        this.bodyId = bodyId;
        this.allIds = new int[]{helmId, bodyId, legId, mainHandId, offHandId};
    }

    public int getLegId() {
        return legId;
    }

    public void setLegId(int legId) {
        this.legId = legId;
        this.allIds = new int[]{helmId, bodyId, legId, mainHandId, offHandId};
    }

    public int getMainHandId() {
        return mainHandId;
    }

    public void setMainHandId(int mainHandId) {
        this.mainHandId = mainHandId;
        this.allIds = new int[]{helmId, bodyId, legId, mainHandId, offHandId};
    }

    public int getOffHandId() {
        return offHandId;
    }

    public void setOffHandId(int offHandId) {
        this.offHandId = offHandId;
        this.allIds = new int[]{helmId, bodyId, legId, mainHandId, offHandId};
    }

    public int getHealthPercentCutoff() {
        return healthPercentCutoff;
    }

    public void setHealthPercentCutoff(int healthPercentCutoff) {
        this.healthPercentCutoff = healthPercentCutoff;
    }

    public boolean isEquipOnHpHigherThanCutoff() {
        return equipOnHpHigherThanCutoff;
    }

    public void setEquipOnHpHigherThanCutoff(boolean equipOnHpHigherThanCutoff) {
        this.equipOnHpHigherThanCutoff = equipOnHpHigherThanCutoff;
    }

    public int[] getAllIds() {
        return allIds;
    }
}
