package ngc._resources.actions;

import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class EquipGear extends BaseAction<ClientContext> {

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

        this.allIds = new int[] {helmId, bodyId, legId, mainHandId, offHandId};
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(allIds).count() > 0 &&
                ((equipOnHpHigherThanCutoff && ctx.combat.healthPercent() >= healthPercentCutoff) || (!equipOnHpHigherThanCutoff && ctx.combat.healthPercent() <= healthPercentCutoff));
    }

    @Override
    public void execute() {
        for( Item i : ctx.inventory.select().id(allIds)
        ) {
            if( i.id() == mainHandId || i.id() == offHandId )
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
}
