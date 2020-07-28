package shared.actions;


import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import shared.constants.Items;
import shared.templates.AbstractAction;
import shared.tools.AntibanTools;
import shared.tools.GaussianTools;

import java.util.concurrent.Callable;

/**
 * Dwarven rock cake guzzle action for NMZ
 */
public class GuzzleRockCake extends AbstractAction<ClientContext> {

    public GuzzleRockCake(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(Items.DWARVEN_ROCK_CAKE_7510).count() == 1 &&
                (ctx.combat.health() > 1 && ctx.combat.health() < 10 );
    }

    @Override
    public void execute() {
        boolean guzzle = GaussianTools.takeActionNormal() || (ctx.combat.health() > 4);

        if( guzzle ) {
            AntibanTools.sleepDelay(AntibanTools.getRandomInRange(3,11));
            while(ctx.combat.health() > 1 && ctx.combat.health() < 50) {
                int h = ctx.combat.health();
                ctx.inventory.select().id(Items.DWARVEN_ROCK_CAKE_7510).poll().interact("Guzzle");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.combat.health() < h;
                    }
                }, 100, 10);
            }

            if(!GaussianTools.takeActionUnlikely() && ctx.combat.health() < 10){
                AntibanTools.moveMouseOffScreen(ctx, (AntibanTools.getRandomInRange(0,1) == 0));
            }
        }
    }
}
