package shared.templates;

import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;

/**
 * Extends the default base action to provide standard activation and execution
 * in predefined phases.
 *
 * @param <C>
 */
public abstract class AbstractAction<C extends ClientContext> extends ClientAccessor<C> {

    /**
     * @param ctx    Runtime context
     * @param status Status set on execution
     */
    public AbstractAction(C ctx, String status) {
        super(ctx);
        this.status = status;
    }

    private String status;

    /**
     * Override method to define conditional logic.
     * Return true to execute the corresponding action.
     *
     * @return activation criteria as boolean. i.e. ctx.inventory.isFull();
     */
    public abstract boolean activate();

    /**
     * Override method to define execution logic.
     * Should only be executed if corresponding activate is true
     */
    public abstract void execute();


    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String msg) {
        this.status = msg;
    }
}
