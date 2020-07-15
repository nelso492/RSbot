package ngc._resources.actions;

import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonFunctions;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class EquipGuthans extends BaseAction<ClientContext> {

    private int minHealth;

    public EquipGuthans(ClientContext ctx, int minHealth) {
        super(ctx, "Equip Guthans Set");
        this.minHealth = minHealth;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(CommonFunctions.guthansEquipment()).count() > 0 && ctx.combat.healthPercent() < minHealth;
    }

    @Override
    public void execute() {
        for( Item i : ctx.inventory.select(new Filter<Item>() {
            @Override
            public boolean accept(Item item) {
                return item.name().contains("Guthan");
            }
        })
        ) {
            if( i.name().contains("spear") )
                i.interact("Wield");
            else
                i.interact("Wear");

            sleep(Random.nextInt(350, 650));
        }
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.select(new Filter<Item>() {
                    @Override
                    public boolean accept(Item item) {
                        return item.name().contains("Guthan");
                    }
                }).count() == 0;
            }
        }, 250, 10);
    }
}