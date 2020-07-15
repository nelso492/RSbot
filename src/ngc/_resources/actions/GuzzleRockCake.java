package ngc._resources.actions;


import ngc._resources.Items;
import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.GaussianTools;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class GuzzleRockCake extends BaseAction<ClientContext> {


    public GuzzleRockCake(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(Items.DWARVEN_ROCK_CAKE_7510).count() == 1 &&
                ctx.combat.health() > 1 && ctx.combat.health() < 5;
    }

    @Override
    public void execute() {
        boolean guzzle;

        switch( ctx.combat.health() ) {
            case 2:
                guzzle = GaussianTools.takeActionNormal();
                break;
            case 3:
                guzzle = GaussianTools.takeActionLikely();
                break;
            case 4:
                guzzle = GaussianTools.takeActionAlways();
                break;
            default:
                guzzle = true;
        }

        if( guzzle ) {
            sleep(Random.nextInt(600, 1200));
            while(ctx.combat.health() > 1) {
                ctx.inventory.select().id(Items.DWARVEN_ROCK_CAKE_7510).poll().interact("Guzzle");
                sleep();
            }
        }
    }
}
