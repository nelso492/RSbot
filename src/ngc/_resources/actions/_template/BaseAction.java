package ngc._resources.actions._template;

import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;

public abstract class BaseAction<C extends ClientContext> extends ClientAccessor<C> {
    public BaseAction(C ctx, String _status) {
        super(ctx);
        setStatus(_status);
    }

    private String status;

    public abstract boolean activate();
    public abstract void execute();

    public String getStatus() {
        return status;
    }

    public void setStatus(String msg) {
        this.status = msg;
    }
}
