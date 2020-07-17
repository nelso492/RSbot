package ngc._resources.actions._template;


import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePhase<C extends ClientContext> extends ClientAccessor<C> {
    public BasePhase(C ctx) {

        super(ctx);

        this.actions = new ArrayList<>();
    }


    private String name;

    private List<BaseAction> actions;

    public void activate() {
        for (BaseAction action : actions) {
            if (action.activate()) {
                action.execute();
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

    public void addAction(BaseAction action){
        this.actions.add(action);
    }
}
