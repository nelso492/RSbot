package ngc.smithing_cannonball;

import ngc._resources.GameObjects;
import ngc._resources.Items;
import ngc._resources.actions.ToggleLevelUp;
import ngc._resources.actions._config.CraftComponentConfig;
import ngc._resources.functions.CommonAreas;
import ngc._resources.functions.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;

@Script.Manifest(name = "Smith - Cannonball", description = "Smith Cannonballs in edgeville", properties = "client=4; topic=000123; author=Bowman")
public class _CannonballSmither extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private String status = "";

    private int cannonballCount;

    private WalkToFurnace walkFurnace;
    private SmithCannonballs smithCannonballs;
    private WalkToBank walkBank;
    private BankCannonballs bankCannonballs;
    private ToggleLevelUp toggleLevelUp;


    @Override
    public void start() {
        status = "Loading";
        // Ignore Events
        ctx.properties.setProperty("randomevents.disable", "true");

        // Tracking
        int barId = Items.STEEL_BAR_2353;
        int craftedItemId = Items.CANNONBALL_2;

        // Configure smithing prompts
        CraftComponentConfig config = new CraftComponentConfig(270, 14, -1, "Make sets");

        // Walk to Furnace
        walkFurnace = new WalkToFurnace(ctx, barId);

        // Make Jewelry
        smithCannonballs = new SmithCannonballs(ctx, barId, GameObjects.FURNACE_EDGEVILLE, CommonAreas.edgevilleSmelter(), config);

        // Walk to Bank
        walkBank = new WalkToBank(ctx, barId);

        // Bank
        bankCannonballs = new BankCannonballs(ctx, barId, craftedItemId);

        // Levels
        toggleLevelUp = new ToggleLevelUp(ctx);

        // Start
        status = "Start";

    }

    @Override
    public void poll() {
        switch( checkState() ) {
            case BANKING:
                status = bankCannonballs.getStatus();
                bankCannonballs.execute();
                break;
            case SMITHING:
                status = smithCannonballs.getStatus();
                smithCannonballs.execute();
                break;
            case TO_BANK:
                status = walkBank.getStatus();
                walkBank.execute();
                break;
            case TO_FURNACE:
                status = walkFurnace.getStatus();
                walkFurnace.execute();
                break;
            case LVL:
                status =
                        "Level";
                toggleLevelUp.execute();
            case WAIT:
                status = "Wait";
                break;
        }
    }

    private enum State {SMITHING, BANKING, TO_FURNACE, TO_BANK, WAIT, LVL}

    private State checkState() {
        if( walkBank.activate() ) {
            return State.TO_BANK;
        }

        if( walkFurnace.activate() || ctx.players.local().tile().x() > 3110 ) {
            return State.TO_FURNACE;
        }

        if( bankCannonballs.activate() ) {
            return State.BANKING;
        }

        if( smithCannonballs.activate() ) {
            return State.SMITHING;
        }

        if( toggleLevelUp.activate() ) {
            return State.LVL;
        }

        return State.WAIT;
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

        if( msg.contains("remove the cannonballs") ) {
            cannonballCount += 4;
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
        g.drawString("Count  : " + cannonballCount, GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(1));


    }

}


// Banking Task
