package ngc.cmb_kurask;

import ngc._resources.constants.Items;
import ngc._resources.actions.*;
import ngc._resources.actions._config.CombatConfig;
import ngc._resources.actions._config.HealConfig;
import ngc._resources.tools.CommonActions;
import ngc._resources.tools.GuiHelper;
import ngc._resources.models.LootItem;
import ngc._resources.models.LootList;
import ngc.slayer_simple.BonesToPeaches;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Kurask AIO", description = "Kills Kurask", properties = "client=4; topic=051515; author=Bowman")
public class _KuraskAIO extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private String status;
    private boolean lootDuringCombat;
    private int minHealthPercent;
    private CombatConfig combatConfig;
    private int[] combatPotionIds;

    // Loot
    private LootList lootList;
    private int[] alchables = new int[0];
    private int[] grimyHerbs;

    // Tasks
    private HealAction healAction;
    private CombatAction combatAction;
    private LootAction lootAction;
    private ToggleLevelUp toggleLevelUp;
    private HighAlch highAlch;
    private UsePotion combatPotion;
    private BonesToPeaches bonesToPeaches;
    private BonesToPeachesLootAction bonesToPeachesLootAction;
    private WaitForCombatLoot waitForLoot;


    @Override
    public void start() {
        // Config
        startConfigs();

        // Heal Config
        HealConfig healConfig = new HealConfig(CommonActions.allFoodIds(), minHealthPercent);
        healAction = new HealAction(ctx, "Healing", healConfig);

        bonesToPeachesLootAction = new BonesToPeachesLootAction(ctx, "b2p loot");
        bonesToPeaches = new BonesToPeaches(ctx, 8, Items.BONES_526);

        // Slayer Config
        kuraskLoot();

        // Loot Task
        lootAction = new LootAction(ctx, "Loot", lootList, -1, lootDuringCombat, true);

        // Combat Config
        combatConfig = new CombatConfig("Kurask", -1, minHealthPercent, lootList, ctx.combat.inMultiCombat(), null);
        combatAction = new CombatAction(ctx, "Combat", combatConfig);

        // Alch Config
        highAlch = new HighAlch(ctx, "Alch", alchables, false, true);

        // Level Up
        toggleLevelUp = new ToggleLevelUp(ctx);

        // Loot
        waitForLoot = new WaitForCombatLoot(ctx);

        // Prompt for kill quota

        // Using melee with defender
        combatPotion = new UsePotion(ctx, "Potion", combatPotionIds, Constants.SKILLS_STRENGTH, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 2, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 4, true);
        status = "Started";

    }

    @Override
    public void poll() {
        switch( checkState() ) {
            case Loot:
                lootAction.execute();
                if(ctx.inventory.select().id(grimyHerbs).count() > 0){
                    ctx.inventory.select().id(Items.HERB_SACK_13226).poll().interact("Fill");
                    sleep();
                }
                break;
            case Combat:
                combatAction.execute();
                break;
            case Healing:
                healAction.execute();
                break;
            case Alch:
                highAlch.execute();
                break;
            case B2P:
                bonesToPeaches.execute();
                break;
            case B2pLoot:
                bonesToPeachesLootAction.execute();
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
                CommonActions.slayerRingTeleport(ctx, 1);
                ctx.controller.suspend();
            default:
                status = "Waiting";
        }
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        String msg = messageEvent.text();

        // Herb sack full of one herb. Then what?
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
            g.drawString("ATK: " + ctx.skills.level(Constants.SKILLS_ATTACK), GuiHelper.getStartX(), GuiHelper.getStartY(5));
            g.drawString("STR: " + ctx.skills.level(Constants.SKILLS_STRENGTH), GuiHelper.getStartX(), GuiHelper.getStartY(6));
            g.drawString("DEF: " + ctx.skills.level(Constants.SKILLS_DEFENSE), GuiHelper.getStartX(), GuiHelper.getStartY(7));


        }
    }


    private void startConfigs() {
        status = "Configuring";
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events
        lootList = new LootList();
        status = "";
        lootDuringCombat = true;
        combatConfig = null;
        minHealthPercent = 50;
        combatPotionIds = new int[] {Items.SUPER_COMBAT_POTION1_12701, Items.SUPER_COMBAT_POTION2_12699, Items.SUPER_COMBAT_POTION3_12697, Items.SUPER_COMBAT_POTION4_12695};


        grimyHerbs = new int[] {Items.GRIMY_RANARR_WEED_207, Items.GRIMY_LANTADYME_2485, Items.GRIMY_AVANTOE_211, Items.GRIMY_KWUARM_213, Items.GRIMY_CADANTINE_215, Items.GRIMY_IRIT_LEAF_209};
    }

    // Loot Configs
    private void kuraskLoot() {

        lootList.addLootItem(new LootItem(Items.MYSTIC_ROBE_TOP_LIGHT_4111));
        lootList.addLootItem(new LootItem(Items.RUNE_LONGSWORD_1303));
        lootList.addLootItem(new LootItem(Items.RUNE_LONGSWORD_6897));
        lootList.addLootItem(new LootItem(Items.RUNE_AXE_1359));
        lootList.addLootItem(new LootItem(Items.LEAF_BLADED_SWORD_11902));
        lootList.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        lootList.addLootItem(new LootItem(Items.RUNE_2H_SWORD_1319));
        lootList.addLootItem(new LootItem(Items.ADAMANT_PLATEBODY_1123));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.LEAF_BLADED_BATTLEAXE_20727));
        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));

        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));
        lootList.addLootItem(new LootItem(Items.AVANTOE_SEED_5298));
        lootList.addLootItem(new LootItem(Items.CADANTINE_SEED_5301));

        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));
        lootList.addLootItem(new LootItem(Items.GRIMY_AVANTOE_211));
        lootList.addLootItem(new LootItem(Items.GRIMY_KWUARM_213));
        lootList.addLootItem(new LootItem(Items.GRIMY_CADANTINE_215));
        lootList.addLootItem(new LootItem(Items.GRIMY_IRIT_LEAF_209));
        lootList.addLootItem(new LootItem(Items.GRIMY_LANTADYME_2485));

        lootList.addLootItem(new LootItem(Items.BIG_BONES_NOTED_533));
        lootList.addLootItem(new LootItem(Items.PAPAYA_FRUIT_NOTED_5973));
        lootList.addLootItem(new LootItem(Items.WHITE_BERRIES_NOTED_240));
        lootList.addLootItem(new LootItem(Items.COCONUT_NOTED_5975));

        // Rare Drop Table
        lootList.addLootItem(new LootItem(Items.RUNITE_BAR_2363));
        lootList.addLootItem(new LootItem(Items.DRAGONSTONE_1615));
        lootList.addLootItem(new LootItem(Items.COINS_995, 1000));
    }

    private enum State {
        Combat, Loot, Alch, Healing, Wait, WaitForLoot, Stop, LevelUp, Potion, B2P, B2pLoot
    }

    private State checkState() {

        if( healAction.activate() ) {
            status = "Heal";
            return State.Healing;
        }
        if( bonesToPeaches.activate() ) {
            status = "B2P";
            return State.B2P;
        }
        if( bonesToPeachesLootAction.activate() ) {
            status = "B2P Loot";
            return State.B2pLoot;
        }
        if(waitForLoot.activate()){
            status = "Drop";

            return State.WaitForLoot;
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

        if( combatPotion.activate() && ctx.inventory.select().id(combatPotionIds).count() > 0 ) {
            status = "Potion";
            return State.Potion;
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


