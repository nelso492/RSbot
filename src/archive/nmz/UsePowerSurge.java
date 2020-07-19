package scripts.nmz;


import shared.models.BaseAction;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class UsePowerSurge extends BaseAction<ClientContext> {

    public UsePowerSurge(ClientContext ctx) {
        super(ctx, "Power Surge");
    }

    @Override
    public boolean activate() {
        return true; // Check Every Time
    }

    @Override
    public void execute() {
    }





}
