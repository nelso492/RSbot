package ngc.fletching_aio;

import ngc._resources.Items;
import ngc._resources.functions.GaussianTools;
import ngc._resources.functions.GuiHelper;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Fletching - Fletch Darts & Bolts", description = "Make darts & bolts at a bank", properties = "client=4")
public class _FletchBoltsAndDarts extends PollingScript<ClientContext> implements PaintListener {

    // GUI Tracking
    private String status;
    private int startXP;
    private long totalBreakTime;
    private long lastBreakTimestamp;
    private int missClickCounter;

    // XP Tracking
    private int primaryComponentId;
    private int[] consumableComponentIds;

    @Override
    public void start() {
        status = "Fletching";
        primaryComponentId = Items.FEATHER_314;
        consumableComponentIds = new int[] {819, 820, 821, 822, 823, 9375, 9376, 9379, 9380, 9381}; //bolt tip
        startXP = ctx.skills.experience(Constants.SKILLS_FLETCHING);
        totalBreakTime = 0L;
        lastBreakTimestamp = 0L;
        missClickCounter = 0;
    }

    @Override
    public void poll() {
        // Fletch
        status = "Fletching";
        if( ctx.inventory.selectedItem().valid() ) {
            if( ctx.inventory.selectedItem().id() == primaryComponentId ) {
                // Click the dart tip twice
                ctx.inventory.select().id(consumableComponentIds).first().poll().click();
                ctx.inventory.select().id(consumableComponentIds).first().poll().click();
            } else {
                // click the feather twice
                ctx.inventory.select().id(primaryComponentId).poll().click();
                ctx.inventory.select().id(primaryComponentId).poll().click();
            }
        } else {
            ctx.inventory.select().id(primaryComponentId).poll().click();
        }

        // break up the automation timing
        sleep(GaussianTools.getRandomGaussian(160, 40));

        // Temporary break 2.2% chance
        if( !GaussianTools.takeActionAlways() ) {
            // Click break
            int breakTime = Random.nextInt(4000, 55000);
            status = "Break: " + breakTime / 1000 + "s";
            totalBreakTime += (long) breakTime;
            lastBreakTimestamp = getRuntime();

            if( GaussianTools.takeActionUnlikely() ) {
                ctx.input.move(ctx.input.getLocation().x + Random.nextInt(-12, 12), ctx.input.getLocation().y + Random.nextInt(-17, 17));
            }
            sleep(breakTime);
            status = "Fletching";
        }

        // 2.2% chance of misclick between items
        if( GaussianTools.takeActionNever() ) {
            status = "Misclick";
            ctx.input.click(new Point(Random.nextInt(550, 590), Random.nextInt(245, 248)), true);
            missClickCounter++;
        }

        if( ctx.inventory.select().id(consumableComponentIds).count() == 0 || ctx.inventory.select().id(primaryComponentId).count() == 0 ) {
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
        g.drawString("Level: " + ctx.skills.realLevel(Constants.SKILLS_FLETCHING), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(4));
        g.drawString("XP Gain: " + (ctx.skills.experience(Constants.SKILLS_FLETCHING) - startXP), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(5));

        g.setColor(GuiHelper.getTextColorImportant());
        g.drawString("Break Time: " + GuiHelper.getReadableRuntime(totalBreakTime), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(1));
        g.drawString("Last Break: " + GuiHelper.getReadableRuntime(lastBreakTimestamp), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(2));
        g.drawString("Miss Clicks: " + missClickCounter, GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(3));

    }
}
