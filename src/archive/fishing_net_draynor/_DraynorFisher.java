package ngc.fishing_net_draynor;

import ngc._resources.models.BaseAction;
import ngc.fishing_net_draynor.actions.BankingAction;
import ngc.fishing_net_draynor.actions.FishingAction;
import ngc.fishing_net_draynor.actions.WalkToBankAction;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Script.Manifest(name = "FISH - Draynor Net", description = "Net fishing with banking in Draynor", properties = "client=4; topic=051515; author=Bowman")
public class _DraynorFisher extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private int expGained = 0;
    private int shrimpCount = 0;
    private int anchovyCount = 0;


    @Override
    public void start() {
        log.info("Starting Script");

        log.info("Loading Tasks");
        taskList.addAll(Arrays.asList(new FishingAction(ctx), new WalkToBankAction(ctx), new BankingAction(ctx)));

        log.info("Tasks Loaded");

    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {
            if( t.activate() ) {
                t.execute();
            }
        }
    }

    @Override
    public void messaged(MessageEvent e){
        String msg = e.text().toLowerCase();
        if (msg.contains("shrimps")) {
            expGained += 10;
            shrimpCount++;
        }
        if (msg.contains("anchovies")) {
            expGained += 40;
            anchovyCount++;
        }
    }

    @Override
    public void repaint(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 16));
        g.drawString("Exp Gained: " + expGained, 50, 50);
        g.drawString("Shrimp: " + shrimpCount, 50, 75);
        g.drawString("Anchovy: " + anchovyCount, 50, 100);
    }

}


// Banking Task
