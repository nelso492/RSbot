package ngc.thieving_aio;

import ngc._resources.actions.ShiftDropInventory;
import ngc._resources.actions.ToggleLevelUp;
import ngc._resources.tools.CommonActions;
import ngc._resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GameObject;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Thieving AIO", description = "AIO Thiever", properties = "client=4")
public class _TheiverAIO extends PollingScript<ClientContext> implements PaintListener {

    // Task Tracking
    // private InteractWithGameObject stealFruitStall;
    private ShiftDropInventory shiftDropInventory;
    private ToggleLevelUp levelUp;

    // GUI
    private String status;
    private Long nextPause;
    private Long nextPauseTimestamp;
    private int pauseDuration;

    @Override
    public void start() {
        //stealFruitStall = new InteractWithGameObject(ctx, "Steal", "Steal-from", 28823);
        shiftDropInventory = new ShiftDropInventory(ctx);
        levelUp = new ToggleLevelUp(ctx);

        setNextPause();
    }

    @Override
    public void poll() {

        switch( checkState() ) {
            case DROP:
                shiftDropInventory.execute();
                break;
            case STEAL:
                GameObject stall = ctx.objects.select().id(28823).nearest().poll();
                if( stall.valid() && stall.inViewport() ) {
                    sleep(Random.nextInt(100, 1000));
                    stall.interact("Steal-from", stall.name());
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !stall.valid();
                        }
                    }, 250, 10);
                }
                break;
            case LEVEL:
                levelUp.execute();
                break;
            case PAUSE:
                CommonActions.moveMouseOffscreen(ctx, false);
                sleep(pauseDuration);
                setNextPause();
                break;
            default:
                status = "Waiting";
        }

    }

    @Override
    public void repaint(Graphics g) {

        /*Draw Background*/
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        /*Draw Data*/
        g.drawString("Status  : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime : " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));
        g.drawString("Thieving: " + ctx.skills.realLevel(Constants.SKILLS_THIEVING), GuiHelper.getStartX(), GuiHelper.getStartY(3));

    }


    private enum State {
        STEAL, DROP, WAITING, LEVEL, PAUSE
    }

    private State checkState() {
        if( shiftDropInventory.activate() ) {
            status = "Drop";
            return State.DROP;
        }

        if( ctx.objects.select().id(28823).nearest().poll().inViewport() && !ctx.inventory.isFull() ) {
            status = "Steal";
            return State.STEAL;
        }

        if( levelUp.activate() ) {
            status = "Level";
            return State.LEVEL;
        }

        if( getRuntime() >= nextPauseTimestamp ) {
            status = "Pause";
            return State.PAUSE;
        }

        return State.WAITING;
    }

    private void setNextPause() {
        nextPause = Random.nextInt(900, 1800) * 1000L;
        nextPauseTimestamp = nextPause + getRuntime();
        pauseDuration = Random.nextInt(41, 180) * 1000;
    }
}
