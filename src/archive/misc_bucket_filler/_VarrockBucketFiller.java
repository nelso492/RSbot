package scripts.misc_bucket_filler;

import resources.models.BaseAction;
import resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Script.Manifest(name = "MISC - Varrock Bucket Filler", description = "Water Bucket filling with banking in GE", properties = "client=4; topic=051515; author=Bowman")
public class _VarrockBucketFiller extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private GuiHelper helper = new GuiHelper();
    private int fillCount = 0;
    private int waterBucketPrice = 16;
    private int emptyBucketPrice = 7;
    private String status = "";


    @Override
    public void start() {
        log.info("Starting Script");

        log.info("Loading Tasks");
        taskList.addAll(Arrays.asList(new BankingAction(ctx), new WalkToFountainAction(ctx), new FillBucketAction(ctx), new WalkToBankAction(ctx)));

        log.info("Disabling Event Manager");
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
        if( msg.contains("fill the bucket") ) {
            fillCount += 28;
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 16));
        g.drawString("Status : " + (status), 50, 50);
        g.drawString("Fill Count: " + fillCount, 50, 75);
        g.drawString("Profit: $" + fillCount * (waterBucketPrice - emptyBucketPrice), 50, 125);
        g.drawString("Runtime: " + helper.getReadableRuntime(getRuntime()), 50, 150);

    }

}


// Banking Task
