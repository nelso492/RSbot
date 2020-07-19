package scripts.cmb_gargoyles_2;

import resources.constants.Items;
import resources.actions.*;
import resources.action_config.CombatConfig;
import resources.action_config.HealConfig;
import resources.tools.CommonActions;
import resources.tools.GuiHelper;
import resources.models.LootItem;
import resources.models.LootList;
import scripts.slayer_simple.GargoyleCombat;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Equipment;

import java.awt.*;
import java.util.ArrayList;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Gargoyle AIO", description = "Kills Gargs off task in slayer tower", properties = "client=4; topic=051515; author=Bowman")
public class _GargoylesAIO extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private String status;
    private boolean lootDuringCombat;
    private int minHealthPercent;
    private CombatConfig combatConfig;
    private int killCount;
    private String killCountMsg;
    private int[] combatPotionIds;
    private Tile[] pathToSafeZone = {new Tile(3443, 3549, 2), new Tile(3440, 3546, 2), new Tile(3437, 3543, 2), new Tile(3433, 3540, 2), new Tile(3429, 3538, 2), new Tile(3426, 3541, 2)};
    private ArrayList<Integer> specWeaponIds;

    // Loot
    private LootList lootList;
    private int[] alchables = new int[0];

    // Tasks
    private HealAction healAction;
    private GargoyleCombat combatAction;
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
        HealConfig healConfig = new HealConfig(CommonActions.allFoodIds(), minHealthPercent);
        healAction = new HealAction(ctx, "Healing", healConfig);

        // Slayer Config
        gargLootConfig();

        // Loot Task
        lootAction = new LootAction(ctx, "Loot", lootList, -1, lootDuringCombat, true);

        // Combat Config
        combatConfig = new CombatConfig("Gargoyle", -1, minHealthPercent, lootList, ctx.combat.inMultiCombat(), null);
        combatAction = new GargoyleCombat(ctx, "Combat", combatConfig);
        waitForLoot = new WaitForCombatLoot(ctx);

        // Alch Config
        highAlch = new HighAlch(ctx, "Alch", alchables, false, true);

        // Level Up
        toggleLevelUp = new ToggleLevelUp(ctx);

        // Prompt for kill quota
        equipGuthans = new EquipGuthans(ctx, 70);
        equipGear = new EquipGear(ctx, 90, true, ctx.equipment.itemAt(Equipment.Slot.HEAD).id(), ctx.equipment.itemAt(Equipment.Slot.TORSO).id(), ctx.equipment.itemAt(Equipment.Slot.LEGS).id(), ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id(), ctx.equipment.itemAt(Equipment.Slot.OFF_HAND).id());

        // Using melee with defender
        combatPotion = new UsePotion(ctx, "Potion", combatPotionIds, Constants.SKILLS_STRENGTH, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 2, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 4, true);

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
                if( !ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).name().contains("Guthan") )
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
                ctx.movement.newTilePath(pathToSafeZone).traverse();
                sleep();

                if( ctx.players.local().tile().x() < 3430 ) {
                    ctx.game.logout();
                    ctx.controller.stop();
                }
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
            g.drawString("ATK: " + ctx.skills.level(Constants.SKILLS_ATTACK), GuiHelper.getStartX(), GuiHelper.getStartY(5));
            g.drawString("STR: " + ctx.skills.level(Constants.SKILLS_STRENGTH), GuiHelper.getStartX(), GuiHelper.getStartY(6));
            g.drawString("DEF: " + ctx.skills.level(Constants.SKILLS_DEFENSE), GuiHelper.getStartX(), GuiHelper.getStartY(7));


        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text();

        if( msg.equals("The gargoyle cracks apart.") ) {
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

        specWeaponIds = new ArrayList<>();
        specWeaponIds.add(Items.ABYSSAL_BLUDGEON_13263);
        specWeaponIds.add(Items.GRANITE_HAMMER_21742);

        combatPotionIds = new int[] {Items.SUPER_COMBAT_POTION1_12701, Items.SUPER_COMBAT_POTION2_12699, Items.SUPER_COMBAT_POTION3_12697, Items.SUPER_COMBAT_POTION4_12695};
    }

    // Loot Configs
    private void gargLootConfig() {

        // Unique Drop Table Alchables
        lootList.addLootItem(new LootItem(Items.ADAMANT_PLATELEGS_1073));
        lootList.addLootItem(new LootItem(Items.RUNE_PLATELEGS_1079));
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        lootList.addLootItem(new LootItem(Items.RUNE_2H_SWORD_1319));
        lootList.addLootItem(new LootItem(Items.ADAMANT_BOOTS_4129));
        lootList.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        lootList.addLootItem(new LootItem(Items.GRANITE_MAUL_4153));
        lootList.addLootItem(new LootItem(Items.MYSTIC_ROBE_TOP_DARK_4101));

        // Rare Drop Table Alchables
        lootList.addLootItem(new LootItem(Items.RUNE_KITESHIELD_1201));
        lootList.addLootItem(new LootItem(Items.DRAGON_MED_HELM_1149));
        lootList.addLootItem(new LootItem(Items.SHIELD_LEFT_HALF_2366));
        lootList.addLootItem(new LootItem(Items.DRAGON_SPEAR_1249));
        lootList.addLootItem(new LootItem(Items.RUNE_SPEAR_1247));
        lootList.addLootItem(new LootItem(Items.RUNE_SQ_SHIELD_1185));

        // Commit Alchables
        alchables = lootList.allItemIds();

        // Unique Drop Table
        lootList.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        lootList.addLootItem(new LootItem(Items.STEEL_BAR_NOTED_2354));
        lootList.addLootItem(new LootItem(Items.MITHRIL_BAR_NOTED_2360));
        lootList.addLootItem(new LootItem(Items.GOLD_BAR_NOTED_2358));
        lootList.addLootItem(new LootItem(Items.RUNITE_ORE_451));
        lootList.addLootItem(new LootItem(Items.GOLD_ORE_NOTED_445));
        lootList.addLootItem(new LootItem(Items.COINS_995, 900));

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

        if( waitForLoot.activate() ) {
            status = "Drop";
            return State.WaitForLoot;
        }
        if( combatAction.activate() ) {
            status = "Combat";
            return State.Combat;
        }

        if( combatPotion.activate() && ctx.inventory.select().id(combatPotionIds).count() > 0 ) {
            status = "Potion";
            return State.Potion;
        }

        if( ctx.combat.specialPercentage() >= 60 && !ctx.combat.specialAttack() && specWeaponIds.contains(ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id()) ) {
            status = "Spec";
            return State.Spec;
        }


        if( toggleLevelUp.activate() ) {
            status = "Level";
            return State.LevelUp;
        }

        if( ctx.inventory.select().id(CommonActions.allFoodIds()).count() == 0 && ctx.combat.healthPercent() < minHealthPercent ) {
            status = "Teleport";
            return State.Stop;
        }

        return State.Wait;

    }
}


