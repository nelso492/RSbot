package ngc.wc_yews;

import ngc._resources.constants.Items;
import ngc._resources.actions.BankAction;
import ngc._resources.actions._config.BankConfig;
import ngc._resources.models.BaseAction;
import ngc._resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GeItem;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "WC - Corsiar Yews", description = "Yew chopping with banking in Corsiar Cove bank", properties = "client=4; topic=000123; author=Bowman")
public class _CorsairYewChopper extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private int logCount = 0;
    private String status = "";
    private double profit = 0;
    private double profitPerHour = 0;
    private long lastCameraMove = 0L;
    private int logPrice = 0;

    // Paint
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

    @Override
    public void start() {
        status = "Starting";

        // Bank Config
        BankConfig bankConfig = new BankConfig(0, 0, 0, 0, 0, 0, true, false, false);

        // Task List
        taskList.addAll(Arrays.asList(new BankAction(ctx, "Banking", bankConfig), new WalkBankToCove(ctx), new ChopYewAction(ctx), new WalkCoveToBank(ctx)));

        // Ignore Random Events
        ctx.properties.setProperty("randomevents.disable", "false");

        // Pull Log Price
        GeItem yew = new GeItem(Items.YEW_LOGS_1515);
        if( yew.price > 0 ) {
            logPrice = (int) (yew.price * 0.9);
        }

        // Start
        status = "Start";
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

        // Stop timeout
        if( getRuntime() - lastCameraMove > (Random.nextInt(90, 200) * 1000) ) {
            int pitchOffset = ctx.camera.pitch() <= 55 ? (55 - ctx.camera.pitch() + Random.nextInt(0, 12)) : ctx.camera.pitch() == 99 ? Random.nextInt(-12, 0) : Random.nextInt(-12, 12);
            ctx.camera.pitch(ctx.camera.pitch() + pitchOffset);
            lastCameraMove = getRuntime();
        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();
        if( msg.contains("get some yew logs") ) {
            logCount++;
            profit = logCount * logPrice;

        }
    }

    @Override
    public void repaint(Graphics g) {
        // Per Hour
        profitPerHour = Math.round(((logCount * logPrice) / ((getRuntime() / 1000) + 1)) * 3600);

        // Paint
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        g.drawString("Status : " + status, GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));
        g.drawString("WC Level: " + ctx.skills.level(Constants.SKILLS_WOODCUTTING), GuiHelper.getStartX(), GuiHelper.getStartY(3));
        g.drawString("Chopped: " + logCount, GuiHelper.getStartX(), GuiHelper.getStartY(4));
        g.drawString("Prft: " + numberFormat.format(profit), GuiHelper.getStartX(), GuiHelper.getStartY(6));
        g.drawString("$/hr: " + numberFormat.format(profitPerHour), GuiHelper.getStartX(), GuiHelper.getStartY(7));
    }

}


// Banking Task
