package scripts.craft_bank_crafter;

import shared.constants.Items;
import shared.actions.BankAction;
import shared.actions.CombineInventoryItems;
import shared.actions.ToggleLevelUp;
import shared.action_config.BankConfig;
import shared.tools.CommonActions;
import shared.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;

@Script.Manifest(name = "CRAFT - Bank Crafter", description = "Craft at bank", properties = "client=4; topic=000123; author=Bowman")
public class _BankCrafter extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private String status = "";

    // Tracking
    private int lvlsGained;
    private int xpPerCraft;
    private String actionMessage;
    private int craftCount;
    private int craftQuota;

    private CombineInventoryItems combineInventoryItems;
    private BankAction bankAction;
    private ToggleLevelUp toggleLevelUp;

    @Override
    public void start() {
        status = "Loading";

        // Ignore Events
        ctx.properties.setProperty("randomevents.disable", "true");

        // Tracking
        lvlsGained = 0;
        xpPerCraft = 0;
        int toolId = ctx.inventory.itemAt(0).id();
        int componentId = ctx.inventory.itemAt(27).id();
        int craftedItemId = 0;

        switch( componentId ) {
            case Items.UNCUT_JADE_1627:
                xpPerCraft = 20;
                actionMessage = "jade";
                craftedItemId = Items.JADE_1611;
                break;
            case Items.UNCUT_SAPPHIRE_1623:
                xpPerCraft = 50;
                actionMessage = "sapphire";
                craftedItemId = Items.SAPPHIRE_1607;
                break;
            case Items.UNCUT_RUBY_1619:
                xpPerCraft = 85;
                actionMessage = "ruby";
                craftedItemId = Items.RUBY_1603;
                break;
            case Items.UNCUT_DIAMOND_1617:
                xpPerCraft = 107;
                actionMessage = "diamond";
                craftedItemId = Items.DIAMOND_1601;
                break;
        }

        combineInventoryItems = new CombineInventoryItems(ctx, toolId, componentId, true, 35);
        toggleLevelUp = new ToggleLevelUp(ctx);

        // Bank
        BankConfig bankConfig = new BankConfig(craftedItemId, Items.CRUSHED_GEM_1633, componentId, 28, -1, -1, false, true, true);
        bankAction = new BankAction(ctx, "Banking", bankConfig);

        craftQuota = CommonActions.promptForNumber("Craft amount? ");
        craftCount = 0;

        // Start
        status = "Start";

    }

    @Override
    public void poll() {
        switch( checkState() ) {
            case BANKING:
                status = bankAction.getStatus();
                bankAction.execute();
                break;
            case CRAFTING:
                status = combineInventoryItems.getStatus();
                combineInventoryItems.execute();
                break;
            case LEVELUP:
                status = toggleLevelUp.getStatus();
                toggleLevelUp.execute();
                break;
            case WAIT:
                status = "Wait";
                break;
        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

        if( msg.contains("advanced your") ) {
            lvlsGained++;
        }

        if(msg.contains(actionMessage)){
            craftCount++;

            if(craftCount >= craftQuota){
                ctx.controller.stop();
            }
        }

    }

    @Override
    public void repaint(Graphics g) {
        int craftsToLevel = (int) ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_CRAFTING) + 1) - (ctx.skills.experience(Constants.SKILLS_CRAFTING))) / xpPerCraft) + 1;

        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Status : " + status, GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));
        g.drawString("Crafting Lvl : " + ctx.skills.level(Constants.SKILLS_CRAFTING), GuiHelper.getStartX(), GuiHelper.getStartY(4));
        g.drawString("Levels gained: " + lvlsGained, GuiHelper.getStartX(), GuiHelper.getStartY(5));
        g.drawString("Crafts To Lvl: " + craftsToLevel, GuiHelper.getStartX(), GuiHelper.getStartY(7));
        g.drawString("Crafts: " + craftCount + "/" + craftQuota, GuiHelper.getStartX(), GuiHelper.getStartY(8));

    }

    private enum State {CRAFTING, BANKING, LEVELUP, WAIT}

    private State checkState() {
        if( bankAction.activate() ) {
            return State.BANKING;
        }

        if( combineInventoryItems.activate() ) {
            return State.CRAFTING;
        }

        if(toggleLevelUp.activate()){
            return State.LEVELUP;
        }

        return State.WAIT;
    }

}


// Banking Task
