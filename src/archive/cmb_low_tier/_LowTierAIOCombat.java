package ngc.cmb_low_tier;

import ngc._resources.constants.Items;
import ngc._resources.actions.*;
import ngc._resources.actions._config.CombatConfig;
import ngc._resources.actions._config.HealConfig;
import ngc._resources.models.BaseAction;
import ngc._resources.tools.CommonActions;
import ngc._resources.tools.GuiHelper;
import ngc._resources.models.LootItem;
import ngc._resources.models.LootList;
import ngc.slayer_simple.SlayerTaskConfig;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Low Tier Combat", description = "Kills Low Tier Monsters", properties = "client=4; topic=051515; author=Bowman")
public class _LowTierAIOCombat extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private String status = "";
    private Tile safetile;
    private String npcName = "-";
    private SlayerTaskConfig taskConfig;
    private String[] targetNames;

    // Loot
    private LootList lootList;

    // Tasks
    private HealAction healAction;
    private BaseAction combatAction;
    private BaseAction combatActionSecondary;
    private LootAction lootAction;
    private ToggleLevelUp toggleLevelUp;
    private MoveToSafeTile moveToSafeTile;
    private WaitForCombatLoot waitForLoot;
    private BuryBones buryBones;

    // Configurations
    private void init() {
        status = "Configuring";
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events

        targetNames = new String[] {"-", "Cow", "Goblin"};

        lootList = new LootList();
        taskConfig = new SlayerTaskConfig();
    }

    private enum State {
        Combat, CombatSecondary, BuryBones, Loot, Healing, Wait, SafeTile, WaitForLoot, LevelUp
    }

    private State checkState() {

        // Highest Priority (heals, pots, gear)
        if( healAction.activate() ) {
            status = "Heal";
            return State.Healing;
        }

        if( moveToSafeTile != null && moveToSafeTile.activate() ) {
            status = "Safetile";
            return State.SafeTile;
        }

        // Normal Priority (combat, loot)
        if( waitForLoot != null && waitForLoot.activate() ) {
            status = "Drop";
            return State.WaitForLoot;
        }
        if( lootAction.activate() ) {
            status = "Loot";
            return State.Loot;
        }

        if( buryBones != null && buryBones.activate() ) {
            status = "Bones";
            return State.BuryBones;
        }

        if( combatAction.activate() ) {
            status = "Combat";
            return State.Combat;
        }

        // Low Priority (Alch, Levels)
        if( toggleLevelUp.activate() ) {
            status = "Level";
            return State.LevelUp;
        }


        // Default
        return State.Wait;

    }

    @Override
    public void start() {
        // Config
        init();

        // Get Target
        npcName = CommonActions.promptForSelection("Target NPC Name", "Target", targetNames);

        // Slayer Config
        slayerConfig();


        // Heal Config
        int[] foodIds = new int[] {Items.SHRIMPS_315, Items.COOKED_MEAT_2142, Items.TROUT_333, Items.SALMON_329};
        HealConfig healConfig = new HealConfig(foodIds, taskConfig.getEatFoodMinHealthPercentage());
        healAction = new HealAction(ctx, "Healing", healConfig);


        // Combat Config
        CombatConfig _combatConfig;
        if( taskConfig.getCustomCombatConfig() == null ) {
            _combatConfig = new CombatConfig(npcName, -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, ctx.combat.inMultiCombat(), (safetile));
        } else {
            _combatConfig = taskConfig.getCustomCombatConfig();
        }

        // Combat Action
        if( taskConfig.getCustomCombatAction() == null ) {
            combatAction = new CombatAction(ctx, "Combat", _combatConfig);
        } else {
            combatAction = taskConfig.getCustomCombatAction();
        }


        if( taskConfig.getSuperiorCombatConfig() != null ) {
            combatActionSecondary = new CombatAction(ctx, "Combat", taskConfig.getSuperiorCombatConfig());
        }

        // Loot Task
        lootAction = new LootAction(ctx, "Loot", lootList, -1, true, true);

        // Loot Drop
        if( taskConfig.isWaitingForLootDrop() ) {
            waitForLoot = new WaitForCombatLoot(ctx);
        }

        // Level Up
        toggleLevelUp = new ToggleLevelUp(ctx);

        status = "Started";
    }

    @Override
    public void poll() {

        switch( checkState() ) {
            case Healing:
                healAction.execute();
                break;
            case WaitForLoot:
                waitForLoot.execute();
                break;
            case BuryBones:
                buryBones.execute();
                break;
            case Loot:
                lootAction.execute();
                sleep();
                break;

            case Combat:
                combatAction.execute();
                break;
            case CombatSecondary:
                combatActionSecondary.execute();
                break;

            case SafeTile:
                moveToSafeTile.execute();
                break;

            case LevelUp:
                toggleLevelUp.execute();
                break;

            default: // waiting
                status = "Waiting";
        }
    }

    @Override
    public void repaint(Graphics g) {
        if( !ctx.controller.isSuspended() && taskConfig != null ) {
            g.drawString("Status : " + (status), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));
            g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(6));

/*
            //  Draw Background
            g.setColor(GuiHelper.getBaseColor());
            g.fillRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
            g.setColor(GuiHelper.getTextColorWhite());
            g.drawRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));

            //   Draw Data
            g.drawString("Status : " + (status), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(1));
            g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(2));

            g.setColor(GuiHelper.getTextColorImportant());
            g.drawString("ATK: " + ctx.skills.level(Constants.SKILLS_ATTACK), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(2));
            g.drawString("STR: " + ctx.skills.level(Constants.SKILLS_STRENGTH), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(3));
            g.drawString("DEF: " + ctx.skills.level(Constants.SKILLS_DEFENSE), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(4));
            g.drawString("RNG: " + ctx.skills.level(Constants.SKILLS_RANGE), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(5));

            g.setColor(GuiHelper.getTextColorInformation());
            g.drawString("Remaining: " + (remainingKills - killCount), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(4));
            g.drawString("Heal: " + healingMethod, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(5));
*/

        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text();
        if( msg.contains("no ammo left") || msg.contains("are dead") ) {
            ctx.controller.stop();
        }

    }

    // Slayer Configurations
    private void slayerConfig() {
        taskConfig.setEatFoodMinHealthPercentage(30);

        switch( npcName ) {
            case "Cow":
                //cowConfig();
            default:
                gemDropTable();
        }
    }

    private void cowConfig() {
        taskConfig.setSuperiorCombatConfig(new CombatConfig("Cow calf", -1, 60, lootList, ctx.combat.inMultiCombat(), (safetile)));
    }

    // Static Drop Tables
    private void gemDropTable() {
        lootList.addLootItem(new LootItem(Items.UNCUT_DIAMOND_1617));
        lootList.addLootItem(new LootItem(Items.RUNE_JAVELIN_830));
        lootList.addLootItem(new LootItem(Items.LOOP_HALF_OF_KEY_987));
        lootList.addLootItem(new LootItem(Items.TOOTH_HALF_OF_KEY_985));
        lootList.addLootItem(new LootItem(Items.SHIELD_LEFT_HALF_2366));
        lootList.addLootItem(new LootItem(Items.COINS_995, 50));

    }

}


