package scripts.crafting_jewelry_furnace;

import resources.constants.GameObjects;
import resources.constants.Items;
import resources.actions.BankAction;
import resources.actions.ToggleLevelUp;
import resources.action_config.BankConfig;
import resources.action_config.CraftComponentConfig;
import resources.models.BaseAction;
import resources.tools.CommonAreas;
import resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name = "CRAFT - Edgeville Furnace", description = "Smelt Jewelry, bank in Edgeville", properties = "client=4; topic=000123; author=Bowman")
public class _EdgevilleJewelry extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private String status = "";

    // Tracking
    private int lvlsGained;
    private int xpPerCraft;

    private WalkToBank walkBank;
    private WalkToFurnace walkFurnace;
    private MakeJewelry makeJewelry;
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
        int barId = 0;
        int componentId = 0;
        int craftedItemId = 0;

        // Configure jewelry based on mould
        Item i = ctx.inventory.itemAt(27);
        CraftComponentConfig config = new CraftComponentConfig(0, 0, 0, "");

        switch( i.id() ) {
            case 1595:
                // Amulet Mould
                config = new CraftComponentConfig(446, 34, -1, "Make-All");
                xpPerCraft = 30;
                craftedItemId = Items.GOLD_AMULET_U_1673;
                break;
            case 1599:
                // Holy Symbol
                config = new CraftComponentConfig(6, 6, 0, "Craft All");
                xpPerCraft = 50;
                craftedItemId = Items.UNSTRUNG_SYMBOL_1714;
                break;
            case Items.BRACELET_MOULD_11065:
                // Bracelet Mould
                config = new CraftComponentConfig(446, 47, -1, "Make-All");
                xpPerCraft = 25;
                craftedItemId = Items.GOLD_BRACELET_11069;
                break;
        }

        // Bars Config
        Item item = ctx.inventory.select(new Filter<Item>() {
            @Override
            public boolean accept(Item item) {
                return item.name().contains("bar");
            }
        }).first().poll();
        if( item.valid() ) {
            barId = item.id();
        }

        // Walk to Furnace
        walkFurnace = new WalkToFurnace(ctx, barId);

        // Make Jewelry
        makeJewelry = new MakeJewelry(ctx, barId, GameObjects.FURNACE_EDGEVILLE, CommonAreas.edgevilleSmelter(), config);

        // Walk to Bank
        walkBank = new WalkToBank(ctx, barId);

        // Bank
        BankConfig bankConfig = new BankConfig(craftedItemId, -1, barId, 28, -1, -1, false, true, false);
        bankAction = new BankAction(ctx, "Banking", bankConfig);

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
                status = makeJewelry.getStatus();
                makeJewelry.execute();
                break;
            case TO_BANK:
                status = walkBank.getStatus();
                walkBank.execute();
                break;
            case TO_FURNACE:
                status = walkFurnace.getStatus();
                walkFurnace.execute();
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

    }

    private enum State {CRAFTING, BANKING, TO_FURNACE, TO_BANK, WAIT}

    private State checkState() {
        if( walkBank.activate() ) {
            return State.TO_BANK;
        }

        if( walkFurnace.activate() || ctx.players.local().tile().x() > 3110 ) {
            return State.TO_FURNACE;
        }

        if( bankAction.activate() ) {
            return State.BANKING;
        }

        if( makeJewelry.activate() ) {
            return State.CRAFTING;
        }

        return State.WAIT;
    }

}


// Banking Task
