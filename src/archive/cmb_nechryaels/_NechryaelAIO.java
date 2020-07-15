package ngc.cmb_nechryaels;

import ngc._resources.Items;
import ngc._resources.actions.*;
import ngc._resources.actions._config.CombatConfig;
import ngc._resources.actions._config.HealConfig;
import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonFunctions;
import ngc._resources.functions.GuiHelper;
import ngc._resources.models.LootItem;
import ngc._resources.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Equipment;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name = "Nechryael AIO", description = "Kills Nechs off task in slayer tower", properties = "client=4; topic=051515; author=Bowman")
public class _NechryaelAIO extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private List<BaseAction> taskList = new ArrayList<>();
    private String status;
    private boolean lootDuringCombat;
    private int minHealthPercent;
    private CombatConfig combatConfig;
    private int killCount;
    private String killCountMsg;
    private int[] rangePotionIds;
    private int[] combatPotionIds;
    private boolean usingRange;

    // Loot
    private LootList lootList;
    private int[] alchables = new int[0];

    // Tasks
    private HealAction healAction;
    private NechryaelCombatAction combatAction;
    private LootAction lootAction;
    private ToggleLevelUp toggleLevelUp;
    private HighAlch highAlch;
    private EquipGuthans equipGuthans;
    private EquipGear equipGear;
    private UsePotion combatPotion;
    private WaitForCombatLoot waitForLoot;

    @Override
    public void start() {
        // Config
        startConfigs();

        // Heal Config
        HealConfig healConfig = new HealConfig(CommonFunctions.allFoodIds(), minHealthPercent);
        healAction = new HealAction(ctx, "Healing", healConfig);

        // Slayer Config
        nechLootConfig();

        // Loot Task
        lootAction = new LootAction(ctx, "Loot", lootList, -1, lootDuringCombat, true);

        // Combat Config
        combatConfig = new CombatConfig("Nechryael", -1, minHealthPercent, lootList, ctx.combat.inMultiCombat(), null);
        combatAction = new NechryaelCombatAction(ctx, "Combat", combatConfig);
        waitForLoot = new WaitForCombatLoot(ctx);

        // Alch Config
        highAlch = new HighAlch(ctx, "Alch", alchables, false, true);

        // Level Up
        toggleLevelUp = new ToggleLevelUp(ctx);

        // Prompt for kill quota
        equipGuthans = new EquipGuthans(ctx, 70);
        equipGear = new EquipGear(ctx, 90, true, ctx.equipment.itemAt(Equipment.Slot.HEAD).id(), ctx.equipment.itemAt(Equipment.Slot.TORSO).id(), ctx.equipment.itemAt(Equipment.Slot.LEGS).id(), ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id(), ctx.equipment.itemAt(Equipment.Slot.OFF_HAND).id());

        if( ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != Items.TOXIC_BLOWPIPE_12926) {
            // Using melee with defender
            usingRange = false;
            combatPotion = new UsePotion(ctx, "Potion", combatPotionIds, Constants.SKILLS_STRENGTH, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 2, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 4, true);
        } else {
            usingRange = true;
            combatPotion = new UsePotion(ctx, "Potion", rangePotionIds, Constants.SKILLS_RANGE, ctx.skills.realLevel(Constants.SKILLS_RANGE), ctx.skills.realLevel(Constants.SKILLS_RANGE) + 3, true);
        }
        status = "Started";

    }

    @Override
    public void poll() {
        switch( checkState() ) {
            case Loot:
                lootAction.execute();
                break;
            case Combat:
                combatAction.execute();
                break;
            case Healing:
                healAction.execute();
                break;
            case Spec:
                ctx.widgets.component(160, 32).click();
                break;
            case Alch:
                highAlch.execute();
                break;
            case Guthans:
                equipGuthans.execute();
                break;
            case Gear:
                equipGear.execute();
                break;
            case LevelUp:
                toggleLevelUp.execute();
                break;
            case Potion:
                combatPotion.execute();
                break;
            case WaitForLoot:
                waitForLoot.execute();
                break;
            case Stop:
                CommonFunctions.slayerRingTeleport(ctx, 1);
                ctx.controller.suspend();
            default: // waiting
                status = "Waiting";
        }

        killCountMsg = "Kills  : " + killCount;
    }

    @Override
    public void repaint(Graphics g) {
        if( !ctx.controller.isSuspended() ) {


            //  Draw Background
            g.setColor(GuiHelper.getBaseColor());
            g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
            g.setColor(Color.WHITE);
            g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

            //   Draw Data
            g.drawString("Status : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
            g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));
            g.drawString(killCountMsg, GuiHelper.getStartX(), GuiHelper.getStartY(3));
            if( usingRange ) {
                g.drawString("Range: " + ctx.skills.level(Constants.SKILLS_RANGE), GuiHelper.getStartX(), GuiHelper.getStartY(5));
            } else {
                g.drawString("ATK: " + ctx.skills.level(Constants.SKILLS_ATTACK), GuiHelper.getStartX(), GuiHelper.getStartY(5));
                g.drawString("STR: " + ctx.skills.level(Constants.SKILLS_STRENGTH), GuiHelper.getStartX(), GuiHelper.getStartY(6));
                g.drawString("DEF: " + ctx.skills.level(Constants.SKILLS_DEFENSE), GuiHelper.getStartX(), GuiHelper.getStartY(7));

            }

        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text();

        if( msg.contains("Valuable drop:") ) {
            killCount++;
        }
    }

    private void startConfigs() {
        status = "Configuring";
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events

        lootList = new LootList();

        killCount = 0;
        killCountMsg = "Kills: " + killCount;
        status = "";
        lootDuringCombat = true;
        combatConfig = null;
        minHealthPercent = 50;

        rangePotionIds = new int[] {Items.RANGING_POTION1_173, Items.RANGING_POTION2_171, Items.RANGING_POTION3_169, Items.RANGING_POTION4_2444};
        combatPotionIds = new int[] {Items.SUPER_COMBAT_POTION1_12701, Items.SUPER_COMBAT_POTION2_12699, Items.SUPER_COMBAT_POTION3_12697, Items.SUPER_COMBAT_POTION4_12695};
    }

    // Loot Configs
    private void nechLootConfig() {
        lootList.addLootItem(new LootItem(Items.RUNE_2H_SWORD_1319));
        lootList.addLootItem(new LootItem(Items.ADAMANT_KITESHIELD_1199));
        lootList.addLootItem(new LootItem(Items.ADAMANT_PLATELEGS_1073));
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        lootList.addLootItem(new LootItem(Items.RUNE_BOOTS_4131));
        lootList.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        lootList.addLootItem(new LootItem(Items.RUNE_SQ_SHIELD_1185));
        lootList.addLootItem(new LootItem(Items.RUNE_KITESHIELD_1201));
        lootList.addLootItem(new LootItem(Items.DRAGON_MED_HELM_1149));
        lootList.addLootItem(new LootItem(Items.SHIELD_LEFT_HALF_2366));
        lootList.addLootItem(new LootItem(Items.DRAGON_SPEAR_1249));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.LAW_RUNE_563));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        lootList.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));

        lootList.addLootItem(new LootItem(Items.AVANTOE_SEED_5298));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.LANTADYME_SEED_5302));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));

        lootList.addLootItem(new LootItem(Items.COINS_995, 1000));
        lootList.addLootItem(new LootItem(Items.SOFT_CLAY_NOTED_1762));

        // Rare Drop Table
        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));
        lootList.addLootItem(new LootItem(Items.RUNITE_BAR_2363));
        lootList.addLootItem(new LootItem(Items.DRAGONSTONE_1615));

    }

    private enum State {
        Combat, Loot, Gear, Guthans, Alch, Healing, Wait, Spec, Stop, WaitForLoot, LevelUp, Potion
    }

    private State checkState() {

        if( healAction.activate() ) {
            status = "Heal";
            return State.Healing;
        }
        if( equipGear.activate() ) {
            status = "Gear";
            return State.Gear;
        }
        if( equipGuthans.activate() ) {
            status = "Guthans";
            return State.Guthans;
        }
        if( lootAction.activate() ) {
            status = "Loot";
            return State.Loot;
        }
        if( highAlch.activate() ) {
            status = "Alch";
            return State.Alch;
        }

        if( combatAction.activate() ) {
            status = "Combat";
            return State.Combat;
        }

        if( combatPotion.activate() && ctx.inventory.select().id(rangePotionIds).count() > 0 && ctx.inventory.select().id(Items.TOXIC_BLOWPIPE_12926).count() == 0 ) {
            status = "Potion";
            return State.Potion;
        }

        if( combatPotion.activate() && ctx.inventory.select().id(combatPotionIds).count() > 0 ) {
            status = "Potion";
            return State.Potion;
        }

/*        if( ctx.combat.specialPercentage() >= 50 && !ctx.combat.specialAttack() && ctx.inventory.select().id(Items.ABYSSAL_BLUDGEON_13263).count() == 0 ) {
            status = "Spec";
            return State.Spec;
        }*/

        if( waitForLoot.activate() ) {
            status = "Drop";
            return State.WaitForLoot;
        }

        if( toggleLevelUp.activate() ) {
            status = "Level";
            return State.LevelUp;
        }

        if( ctx.inventory.select().id(CommonFunctions.allFoodIds()).count() == 0 && ctx.combat.healthPercent() < minHealthPercent ) {
            status = "Teleport";
            return State.Stop;
        }

        return State.Wait;

    }
}


