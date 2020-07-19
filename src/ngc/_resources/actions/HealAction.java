package ngc._resources.actions;

import ngc._resources.actions._config.HealConfig;
import ngc._resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

public class HealAction extends BaseAction<ClientContext> {
    private HealConfig config;

    public HealAction(ClientContext ctx, String status, HealConfig _config) {
        super(ctx, status);
        config = _config;
    }

    @Override
    public boolean activate() {
        boolean minHealth = ctx.combat.healthPercent() <= config.getHeathPercent();
        boolean foodInInventory = ctx.inventory.select().id(config.getFoodIds()).count() > 0;
/*
        System.out.println("Min Health: " + minHealth);
        System.out.println("Food: " + foodInInventory);*/
        return minHealth && foodInInventory;
    }

    @Override
    public void execute() {
        ctx.game.tab(Game.Tab.INVENTORY);

        Item i = ctx.inventory.select().id(config.getFoodIds()).first().poll();

        if(i.name().contains("Jug") || i.name().contains("brew")){
            i.interact("Drink");
        }else
        {
            i.interact("Eat", i.name());
        }
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !i.valid();
            }
        }, 100, 30);
    }
}
