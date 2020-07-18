package ngc.fishing_net_draynor.phases;

import ngc._resources.actions._template.BaseAction;
import ngc._resources.actions._template.BasePhase;
import ngc._resources.functions.AntibanActions;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class FishingPhase extends BasePhase<ClientContext> {
    public FishingPhase(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.isFull();
    }

    @Override
    public void activate(){
        for (var action : this.getActions()) {
            if (action.activate()) {
                action.execute();

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().interacting().valid();
                    }
                }, 440, 4);

                if(ctx.players.local().interacting().valid()){
                    AntibanActions.moveMouseOffScreen(ctx, true);
                    ctx.input.defocus();
                }
            }
        }
    }
}
