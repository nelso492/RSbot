package shared.templates;


import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <C> ClientContext from RsBot Runtime
 */
public abstract class AbstractPhase<C extends ClientContext> extends ClientAccessor<C> {
    public AbstractPhase(C ctx, String name) {
        super(ctx);
        this.actions = new ArrayList<>();
        this.name = name;
        this.status = "None";
    }

    private String name;
    private String status;

    private List<AbstractAction<ClientContext>> actions;

    private AbstractPhase<org.powerbot.script.rt4.ClientContext> nextPhase;

    /**
     * Activation to be called on poll to decide which action to perform
     */
    public abstract void activate();

    /**
     * Criteria for moving to the next phase in the script
     *
     * @return
     */
    public abstract boolean moveToNextPhase();

    //region G&S

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AbstractAction<ClientContext>> getActions() {
        return actions;
    }

    public void setActions(List<AbstractAction<ClientContext>> actions) {
        this.actions = actions;
    }

    public void addAction(AbstractAction action) {
        this.actions.add(action);
    }

    public AbstractPhase<org.powerbot.script.rt4.ClientContext> getNextPhase() {
        return nextPhase;
    }

    public void setNextPhase(AbstractPhase<org.powerbot.script.rt4.ClientContext> nextPhase) {
        this.nextPhase = nextPhase;
    }

    //endregion
}
