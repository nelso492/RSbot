package ngc.misc_barb_village_looter;

import resources.models.BaseAction;
import resources.tools.GuiHelper;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Script.Manifest(name = "MISC - Barb Village Looter", description = "Loots cooked and non cooked drops at Barbarian Village", properties = "client=4; topic=051515; author=Bowman")
public class _BarbVillageLooter extends PollingScript<ClientContext> implements PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private String status = "Starting";
    private GuiHelper helper = new GuiHelper();
    //  private int pickupCount = 0;


    @Override
    public void start() {
        log.info("Loading Task List");
        taskList.addAll(Arrays.asList(new ToggleRunAction(ctx), new WalkToFishingAction(ctx), new LootGIAction(ctx), new WalkToBankAction(ctx), new BankingAction(ctx)));
    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {
            if( t.activate() ) {
                status = t.getStatus() != null ? t.getStatus() : status;

                /*if(status.equalsIgnoreCase("banking")){
                    pickupCount += 28;
                }*/
                t.execute();
            }
        }
    }


    @Override
    public void repaint(Graphics g) {
        int baseY = 305;

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 284, 520, 50);

        // Headers
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // App Specific Stats
        g.drawString("Status", 0, baseY);
        g.drawString("Time", 150, baseY);
        // g.drawString("Count", 250, baseY);

        // App Data
        baseY += 20;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(status, 0, baseY);
        g.drawString(helper.getReadableRuntime(getRuntime()), 150, baseY);
        // g.drawString(pickupCount + "", 250, baseY);
    }
}