package ngc.magic_super_splasher;

import ngc._resources.Items;
import ngc._resources.functions.CommonFunctions;
import ngc._resources.functions.GaussianTools;
import ngc._resources.functions.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Game;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Magic Splash", description = "Keeps the afk timer from loggin out when splashing", properties = "client=4")
public class SuperSplasher extends PollingScript<ClientContext> implements PaintListener {

    // GUI Tracking
    private String status;
    private int startXP;
    private long totalBreakTime;
    private long lastBreakTimestamp;
    private int nextBreakInMinutes;

    @Override
    public void start() {
        startXP = ctx.skills.experience(Constants.SKILLS_MAGIC);
        totalBreakTime = 0L;
        lastBreakTimestamp = 0L;
        nextBreakInMinutes = Random.nextInt(3, 15);
    }

    @Override
    public void poll() {
        // Fletch
        status = "Splashing";


        // Do Something every X minutes
        if( getRuntime() - lastBreakTimestamp > (1000 * 60 * nextBreakInMinutes) ) {
            switch(Random.nextInt(1,4)){
                case 1:
                    // Check magic level
                    ctx.game.tab(Game.Tab.STATS);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.game.tab() == Game.Tab.STATS;
                        }
                    }, 250, 10);

                    // Hover Magic
                    sleep();
                    ctx.input.move(Random.nextInt(560, 600), Random.nextInt(370, 390));
                    sleep();
                    CommonFunctions.moveMouseOffscreen(ctx, false);
                    break;
                case 2:
                    // Check Combat Level
                    ctx.game.tab(Game.Tab.ATTACK);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.game.tab() == Game.Tab.ATTACK;
                        }
                    }, 250, 10);

                    sleep(GaussianTools.getRandomGaussian(1500, 400));
                    ctx.game.tab(Game.Tab.INVENTORY);
                    break;
                default:
                    ctx.camera.angle(Random.nextInt(0, 99));
            }
            lastBreakTimestamp = getRuntime();
            nextBreakInMinutes = Random.nextInt(3, 15);
            status = "Splashing";
        }

        if( ctx.inventory.select().id(Items.AIR_RUNE_556).count() == 0) {
            ctx.controller.stop();
        }
    }

    @Override
    public void repaint(Graphics g) {

        /*Draw Background*/
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getDialogStartX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getDialogStartX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        /*Draw Data*/
        g.drawString("Status : " + (status), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(2));

        g.setColor(GuiHelper.getTextColorInformation());
        g.drawString("Level: " + ctx.skills.realLevel(Constants.SKILLS_MAGIC), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(4));
        g.drawString("XP Gain: " + (ctx.skills.experience(Constants.SKILLS_MAGIC) - startXP), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(5));

        g.setColor(GuiHelper.getTextColorImportant());
        g.drawString("Last Break: " + GuiHelper.getReadableRuntime(lastBreakTimestamp), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(2));

    }
}
