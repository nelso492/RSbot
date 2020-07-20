package shared.templates;

import org.powerbot.script.rt4.ClientContext;

public abstract class StructuredAction extends AbstractAction<ClientContext> {

    /**
     * @param ctx    Runtime context
     * @param status Status set on execution
     */
    public StructuredAction(ClientContext ctx, String status) {
        super(ctx, status);

        this.nextAction = null;
    }

    public abstract boolean isComplete();

    private AbstractAction<ClientContext> nextAction;

    public AbstractAction<ClientContext> getNextAction() {
        return nextAction;
    }

    public void setNextAction(AbstractAction<ClientContext> nextAction) {
        this.nextAction = nextAction;
    }
}
