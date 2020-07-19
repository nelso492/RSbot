package scripts.wc_oak_cutter;

import resources.actions.ToggleLevelUp;
import resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GameObject;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "WC - Varrock Oak Chopper", description = "Oak chopping with banking in GE", properties = "client=4; topic=051515; author=Bowman")
public class _VarrockOakChopper extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private int logsToLevel;
    private BankingAction bankingAction;
    private ChopOakAction chopOakAction;
    private WalkToBankAction walkToBankAction;
    private WalkToOakTreeAction walkToOakTreeAction;
    private ToggleLevelUp levelUp;

    private String status;
    private GameObject closestTree;

    // State
    private enum State {
        ToBank, Banking, ToOaks, Chopping, LevelUp, Waiting
    }

    // Check State


    @Override
    public void start() {
        // Set calculations
        logsToLevel = 1 + (int) ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_WOODCUTTING) + 1) - (ctx.skills.experience(Constants.SKILLS_WOODCUTTING))) / 37.5);

        // Establish Tasks
        bankingAction = new BankingAction(ctx);
        chopOakAction = new ChopOakAction(ctx);
        walkToBankAction = new WalkToBankAction(ctx);
        walkToOakTreeAction = new WalkToOakTreeAction(ctx);
        levelUp = new ToggleLevelUp(ctx);

        // Disable random event interactions
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events


    }

    private State checkState() {
        closestTree = ctx.objects.select().id(10820).nearest().poll();

/*        // Break check
        if( GaussianTools.takeActionRarely() ) {
            int breakTime = Random.nextInt(5, 15);
            status = "Break: " + breakTime + "s";
            if( GaussianTools.takeActionUnlikely() ) {
                CommonFunctions.moveMouseOffscreen(ctx, true);
            }
            sleep(breakTime);
        }*/

        if( levelUp.activate() ) {
            return State.LevelUp;
        }

        // Banking
        if( ctx.bank.inViewport() && ctx.inventory.isFull() ) {
            return State.Banking;
        }

        if( !ctx.bank.inViewport() && ctx.inventory.isFull() ) {
            return State.ToBank;
        }

        if( !closestTree.inViewport() && ctx.inventory.isEmpty() ) {
            return State.ToOaks;
        }

        // Chop
        if( ctx.players.local().animation() != 871 ) {
            return State.Chopping;
        }

        return State.Waiting;
    }

    @Override
    public void poll() {
        switch( checkState() ) {
            case Chopping:
                status = "Chop";
                closestTree.click();

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() == 871;
                    }
                }, 300, 5);
                break;
            case Banking:
                status = "Bank";
                ctx.bank.open();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.bank.opened();
                    }
                }, 250, 10);
                ctx.bank.depositInventory();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.inventory.isEmpty();
                    }
                }, 250, 10);
                break;
            case ToOaks:
                status = "To Oaks";
                walkToOakTreeAction.execute();
                break;
            case ToBank:
                status = "To Bank";
                walkToBankAction.execute();
                break;
            case LevelUp:
                status = "Level";
                levelUp.execute();
                break;
            default:
                status = "Waiting";
        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();
        if( msg.contains("get some oak logs") ) {
            logsToLevel--;
        }

        if( msg.contains("woodcutting level.") ) {
            logsToLevel = 1 + (int) ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_WOODCUTTING) + 1) - (ctx.skills.experience(Constants.SKILLS_WOODCUTTING))) / 37.5);
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(GuiHelper.getTextColorWhite());
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));

        // Draw Data
        g.drawString("Status: " + status, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(4));
        g.drawString("WC Level: " + ctx.skills.level(Constants.SKILLS_WOODCUTTING), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(5));
        g.drawString("Logs to LVL: " + logsToLevel, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));
    }

}


// Banking Task
