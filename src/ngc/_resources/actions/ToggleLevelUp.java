package ngc._resources.actions;


import ngc._resources.models.BaseAction;
import ngc._resources.tools.AntibanTools;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

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
           // ctx.widgets.component(233, 3).click();
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

            ctx.input.send(" ");
        }
    }
}
