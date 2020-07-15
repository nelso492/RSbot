/*
package archive.mining_rune_ess;

import ngc._resources.actions.ToggleLevelUp;
import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.GuiHelper;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "MINING - Rune Ess", description = "Ess Miner banking in East Varrock", properties = "client=4; topic=051515; author=Bowman")
public class _RuneEssMiner extends PollingScript<ClientContext> implements PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private GuiHelper helper = new GuiHelper();
    private int startExp = 0;
    private int currentExp = 0;
    private String status = "";


    @Override
    public void start() {
        log.info("Starting Script");

        log.info("Loading Tasks");
        taskList.addAll(Arrays.asList(new ToggleLevelUp(ctx), new WalkToAubury(ctx), new AuburyTeleport(ctx), new MineEssence(ctx), new PortalTeleport(ctx), new WalkToBank(ctx), new BankAction(ctx)));

        startExp = ctx.skills.experience(Constants.SKILLS_MINING);
        log.info("Tasks Loaded");

    }

    @Override
    public void poll() {
        currentExp = ctx.skills.experience(Constants.SKILLS_MINING);
        for( BaseAction t : taskList ) {
            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(helper.getBaseColor());
        g.fillRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("Status : " + (status), helper.getStartX(), helper.getStartY(1));
        g.drawString("Runtime: " + helper.getReadableRuntime(getRuntime()), helper.getStartX(), helper.getStartY(2));
        g.drawString("Ess Count: " + ((currentExp - startExp) / 5), helper.getStartX(), helper.getStartY(3));
    }

}

*/
