package shared.templates;

import org.powerbot.script.rt4.ClientContext;

public abstract class PollingPhase extends AbstractPhase<ClientContext> {
    public PollingPhase(ClientContext ctx, String name) {
        super(ctx, name);
    }

    @Override
    public void activate() {
        for (var action : getActions()) {
            if (action.activate()) {
                action.execute();
                setStatus(action.getStatus());
                break;
            }
        }
    }

}
