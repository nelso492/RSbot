package resources.models;


import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <C> ClientContext from RsBot Runtime
 */
public abstract class BasePhase<C extends ClientContext> extends ClientAccessor<C> {
    public BasePhase(C ctx, String name) {
        super(ctx);
        this.actions = new ArrayList<>();
        this.name = name;
        this.status = "None";
    }

    private String name;
    private String status;

    private List<BaseAction> actions;

    private BasePhase<org.powerbot.script.rt4.ClientContext> nextPhase;

    public void activate() {
        for (BaseAction action : actions) {
            if (action.activate()) {
                action.execute();
                setStatus(action.getStatus());
                break;
            }
        }
    }

    /**
     * Criteria for moving to the next phase in the script
     *
     * @return
     */
    public abstract boolean moveToNextPhase();

    //region G&S

    public String getStatus() {
        return name + ":" + status;
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

    public List<BaseAction> getActions() {
        return actions;
    }

    public void setActions(List<BaseAction> actions) {
        this.actions = actions;
    }

    public void addAction(BaseAction action) {
        this.actions.add(action);
    }

    public BasePhase<org.powerbot.script.rt4.ClientContext> getNextPhase() {
        return nextPhase;
    }

    public void setNextPhase(BasePhase<org.powerbot.script.rt4.ClientContext> nextPhase) {
        this.nextPhase = nextPhase;
    }

    //endregion
}
