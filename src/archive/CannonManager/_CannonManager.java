package archive.CannonManager;

import resources.actions.ToggleLevelUp;
import resources.tools.CommonActions;
import resources.tools.GaussianTools;
import resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Cannon Manager AIO", description = "Kills anything with cannon", properties = "client=4; topic=051515; author=Bowman")
public class _CannonManager extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Tasks
    private ToggleLevelUp toggleLevelUp;

    @Override
    public void start() {
        // Config
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events

        // Level Up
        toggleLevelUp = new ToggleLevelUp(ctx);
    }

    @Override
    public void poll() {}

    @Override
    public void messaged(MessageEvent messageEvent) {
        String msg = messageEvent.text();

        if( msg.contains("cannon is out of ammo") ) {
            sleep(GaussianTools.getRandomGaussian(1000, 125));
            CommonActions.reloadCannon(ctx);
        }

        if( msg.contains("cannon") && msg.contains("broken") ) {
            sleep(GaussianTools.getRandomGaussian(2000, 1250));
            CommonActions.fixCannon(ctx);
        }

        if( msg.contains("advanced your Ranged level.") ) {
            toggleLevelUp.activate();
            CommonActions.reloadCannon(ctx);
        }
    }


    @Override
    public void repaint(Graphics g) {
        if( !ctx.controller.isSuspended() ) {
            g.drawString("RNG: " + ctx.skills.level(Constants.SKILLS_RANGE), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));
        }
    }
}


