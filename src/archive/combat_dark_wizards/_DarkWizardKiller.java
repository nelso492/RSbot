package scripts.combat_dark_wizards;

import shared.actions.CombatAction;
import shared.actions.HealAction;
import shared.actions.LootAction;
import shared.actions.ToggleRunAction;
import shared.action_config.CombatConfig;
import shared.action_config.HealConfig;
import shared.action_config.RunConfig;
import shared.templates.AbstractAction;
import shared.enums.ITEM_IDS;
import shared.enums.NPC_IDS;
import shared.tools.GuiHelper;
import shared.tools.RsLookup;
import shared.models.LootItem;
import shared.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name = "CMB - Dark Wizard Killer", description = "Kills Dark wizards in S. Varrock & loots most profitable items.", properties = "client=4; topic=051515; author=Bowman")
public class _DarkWizardKiller extends PollingScript<ClientContext> implements PaintListener {

    // Task List
    private List<AbstractAction> taskList = new ArrayList<>();

    // Params
    private String status = "";

    // Loot
    LootList lootList;


    @Override
    public void start() {
        // Setup
        status = "Config - Setup";
        ctx.properties.setProperty("randomevents.disable", "false"); //Ignore random events

        // Loot
        status = "Config - Loot";
        lootList = new LootList();
        lootList.addLootItem(new LootItem(RsLookup.getId(ITEM_IDS.NatureRune_561)));
        lootList.addLootItem(new LootItem(RsLookup.getId(ITEM_IDS.ChaosRune_562)));
        lootList.addLootItem(new LootItem(RsLookup.getId(ITEM_IDS.EarthRune_557)));
        lootList.addLootItem(new LootItem(RsLookup.getId(ITEM_IDS.WaterRune_555)));
        lootList.addLootItem(new LootItem(RsLookup.getId(ITEM_IDS.LawRune_563)));
        lootList.addLootItem(new LootItem(RsLookup.getId(ITEM_IDS.FireRune_554)));
        lootList.addLootItem(new LootItem(RsLookup.getId(ITEM_IDS.BodyRune_559)));
        lootList.addLootItem(new LootItem(RsLookup.getId(ITEM_IDS.AirRune_556)));
        lootList.addLootItem(new LootItem(558)); // Mind Rune
        taskList.add(new LootAction(ctx, "Loot", lootList));

        // Combat
        status = "Config - Combat";

        int healthPercent = 50;
        CombatConfig combatConfig = new CombatConfig("Dark wizard", RsLookup.getId(NPC_IDS.DarkWizardDeathAnimation_836), healthPercent, lootList, ctx.combat.inMultiCombat());
        taskList.add(new CombatAction(ctx, "Combat", combatConfig));

        // Heal
        status = "Config - Heal";
        Item food = ctx.inventory.select().first().poll();
        if( food.valid() ) {
            int[] foodIds = {food.id()};
            HealConfig healConfig = new HealConfig(foodIds, healthPercent);
            taskList.add(new HealAction(ctx, "Healing", healConfig));
        }

        // Toggle Run
        taskList.add(new ToggleRunAction(ctx, "", new RunConfig(30)));

        status = "Started";
    }

    @Override
    public void poll() {
        for( AbstractAction t : taskList ) {
            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }
        }

    }


    @Override
    public void repaint(Graphics g) {
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));

        g.drawString("Status : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));

    }
}


