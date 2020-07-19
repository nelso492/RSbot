package scripts.fishing_net_draynor.phases;

import shared.models.BasePhase;
import shared.tools.AntibanTools;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import shared.tools.CommonAreas;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class FishingPhase extends BasePhase<ClientContext> {
    public FishingPhase(ClientContext ctx) {
        super(ctx, "Fishing");
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.isFull() && CommonAreas.getDraynorBank().contains(ctx.players.local());
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
                    AntibanTools.moveMouseOffScreen(ctx, true);
                    ctx.input.defocus();
                }
            }
        }
    }
}
