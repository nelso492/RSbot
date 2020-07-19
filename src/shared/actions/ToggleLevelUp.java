package shared.actions;


import shared.models.BaseAction;
import shared.tools.AntibanTools;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

/**
 * Manage the level up prompt with spacebar input
 */
public class ToggleLevelUp extends BaseAction<ClientContext> {
    public ToggleLevelUp(ClientContext ctx) {
        super(ctx, "Level Up");
    }

    @Override
    public boolean activate() {
        return ctx.widgets.component(233, 3).valid();
    }

    @Override
    public void execute() {
        if( ctx.widgets.component(233, 3).valid() ) {

            if(!ctx.input.isFocused()){
                AntibanTools.sleepDelay(Random.nextInt(1,6));
                ctx.input.focus();
            }
            ctx.input.send(" "); //Spacebar more human-like to dismiss the level up

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.widgets.component(233, 3).valid();
                }
            }, 440, 4);

            // Handles the secondary prompt for newly accessible info.
            // TODO: make this dependent on if that widget is displayed.
            ctx.input.send(" ");
        }
    }
}
