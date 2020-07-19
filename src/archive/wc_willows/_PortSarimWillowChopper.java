package scripts.wc_willows;

import shared.models.BaseAction;
import shared.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "WC - Willow Chopper", description = "Willow chopping with banking in Sarim Deposit Box", properties = "client=4; topic=000123; author=Bowman")
public class _PortSarimWillowChopper extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private double logCount = 0;
    private String status = "";
    private int logsToLevel = 0;

    private int lvlsGained;
    private int logsOnCurrentLevel;

    private boolean powerMode = false; // Change to false to enable bank

    @Override
    public void start() {
        status = "Starting";
        lvlsGained = 0;
        logsToLevel = (int) ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_WOODCUTTING) + 1) - (ctx.skills.experience(Constants.SKILLS_WOODCUTTING))) / 67.5) + 1;

        // Add Chopping Action
        taskList.add(new ChopWillowAction(ctx));

        // Add walking and banking
        taskList.addAll(Arrays.asList(new WalkToWillowTreeAction(ctx), new WalkToBankAction(ctx, 0), new BankingAction(ctx)));

        // Add Power Drop for under energy
        // taskList.add(new ShiftDropInventory(ctx, 30, lookup.getId(OBJECT_IDS.WillowTree_1750) ));

        // Ignore Events
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events
        sleep(500);

        status = "Started";
    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {

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
        if( msg.contains("get some willow logs") ) {
            logCount++;
            logsOnCurrentLevel++;
        }

        if( msg.contains("advanced your") ) {
            lvlsGained++;
            logsOnCurrentLevel = 0;
            logsToLevel = (int) ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_WOODCUTTING) + 1) - (ctx.skills.experience(Constants.SKILLS_WOODCUTTING))) / 67.5) + 1;
        }
    }

    @Override
    public void repaint(Graphics g) {
    //    double logsPerHour = Math.round((logCount / ((getRuntime() / 1000) + 1)) * 3600);

        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        g.drawString("Status  : " + status, GuiHelper.getStartX(), GuiHelper.getStartY(1));
      //  g.drawString("Runtime : " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));
        g.drawString("WC Level : " + ctx.skills.level(Constants.SKILLS_WOODCUTTING), GuiHelper.getStartX(), GuiHelper.getStartY(4));
       // g.drawString("Gained   : " + lvlsGained, GuiHelper.getStartX(), GuiHelper.getStartY(5));
        g.drawString("To LVL   : " + logsOnCurrentLevel + "/" + logsToLevel, GuiHelper.getStartX(), GuiHelper.getStartY(6));
       // g.drawString("Chopped  : " + logCount, GuiHelper.getStartX(), GuiHelper.getStartY(7));
       // g.drawString("WPH  : " + logsPerHour, GuiHelper.getStartX(), GuiHelper.getStartY(8));


    }
}


// Banking Task
