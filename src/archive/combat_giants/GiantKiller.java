package scripts.combat_giants;

import shared.constants.Items;
import shared.actions.CombatAction;
import shared.actions.HealAction;
import shared.actions.LootAction;
import shared.actions.ToggleLevelUp;
import shared.action_config.CombatConfig;
import shared.action_config.HealConfig;
import shared.models.BaseAction;
import shared.tools.GuiHelper;
import shared.models.LootItem;
import shared.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name = "CMB - Giants", description = "Giant Killer w/ stackable loot. Food and tele support. No banking.", properties = "client=4; topic=051515; author=Bowman")
public class GiantKiller extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private List<BaseAction> taskList = new ArrayList<>();
    private String status = "";

    // Combat Tracking
    private int combatSkill = 0;
    private int combatLevel = 0;
    private double combatStartXP = 0;
    private double combatXpPerHour = 0;
    private int combatLvlsGained;
    private double killsToLevel = 0;

    // Prayer
    private int prayerLevel = 0;
    private double prayerStartXP = 0;
    private double prayerXpPerHour = 0;
    private int prayerLvlsGained;
    private double bonesToLevel = 0;

    // Loot
    private LootList lootList;

    // Action Flags
    private boolean buryBones;
    private int minHealthPercent;

    @Override
    public void start() {
        // Config
        startConfigs();

        // Build Loot List
        buildLootList();


        // Start XP
        combatSkill = Constants.SKILLS_DEFENSE;
        combatStartXP = ctx.skills.experience(combatSkill);

        // Combat Action
        // String npcName = CommonFunctions.promptForSelection("Select a Target", "Which NPC are we fighting today?", "Hill Giant", "Moss giant");
        CombatConfig combatConfig = new CombatConfig(new int[] {2090, 2091, 2092, 2093}, -1, minHealthPercent, lootList, ctx.combat.inMultiCombat());
        taskList.add(new CombatAction(ctx, "Combat", combatConfig));

        // Loot Config
        taskList.add(new LootAction(ctx, "Loot", lootList, -1));

       /* buryBones = CommonFunctions.promptForBuryBones();
        if( buryBones ) {
            taskList.add(new BuryBones(ctx, "Bury Bones", 532));
            lootList.addLootItem(new LootItem(Items.BIG_BONES_532));

            // Pull XP
            prayerStartXP = ctx.skills.experience(Constants.SKILLS_PRAYER);
        }*/


        // Heal Action
        Item food = ctx.inventory.select().first().poll();
        if( food.valid() ) {
            int[] foodIds = {food.id()};
            HealConfig healConfig = new HealConfig(foodIds, minHealthPercent);
            taskList.add(new HealAction(ctx, "Healing", healConfig));

/*            // Skull Scepter Teleport
            if( ctx.inventory.select().id(Items.SKULL_SCEPTRE_I_21276).count() == 1 ) {
                taskList.add(new SkullScepterTeleport(ctx, "Teleport", food.id(), 30));
            }*/
        }

        // Toggle Run Action
/*
        taskList.add(new ToggleRunAction(ctx, "", new RunConfig(30)));
*/

        // Level Up
        taskList.add(new ToggleLevelUp(ctx));

        status = "Started";

    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {

            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }
        }
    }

    @Override
    public void repaint(Graphics g) {
        calculateXP();

        /*Draw Background*/
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        /*Draw Data*/
        g.drawString("Status : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));

        /*Combat*/
        g.drawString("CMB  : " + (combatLevel) + " [" + combatLvlsGained + "]", GuiHelper.getStartX(), GuiHelper.getStartY(4));
        g.drawString("KTL  : " + (killsToLevel), GuiHelper.getStartX(), GuiHelper.getStartY(5));
        g.drawString("XP/HR: " + (combatXpPerHour), GuiHelper.getStartX(), GuiHelper.getStartY(6));

        if( buryBones ) {
            /*Prayer*/
            g.drawString("Pray : " + (prayerLevel) + " [" + prayerLvlsGained + "]", GuiHelper.getStartX(), GuiHelper.getStartY(8));
            g.drawString("BTL  : " + (bonesToLevel), GuiHelper.getStartX(), GuiHelper.getStartY(9));
            g.drawString("XP/HR: " + (prayerXpPerHour), GuiHelper.getStartX(), GuiHelper.getStartY(10));
        }

    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

        if( msg.contains("advanced your") && !msg.contains("hitpoints") ) {
            if( msg.contains("prayer") ) {
                prayerLvlsGained++;
            } else {
                combatLvlsGained++;
            }
        }
    }

    /*Private Functions*/
    private void calculateXP() {

        // Levels
        combatLevel = ctx.skills.realLevel(combatSkill);
        prayerLevel = ctx.skills.realLevel(Constants.SKILLS_PRAYER);

        // Combat XP
        int currentXP = ctx.skills.experience(combatSkill);
        double xpGained = currentXP - combatStartXP;
        double killXP = 240;

        combatXpPerHour = Math.round((xpGained / ((getRuntime() / 1000) + 1)) * 3600);
        killsToLevel = Math.round(((ctx.skills.experienceAt(ctx.skills.realLevel(combatSkill) + 1) - (ctx.skills.experience(combatSkill))) / killXP) + 1);

        if( buryBones ) {
            // Prayer XP
            currentXP = ctx.skills.experience(Constants.SKILLS_PRAYER);
            xpGained = currentXP - prayerStartXP;
            double bonesXP = 15;

            prayerXpPerHour = Math.round((xpGained / ((getRuntime() / 1000) + 1)) * 3600);
            bonesToLevel = Math.round(((ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_PRAYER) + 1) - (ctx.skills.experience(Constants.SKILLS_PRAYER))) / bonesXP) + 1);
        }
    }

    private void buildLootList() {
        lootList = new LootList();

        // Keys
        lootList.addLootItem(new LootItem(Items.MOSSY_KEY_22374));
        lootList.addLootItem(new LootItem(Items.GIANT_KEY_20754));

        // runes
        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));
        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
        lootList.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        lootList.addLootItem(new LootItem(Items.LAW_RUNE_563));

        // Seeds
        lootList.addLootItem(new LootItem(Items.WILDBLOOD_SEED_5311));
        lootList.addLootItem(new LootItem(Items.RANARR_SEED_5295));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.LANTADYME_SEED_5302));
        lootList.addLootItem(new LootItem(Items.AVANTOE_SEED_5298));
        lootList.addLootItem(new LootItem(Items.TOADFLAX_SEED_5296));
        lootList.addLootItem(new LootItem(Items.CADANTINE_SEED_5301));
        lootList.addLootItem(new LootItem(Items.DWARF_WEED_SEED_5303));

        // Rare Loot Table Items
        lootList.addLootItem(new LootItem(Items.LOOTING_BAG_11941));

        // Food
        lootList.addLootItem(new LootItem(Items.SLICE_OF_CAKE_1895, 1, 8));

        // Ammo
        lootList.addLootItem(new LootItem(Items.STEEL_ARROW_886, 1));

/*        lootList.addLootItem((new LootItem(Items.UNCUT_SAPPHIRE_1623)));
        lootList.addLootItem((new LootItem(Items.UNCUT_RUBY_1619)));
        lootList.addLootItem((new LootItem(Items.UNCUT_EMERALD_1621)));
        lootList.addLootItem((new LootItem(Items.UNCUT_DIAMOND_1617)));*/
    }

    private void startConfigs() {
        status = "Configuring";
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events
        buryBones = false;
        combatLvlsGained = 0;
        prayerLvlsGained = 0;
        minHealthPercent = 50;
    }
}


