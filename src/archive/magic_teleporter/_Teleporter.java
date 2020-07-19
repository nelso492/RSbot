package ngc.magic_teleporter;

import ngc._resources.constants.Items;
import ngc._resources.tools.CommonActions;
import ngc._resources.tools.GaussianTools;
import ngc._resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Magic;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Mage Tele AIO", description = "Teleports till it runs out of runes", properties = "client=4; topic=051515; author=Bowman")
public class _Teleporter extends PollingScript<ClientContext> implements PaintListener {

    // Tasks
    private Magic.Spell selectedTeleport;

    @Override
    public void start() {
        // Config
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events

        selectedTeleport = Magic.Spell.CAMELOT_TELEPORT;
    }

    @Override
    public void poll() {
        if( ctx.players.local().animation() == -1 ) {
            if( ctx.inventory.select().id(Items.LAW_RUNE_563).count() > 0 ) {
                triggerTeleport();

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() != -1;
                    }
                }, 100, 20);
            } else {
                ctx.controller.stop();
            }
        }

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().animation() == -1;
            }
        }, 254, 20);
    }


    @Override
    public void repaint(Graphics g) {
        g.drawString("Magic: " + ctx.skills.level(Constants.SKILLS_MAGIC), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));

    }

    private void triggerTeleport() {

        // pause between teleports
        sleep(GaussianTools.getRandomGaussian(500, 133));

        // check for break 1-30 seconds
        if( GaussianTools.takeActionRarely() ) {
            switch( Random.nextInt(1, 3) ) {
                case 1:
                    sleep(Random.nextInt(1, 10));
                    break;
                case 2:
                    sleep(Random.nextInt(10, 40));
                    CommonActions.moveMouseOffscreen(ctx, false);
                    break;
                case 3:
                    ctx.game.tab(Game.Tab.STATS);
                    sleep();
                    ctx.input.move(Random.nextInt(560, 600), Random.nextInt(370, 390));
                    break;

            }
        }

        // Teleport
        if( ctx.game.tab() != Game.Tab.MAGIC ) {
            ctx.game.tab(Game.Tab.MAGIC);
        }

        ctx.magic.cast(selectedTeleport);
    }
}


