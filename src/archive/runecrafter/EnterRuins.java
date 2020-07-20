package scripts.runecrafter;


import shared.templates.AbstractAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class EnterRuins extends AbstractAction<ClientContext> {

    private int ruinsId;
    private int ruinsYcoord;
    private GameObject altar;

    public EnterRuins(ClientContext ctx, int ruinsId, int ruinsYcoord) {
        super(ctx, "Enter Ruins");
        this.ruinsId = ruinsId;
        this.ruinsYcoord = ruinsYcoord;
    }

    @Override
    public boolean activate() {
        altar = ctx.objects.select().id(ruinsId).poll();
        return ctx.inventory.isFull() && (altar.valid() && altar.inViewport());
    }

    @Override
    public void execute() {
        if( altar.valid() ) {
            altar.interact("Enter");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().tile().y() > ruinsYcoord;
                }
            }, Random.nextInt(250, 350), 30);
        }

    }
}

