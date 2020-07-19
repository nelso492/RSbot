package scripts.cmb_gargoyle_smasher;

import shared.constants.Items;
import shared.actions.*;
import shared.action_config.CombatConfig;
import shared.action_config.HealConfig;
import shared.models.BaseAction;
import shared.tools.CommonActions;
import shared.tools.GuiHelper;
import shared.models.LootItem;
import shared.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Equipment;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Gargoyle Smasher", description = "Kills gargoyles off task in slayer tower", properties = "client=4; topic=051515; author=Bowman")
public class _GargoyleSmasher extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private List<BaseAction> taskList = new ArrayList<>();
    private String status;
    private boolean lootDuringCombat;
    private int[] foodIds;
    private int minHealthPercent;
    private CombatConfig combatConfig;
    private int killCount;
    private int killQuota;
    private String killCountMsg;

    // Loot
    private LootList lootList;
    private int[] alchables = new int[0];

    // Tasks
    private HealAction healAction;
    private GargoyleCombat gargoyleCombat;
    private LootAction lootAction;
    private WaitForCombatLoot waitForCombatLoot;
    private ToggleLevelUp toggleLevelUp;
    private HighAlch highAlch;
    private EquipGuthans equipGuthans;
    private EquipGear equipGear;
    private Tile[] pathToSafeZone = {new Tile(3443, 3549, 2), new Tile(3440, 3546, 2), new Tile(3437, 3543, 2), new Tile(3433, 3540, 2), new Tile(3429, 3538, 2), new Tile(3426, 3541, 2)};

    @Override
    public void start() {
        // Config
        startConfigs();

        // Heal Config
        HealConfig healConfig = new HealConfig(CommonActions.allFoodIds(), minHealthPercent);
        healAction = new HealAction(ctx, "Healing", healConfig);

        // Slayer Config
        gargoyleLootConfig();

        // Loot Task
        lootAction = new LootAction(ctx, "Loot", lootList, -1, lootDuringCombat, true);

        // Combat Config
        combatConfig = new CombatConfig("Gargoyle", -1, minHealthPercent, lootList, ctx.combat.inMultiCombat(), null);
        gargoyleCombat = new GargoyleCombat(ctx, "Combat", combatConfig, alchables);
        waitForCombatLoot = new WaitForCombatLoot(ctx);

        // Alch Config
        highAlch = new HighAlch(ctx, "Alch", alchables, false, true);

        // Level Up
        toggleLevelUp = new ToggleLevelUp(ctx);


        // Prompt for kill quota
        killQuota = CommonActions.promptForQuantity("Set Kill Quota");

        equipGuthans = new EquipGuthans(ctx, 70);
        equipGear = new EquipGear(ctx, ctx.equipment.itemAt(Equipment.Slot.HEAD).id(), ctx.equipment.itemAt(Equipment.Slot.TORSO).id(), ctx.equipment.itemAt(Equipment.Slot.LEGS).id(), ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id(), ctx.equipment.itemAt(Equipment.Slot.OFF_HAND).id(), ctx.equipment.itemAt(Equipment.Slot.RING).id());

        status = "Started";

    }

    @Override
    public void poll() {
        switch( checkState() ) {
            case Loot:
                lootAction.execute();
                break;
            case Combat:
                gargoyleCombat.execute();
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
            case WaitForLoot:
                waitForCombatLoot.execute();
                break;
            case LevelUp:
                toggleLevelUp.execute();
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
        killCountMsg = killCountMsg.concat((killQuota > 0) ? "/" + killQuota : "");
    }

    @Override
    public void repaint(Graphics g) {
        if( !ctx.controller.isSuspended() ) {


            //  Draw Background
            g.setColor(GuiHelper.getBaseColor());
            g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
            g.setColor(Color.WHITE);
            g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

            //   Draw Data
            g.drawString("Status : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
            g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));
            g.drawString(killCountMsg, GuiHelper.getStartX(), GuiHelper.getStartY(3));


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
        status = "";
        lootDuringCombat = true;
        combatConfig = null;
        minHealthPercent = 50;
    }

    // Loot Configs
    private void gargoyleLootConfig() {

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
        lootList.addLootItem(new LootItem(Items.COINS_995, 9999));

        // Rare Drop Table
        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));
        lootList.addLootItem(new LootItem(Items.RUNITE_BAR_2363));
        lootList.addLootItem(new LootItem(Items.DRAGONSTONE_1615));
        lootList.addLootItem(new LootItem(Items.LOOP_HALF_OF_KEY_987));
        lootList.addLootItem(new LootItem(Items.TOOTH_HALF_OF_KEY_985));

    }

    private enum State {
        Combat, Loot, Gear, Guthans, Alch, Healing, Wait, Spec, Stop, WaitForLoot, LevelUp
    }

    private State checkState() {

        if( ctx.combat.specialPercentage() >= 60 && !ctx.combat.specialAttack() && ctx.combat.healthPercent() < 90 ) {
            return State.Spec;
        }

        if( lootAction.activate() ) {
            status = "Loot";
            return State.Loot;
        }

        if( healAction.activate() ) {
            status = "Heal";
            return State.Healing;
        }

        if( equipGuthans.activate() ) {
            status = "Guthans";
            return State.Guthans;
        }

        if( equipGear.activate() ) {
            status = "Gear";
            return State.Gear;
        }

        if( highAlch.activate() ) {
            status = "Alch";
            return State.Alch;
        }

        if( gargoyleCombat.activate() ) {
            status = "Combat";
            return State.Combat;
        }

        if( waitForCombatLoot.activate() ) {
            return State.WaitForLoot;
        }

        if( toggleLevelUp.activate() ) {
            return State.LevelUp;
        }


        if( ctx.inventory.isFull() || killQuota == killCount ) {
            return State.Stop;
        }

        return State.Wait;

    }
}


