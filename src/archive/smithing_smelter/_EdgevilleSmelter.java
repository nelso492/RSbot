package scripts.smithing_smelter;

import shared.templates.AbstractAction;
import shared.tools.GuiHelper;
import shared.actions.ToggleLevelUp;
import scripts.misc_barb_village_looter.ToggleRunAction;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "SMITH - Edgeville Smelter", description = "Smelt Bars, bank in Edgeville", properties = "client=4; topic=000123; author=Bowman")
public class _EdgevilleSmelter extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<AbstractAction> taskList = new ArrayList<>();
    private double barCount = 0;
    private String status = "";
    private double barsToLevel = 0;
    private double barsPerHour = 0;

    @Override
    public void start() {
        log.info("Starting Script");

        log.info("Loading Tasks");
        taskList.addAll(Arrays.asList(new ToggleLevelUp(ctx), new ToggleRunAction(ctx), new BankAction(ctx), new WalkToSmelter(ctx), new SmeltBars(ctx), new WalkToBank(ctx)));
    }

    @Override
    public void poll() {
        for( AbstractAction t : taskList ) {
            if( t.activate() ) {
                if( t.getStatus() != null ) {
                    status = t.getStatus();
                }
                t.execute();
            }
        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();
        if( msg.contains("retrieve a bar of") ) {
            barCount++;
            long runtimeSeconds = (getRuntime() / 1000) + 1;
            barsPerHour = Math.round((barCount / runtimeSeconds) * 3600);
        }
    }

    @Override
    public void repaint(Graphics g) {
        barsToLevel = (int)((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_SMITHING) + 1)-(ctx.skills.experience(Constants.SKILLS_SMITHING))) / 13.5);

        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));

        g.drawString("Status : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));
        g.drawString("Bars   : " + barCount, GuiHelper.getStartX(), GuiHelper.getStartY(3));
        g.drawString("Lvl Up  : " + barsToLevel, GuiHelper.getStartX(), GuiHelper.getStartY(4));
        g.drawString("Bars /hr: " + barsPerHour, GuiHelper.getStartX(), GuiHelper.getStartY(5));
        g.drawString("XP /hr  : " + barsPerHour * 13.5, GuiHelper.getStartX(), GuiHelper.getStartY(6));

    }

}


// Banking Task
