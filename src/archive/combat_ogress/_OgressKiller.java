package scripts.combat_ogress;

import shared.constants.GameObjects;
import shared.constants.Items;
import shared.constants.NpcAnimations;
import shared.constants.Npcs;
import shared.actions.HealAction;
import shared.actions.HighAlch;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "CMB - Ogress", description = "Ogress Killer w/ range & safe spot. high alching. looting.", properties = "client=4; topic=051515; author=Bowman")
public class _OgressKiller extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Tasks & Tracking
    private List<BaseAction> taskList;
    private String status;
    private Tile safeTile;
    private Tile altSafeTile;

    // Loot
    private LootList loot;
    private int[] alchables;
    private boolean bankMode;


    @Override
    public void start() {
        // Startup
        startupConfig();
        loot = new LootList();

        // Bank Mode Prompt
     /*   String bankModeString = CommonFunctions.promptForSelection("Bank or Alch", "", "Bank", "Alch");
        bankMode = (bankModeString.equals("Bank"));*/

        // Set Safespot
/*        String safeSpotTile = CommonFunctions.promptForSelection("Safe Spot Location", "", "Bank Hole", "Cavern");
        if( safeSpotTile.equals("Bank Hole") ) {*/
        safeTile = new Tile(2011, 9002, 1);
        altSafeTile = new Tile(2014, 9003, 1);
/*        } else {
            safeTile = new Tile(1969, 9062, 1);
        }*/


        // Bury Bones Action
/*        String bonesModeString = CommonFunctions.promptForSelection("Bury Bones", "", "Yes", "No");
        if( bonesModeString.equals("Yes") ) {
            taskList.add(new BuryBones(ctx, "Burying", Items.BIG_BONES_532));
            // Bones
            loot.addLootItem(new LootItem(Items.BIG_BONES_532));
        }*/

        // Healing
        HealConfig healConfig = new HealConfig(new int[] {Items.SALMON_329}, 99);
        taskList.add(new HealAction(ctx, "Healing", healConfig));


        // Teleport
/*        if( ctx.inventory.select().id(Items.SKULL_SCEPTRE_I_21276).count() > 0 ) {
            taskList.add(new SkullScepterTeleport(ctx, "Teleporting", Items.LOBSTER_379, 40));
        }*/

        // Re-Equip Arrows
       /* int arrowType = CommonFunctions.promptForArrowType();
        int equipQty = CommonFunctions.promptForQuantity("Arrow count to equip?");
        EquipArrows equipArrows = new EquipArrows(ctx, arrowType, equipQty);*/
        // loot.addLootItem(new LootItem(arrowType, 4));


        // Set Loot
        lootConfig();
        LootAction lootAction = new LootAction(ctx, "Looting", loot, 6);

        // Combat Configuration
        CombatConfig combatConfig = new CombatConfig(new int[] {Npcs.OGRESS_WARRIOR_7990}, NpcAnimations.OGRESSWARRIOR_DEATH, 40, loot, false);
        SafeSpotOgress combatAction = new SafeSpotOgress(ctx, "Combat", combatConfig, 6);

        // Safe Spot Action
        MoveToSafeSpot moveToSafeSpot = new MoveToSafeSpot(ctx, "Moving", safeTile, new int[] {Npcs.OGRESS_WARRIOR_7989, Npcs.OGRESS_WARRIOR_7990}, loot);

        // Walk To Pit
        taskList.add(new WalkBankToHole(ctx));

        // Climb Ladder Down
        taskList.add(new ClimbDownHole(ctx));

        // Climb Ladder Up
        taskList.add(new ClimbUpLadder(ctx, alchables));

        // Walk to Bank
        taskList.add(new WalkHoleToBank(ctx));

        // Deposit Inventory
        taskList.add(new DepostBoxDeposit(ctx, GameObjects.DEPOSIT_BOX_31726));

        // Alch Tasks
        taskList.add(new HighAlch(ctx, "Alching", alchables, true));

        // Level Up
        ToggleLevelUp toggleLevelUp = new ToggleLevelUp(ctx);

        // Default Tasks
        taskList.addAll(Arrays.asList(combatAction, moveToSafeSpot, lootAction, toggleLevelUp));

        // Start
        status = "Start";
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        String msg = messageEvent.text();

        if( msg.contains("no ammo left") ) {
            status = "Out of Ammo";
            ctx.movement.step(safeTile);
            ctx.controller.suspend();
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        g.drawString("Status : " + status, 350, 300);
        g.drawString("Time   : " + GuiHelper.getReadableRuntime(getRuntime()), 350, 325);

    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {

            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }
        }
        if( ctx.game.floor() == 1 ) {
            status = "Waiting";
        }
    }

    // Private Fx
    private void startupConfig() {
        status = "Loading";
        taskList = new ArrayList<>();
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events

    }

    private void lootConfig() {
        // Rune Items
        loot.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        loot.addLootItem(new LootItem(Items.RUNE_MED_HELM_1147));
        loot.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        loot.addLootItem(new LootItem(Items.MITHRIL_KITESHIELD_1197));

        // Runes
        loot.addLootItem(new LootItem(Items.NATURE_RUNE_561));
        loot.addLootItem(new LootItem(Items.LAW_RUNE_563));
        loot.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        loot.addLootItem(new LootItem(Items.COSMIC_RUNE_564));
        loot.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        //loot.addLootItem(new LootItem(Items.AIR_RUNE_556));
        // loot.addLootItem(new LootItem(Items.MIND_RUNE_558));
        //loot.addLootItem(new LootItem(Items.WATER_RUNE_555));
        // loot.addLootItem(new LootItem(Items.EARTH_RUNE_557));

        // Ammo
        // loot.addLootItem(new LootItem(Items.IRON_ARROW_884, 10));
        //loot.addLootItem(new LootItem(Items.STEEL_ARROW_886, 2));
        // loot.addLootItem(new LootItem(Items.MITHRIL_ARROW_888, 2));
         loot.addLootItem(new LootItem(Items.MITHRIL_BOLTS_9142, 2));

        // Gems
        loot.addLootItem(new LootItem(Items.UNCUT_DIAMOND_1617));
        loot.addLootItem(new LootItem(Items.UNCUT_RUBY_1619));
        loot.addLootItem(new LootItem(Items.UNCUT_EMERALD_1621));
        loot.addLootItem(new LootItem(Items.UNCUT_SAPPHIRE_1623));

        // Seeds: Price Date: 1/28
        loot.addLootItem(new LootItem(Items.RANARR_SEED_5295, 1));      // 47k
        loot.addLootItem(new LootItem(Items.KWUARM_SEED_5299, 1));      // 850 gp
        loot.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300, 1));  // 57k
        loot.addLootItem(new LootItem(Items.TOADFLAX_SEED_5296, 1));    // 900 gp
        loot.addLootItem(new LootItem(Items.TORSTOL_SEED_5304, 1));     // 69k
        loot.addLootItem(new LootItem(Items.AVANTOE_SEED_5298, 1));     // 69k
        loot.addLootItem(new LootItem(22879, 1)); // Snape Grass Seed

        // Misc
        loot.addLootItem(new LootItem(Items.COINS_995, 5));
        loot.addLootItem(new LootItem(Items.SHAMAN_MASK_21838));
        loot.addLootItem(new LootItem(Items.LIMPWURT_ROOT_225));
        loot.addLootItem(new LootItem(Items.SALMON_329, 1, 2));

        if( !bankMode ) {
            // Alch Mode
            loot.addLootItem(new LootItem(Items.FIRE_RUNE_554));
            alchables = new int[] {Items.RUNE_FULL_HELM_1163, Items.RUNE_MED_HELM_1147, Items.RUNE_BATTLEAXE_1373, Items.MITHRIL_KITESHIELD_1197};
        }
    }
}
