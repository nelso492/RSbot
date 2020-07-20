package shared.templates;

import org.powerbot.script.rt4.ClientContext;
import shared.tools.AntibanTools;

import static org.powerbot.script.Condition.sleep;

public abstract class StructuredPhase extends AbstractPhase<ClientContext> {

    private StructuredAction currentAction;
    private StructuredAction initialAction;

    private int checkAttempt;

    public StructuredPhase(ClientContext ctx, String name) {
        super(ctx, name);
    }

    @Override
    public void activate() {

        // If null, time to switch phases on completion
        if (this.currentAction != null) {
            if (this.currentAction.activate()) {
                if (this.getStatus().equals(this.currentAction.getStatus())) {
                    // This process is currently running, check for completion;

                    System.out.println("Checking for completion: " + this.getStatus());
                    if (this.currentAction.isComplete()) {
                        this.currentAction = (StructuredAction) this.currentAction.getNextAction();
                    } else {
                        System.out.println(this.currentAction.getStatus() + " is awaiting completion");
                        sleep(); //Wait before checking again
                    }
                } else {
                    // This is a new Action, execute it
                    this.checkAttempt = 1;
                    System.out.println("Executing: " + this.currentAction.getStatus());
                    this.currentAction.execute();
                    this.setStatus(this.currentAction.getStatus());
                }
            } else {
                if (this.currentAction.isComplete()) {
                    System.out.println("Action Complete: " + this.currentAction.getStatus());
                    this.currentAction = (StructuredAction) this.currentAction.getNextAction();
                } else {
                    // Sleep till retry
                    this.checkAttempt += this.checkAttempt;
                    AntibanTools.sleepDelay(this.checkAttempt);

                }

                if (!ctx.players.local().interacting().valid() && ctx.players.local().animation() == -1 && !ctx.players.local().inMotion() && this.currentAction.activate()) {
                    // Retry execution
                    System.out.println("Retrying: " + this.currentAction.getStatus());
                    this.currentAction.execute();
                }
            }
        } else {
            System.out.println("Awaiting phase change");
        }
    }

    public StructuredAction getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(StructuredAction currentAction) {
        this.currentAction = currentAction;
    }

    public StructuredAction getInitialAction() {
        return initialAction;
    }

    public void setInitialAction(StructuredAction initialAction) {
        this.initialAction = initialAction;
        this.currentAction = initialAction;
    }

    public void resetCurrentAction() {
        this.currentAction = this.initialAction;
        this.setStatus(this.currentAction.getStatus());
    }
}
