package scripts.slayer_simple;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Equipment;
<<<<<<< HEAD:src/scripts/slayer_simple/_SimpleSlayerPlus.java
import org.powerbot.script.rt4.Game;
=======
>>>>>>> 9aa08e59baaf883509287c175f84823629cb01dd:src/non_active/slayer_simple/_SimpleSlayerPlus.java
import shared.actions.*;
import shared.constants.Items;
import shared.models.LootItem;
import shared.models.LootList;
import shared.templates.AbstractAction;
import shared.tools.AntibanTools;
import shared.tools.CommonActions;
import shared.tools.GuiHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Simple Slayer", description = "Kills Slayer Tasks", properties = "client=4; topic=051515; author=Bowman")
public class _SimpleSlayerPlus extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private String status = "";
    private int remainingKills = 0;
    private int startXP = 0;
    private Tile safetile;
    private String npcName = "-";
    private String healingMethod;
    private SlayerTaskConfig taskConfig;
    private String[] targetNames;
    private int killCount = 0;
    private int potionDosesUsed = 0;
    private int[] combatPotionIds;
    private int[] prayerPotionIds;
    private ArrayList<Integer> specWeaponIds;

    // Antiban
    long nextAntibanActionTime;

    // Loot
    private LootList lootList;
    private int[] alchables = new int[0];
    private int[] grimyHerbs;

    // Tasks
    private HealAction healAction;
    private AbstractAction combatAction;
    private AbstractAction superiorCombatAction;
    private BonesToPeaches bonesToPeaches;
    private LootAction lootAction;
    private ToggleLevelUp toggleLevelUp;
    private HighAlch highAlch;
    private EquipGuthans equipGuthans;
    private EquipGear equipGear;
    private UsePotion combatPotion;
    private UsePotion prayerPotion;
    private MoveAwayFromTarget moveAwayFromTarget;
    private MoveToSafeTile moveToSafeTile;
    private Teletab teletab;
    private WaitForCombatLoot waitForLoot;
    private BonesToPeachesLootAction bonesToPeachesLootAction;
    private BuryBones buryBones;

    // Configurations
    private void init() {
        status = "Configuring";
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events
        startXP = ctx.skills.experience(Constants.SKILLS_SLAYER);

        targetNames = new String[]{"-", "Aberrant spectre", "Ankou", "Black demon", "Blue dragon", "Bloodveld", "Dagannoth", "Deviant spectre", "Fire giant", "Gargoyle", "Greater demon", "Greater Nechryael", "Hellhound", "Jelly", "Kalphite Worker", "Kurask", "Mountain troll", "Mutated Bloodveld", "Nechryael", "Turoth", "Warped Jelly", "Wyrm"};

        lootList = new LootList();
        taskConfig = new SlayerTaskConfig();

        taskConfig.setEatFoodMinHealthPercentage(30);
        potionDosesUsed = 0;

        specWeaponIds = new ArrayList<>();
        specWeaponIds.add(Items.ABYSSAL_WHIP_4151);
        specWeaponIds.add(Items.GRANITE_HAMMER_21742);
        specWeaponIds.add(Items.DRAGON_CROSSBOW_21902);

        this.nextAntibanActionTime = getRuntime() + (3600 * AntibanTools.getRandomInRange(0, 5));
    }

    private enum State {
        Combat, BuryBones, Loot, Gear, DodgeProjectile, Guthans, Alch, Healing, Wait, Spec, B2PLoot, Teletab, SafeTile, WaitForLoot, LevelUp, CombatPotion, PrayerPotion, MoveAwayFromTarget, SuperiorCombat, BonesToPeaches
    }

    private State checkState() {

<<<<<<< HEAD:src/scripts/slayer_simple/_SimpleSlayerPlus.java
        // Highest Priority (heals, pots, gear)
        if (healAction.activate()) {
            status = "Heal";
            return State.Healing;
        }
        if (this.buryBones != null && this.buryBones.activate()) {
            status = "Bury";
            return State.BuryBones;
        }
        if (prayerPotion != null && prayerPotion.activate() && ctx.inventory.select().id(prayerPotionIds).count() > 0) {
            status = "Prayer";
            return State.PrayerPotion;
        }
        if (bonesToPeaches != null && bonesToPeaches.activate() && ctx.inventory.select().id(Items.BONES_TO_PEACHES_8015).count() > 0) {
            status = "B2P";
            return State.BonesToPeaches;
        }
        if (equipGuthans != null && equipGuthans.activate()) {
            status = "Guthans";
            return State.Guthans;
        }
        if (equipGear != null && equipGear.activate()) {
            status = "Gear";
            return State.Gear;
        }
        if (combatPotion.activate() && ctx.inventory.select().id(combatPotionIds).count() > 0) {
            status = "Combat Potion";
            return State.CombatPotion;
        }
=======
        if (ctx.players.local().interacting().valid()) {

            // Checks while in combat
            if (waitForLoot != null && waitForLoot.activate()) {
                status = "Drop";
                return State.WaitForLoot;
            }
            if (healAction.activate()) {
                status = "Heal";
                return State.Healing;
            }
            if (prayerPotion != null && prayerPotion.activate() && ctx.inventory.select().id(prayerPotionIds).count() > 0) {
                status = "Prayer";
                return State.PrayerPotion;
            }
            if (equipGuthans != null && equipGuthans.activate()) {
                status = "Guthans";
                return State.Guthans;
            }
            if (equipGear != null && equipGear.activate()) {
                status = "Gear";
                return State.Gear;
            }
            if (moveAwayFromTarget != null && moveAwayFromTarget.activate()) {
                status = "Moving";
                return State.MoveAwayFromTarget;
            }
            if (moveToSafeTile != null && moveToSafeTile.activate()) {
                status = "Safetile";
                return State.SafeTile;
            }
            if (ctx.combat.specialPercentage() >= 60 && !ctx.combat.specialAttack() && specWeaponIds.contains(ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id())) {
                status = "Spec";
                return State.Spec;
            }
>>>>>>> 9aa08e59baaf883509287c175f84823629cb01dd:src/non_active/slayer_simple/_SimpleSlayerPlus.java

        } else {

<<<<<<< HEAD:src/scripts/slayer_simple/_SimpleSlayerPlus.java
        // High Priority (task Specific)
        if (moveAwayFromTarget != null && moveAwayFromTarget.activate()) {
            status = "Moving";
            return State.MoveAwayFromTarget;
        }
        if (moveToSafeTile != null && moveToSafeTile.activate()) {
            status = "Safetile";
            return State.SafeTile;
        }


        // Normal Priority (combat, loot)
        if (waitForLoot != null && waitForLoot.activate()) {
            status = "Drop";
            return State.WaitForLoot;
        }
        if (lootAction.activate()) {
            status = "Loot";
            return State.Loot;
        }
        if (bonesToPeachesLootAction != null && bonesToPeachesLootAction.activate()) {
            status = bonesToPeachesLootAction.getStatus();
            return State.B2PLoot;
        }
        if (buryBones != null && buryBones.activate()) {
            status = "Bones";
            return State.BuryBones;
        }
        if (superiorCombatAction != null && superiorCombatAction.activate()) {
            status = "Superior";
            return State.SuperiorCombat;
        }
        if (combatAction.activate()) {
            status = "Combat";
            return State.Combat;
        }
        if (ctx.combat.specialPercentage() >= 60 && !ctx.combat.specialAttack() && specWeaponIds.contains(ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id())) {
            status = "Spec";
            return State.Spec;
        }


        // Low Priority (Alch, Levels)
        if (highAlch.activate()) {
            status = "Alch";
            return State.Alch;
        }
=======
            // Checks while out of combat
            if (this.buryBones != null && this.buryBones.activate() && ctx.skills.realLevel(Constants.SKILLS_PRAYER) - ctx.skills.level(Constants.SKILLS_PRAYER) > 20 && !ctx.players.local().interacting().valid()) {
                status = "Bury";
                return State.BuryBones;
            }
            if (bonesToPeaches != null && bonesToPeaches.activate() && ctx.inventory.select().id(Items.BONES_TO_PEACHES_8015).count() > 0) {
                status = "B2P";
                return State.BonesToPeaches;
            }
            if (combatPotion.activate() && ctx.inventory.select().id(combatPotionIds).count() > 0) {
                status = "Combat Potion";
                return State.CombatPotion;
            }
            if (lootAction.activate()) {
                status = "Loot";
                return State.Loot;
            }
            if (bonesToPeachesLootAction != null && bonesToPeachesLootAction.activate()) {
                status = bonesToPeachesLootAction.getStatus();
                return State.B2PLoot;
            }
            if (superiorCombatAction != null && superiorCombatAction.activate()) {
                status = "Superior";
                return State.SuperiorCombat;
            }
            if (combatAction.activate()) {
                status = "Combat";
                return State.Combat;
            }
            if (highAlch.activate()) {
                status = "Alch";
                return State.Alch;
            }
        }

>>>>>>> 9aa08e59baaf883509287c175f84823629cb01dd:src/non_active/slayer_simple/_SimpleSlayerPlus.java
        if (toggleLevelUp.activate()) {
            status = "Level";
            return State.LevelUp;
        }
<<<<<<< HEAD:src/scripts/slayer_simple/_SimpleSlayerPlus.java
        if (teletab != null && teletab.activate()) {
            status = "Tele";
            return State.Teletab;
        }
=======
>>>>>>> 9aa08e59baaf883509287c175f84823629cb01dd:src/non_active/slayer_simple/_SimpleSlayerPlus.java

        // Default
        return State.Wait;

    }

    @Override
    public void start() {
        // Config
        init();

        // Get Target
        npcName = CommonActions.promptForSelection("Target NPC Name", "Target", targetNames);

        if (CommonActions.promptForYesNo("Bury Bones", "")) {
            this.buryBones = new BuryBones(ctx, "Bury", Items.BIG_BONES_532, false);
//            this.lootList.addLootItem(new LootItem(Items.BONES_526, 1, 5));
            this.lootList.addLootItem(new LootItem(Items.BIG_BONES_532, 1, 5));
//            this.lootList.addLootItem(new LootItem(Items.DRAGON_BONES_536, 1, 5));
        }

        // Slayer Config
        slayerConfig();

        // Healing Methods
        // Guthans
        if (taskConfig.isUsingGuthans()) {
            equipGuthans = new EquipGuthans(ctx, 70);
            equipGear = new EquipGear(ctx, 90, true, ctx.equipment.itemAt(Equipment.Slot.HEAD).id(), ctx.equipment.itemAt(Equipment.Slot.TORSO).id(), ctx.equipment.itemAt(Equipment.Slot.LEGS).id(), ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id(), ctx.equipment.itemAt(Equipment.Slot.OFF_HAND).id());
            // GUI
            healingMethod = "Guthans";
        }

        // Prayer
        if (taskConfig.isUsingPrayer() && ctx.inventory.select().id(Items.PRAYER_POTION4_2434).count() > 0) {
            prayerPotionIds = new int[]{Items.PRAYER_POTION4_2434, Items.PRAYER_POTION3_139, Items.PRAYER_POTION2_141, Items.PRAYER_POTION1_143};
            prayerPotion = new UsePotion(ctx, "Prayer Pot", prayerPotionIds, Constants.SKILLS_PRAYER, 5, 15, true);

            healingMethod = "Prayer";
        }

        // Bones to Peaches
        if (taskConfig.isUsingBonesToPeaches() && ctx.inventory.select().id(Items.BONES_TO_PEACHES_8015).count() > 0) {
            bonesToPeaches = new BonesToPeaches(ctx, 8, taskConfig.getBonesToPeachesBoneId());
            bonesToPeachesLootAction = new BonesToPeachesLootAction(ctx, "B2P Loot");
            healingMethod = "B2P";
        }


        // Heal Config
        healAction = new HealAction(ctx, "Healing", CommonActions.allFoodIds(), taskConfig.getEatFoodMinHealthPercentage());


        // Superior Config
        if (taskConfig.getSuperiorCombatConfig() != null) {
            superiorCombatAction = new CombatAction(ctx, taskConfig.getSuperiorCombatConfig());
            superiorSlayerLoot();
        }

        // Combat Config
        CombatConfig _combatConfig;
        if (taskConfig.getCustomCombatConfig() == null) {
            _combatConfig = new CombatConfig(npcName, -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, ctx.combat.inMultiCombat(), (safetile));
        } else {
            _combatConfig = taskConfig.getCustomCombatConfig();
        }

        // Combat Action
        if (taskConfig.getCustomCombatAction() == null) {
            combatAction = new CombatAction(ctx, _combatConfig);
        } else {
            combatAction = taskConfig.getCustomCombatAction();
        }

        // Loot Task
<<<<<<< HEAD:src/scripts/slayer_simple/_SimpleSlayerPlus.java
        lootAction = new LootAction(ctx, "Loot", lootList, 5, true);
=======
        lootAction = new LootAction(ctx, "Loot", lootList, -1, true);
>>>>>>> 9aa08e59baaf883509287c175f84823629cb01dd:src/non_active/slayer_simple/_SimpleSlayerPlus.java

        // Loot Drop
        if (taskConfig.isWaitingForLootDrop()) {
            waitForLoot = new WaitForCombatLoot(ctx);
        }

        // Alch Config
        highAlch = new HighAlch(ctx, "Alch", alchables, false, true);

        // Level Up
        toggleLevelUp = new ToggleLevelUp(ctx);

        // GUI Final Checks
        if (!taskConfig.isUsingPrayer() && !taskConfig.isUsingGuthans()) {
            healingMethod = "Food";
        }

        // Combat Potions
        if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() == Items.DRAGON_CROSSBOW_21902) {
            //Range Task
            combatPotionIds = new int[]{Items.RANGING_POTION4_2444, Items.RANGING_POTION3_169, Items.RANGING_POTION2_171, Items.RANGING_POTION1_173};
<<<<<<< HEAD:src/scripts/slayer_simple/_SimpleSlayerPlus.java
            combatPotion = new UsePotion(ctx, "Range Pot", combatPotionIds, Constants.SKILLS_RANGE, ctx.skills.realLevel(Constants.SKILLS_RANGE) + 4, ctx.skills.realLevel(Constants.SKILLS_RANGE) + 6, true);
=======
            combatPotion = new UsePotion(ctx, "Range Pot", combatPotionIds, Constants.SKILLS_RANGE, ctx.skills.realLevel(Constants.SKILLS_RANGE) + 2, (int) (ctx.skills.realLevel(Constants.SKILLS_RANGE) * 1.1) + 4, true);
>>>>>>> 9aa08e59baaf883509287c175f84823629cb01dd:src/non_active/slayer_simple/_SimpleSlayerPlus.java
        } else {
            combatPotionIds = new int[]{Items.SUPER_COMBAT_POTION1_12701, Items.SUPER_COMBAT_POTION2_12699, Items.SUPER_COMBAT_POTION3_12697, Items.SUPER_COMBAT_POTION4_12695};
            combatPotion = new UsePotion(ctx, "Combat Pot", combatPotionIds, Constants.SKILLS_STRENGTH, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 4, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 6, true);
        }
        grimyHerbs = new int[]{Items.GRIMY_RANARR_WEED_207, Items.GRIMY_LANTADYME_2485, Items.GRIMY_AVANTOE_211, Items.GRIMY_KWUARM_213, Items.GRIMY_CADANTINE_215, Items.GRIMY_IRIT_LEAF_209};


        // Check counts on Slayer Helm
<<<<<<< HEAD:src/scripts/slayer_simple/_SimpleSlayerPlus.java
        CommonActions.openTab(ctx, Game.Tab.EQUIPMENT);
        ctx.equipment.itemAt(Equipment.Slot.HEAD).interact("Check");
        sleep();
        CommonActions.openTab(ctx, Game.Tab.INVENTORY);
=======
//        CommonActions.openTab(ctx, Game.Tab.EQUIPMENT);
//        ctx.equipment.itemAt(Equipment.Slot.HEAD).interact("Check");
//        sleep();
//        CommonActions.openTab(ctx, Game.Tab.INVENTORY);
>>>>>>> 9aa08e59baaf883509287c175f84823629cb01dd:src/non_active/slayer_simple/_SimpleSlayerPlus.java

        status = "Started";
    }

    @Override
    public void poll() {
        killCount = (int) Math.abs(((ctx.skills.experience(Constants.SKILLS_SLAYER) - startXP) / taskConfig.getXpPerKill()));

        switch (checkState()) {
            case Healing:

                healAction.execute();
                break;
            case PrayerPotion:
                prayerPotion.execute();
                this.potionDosesUsed++;
                break;
            case BonesToPeaches:
                bonesToPeaches.execute();
                break;
            case Guthans:
                equipGuthans.execute();
                break;
            case Gear:
                equipGear.execute();
                break;
            case CombatPotion:
                combatPotion.execute();
                break;
            case WaitForLoot:
                waitForLoot.execute();
                break;
            case BuryBones:
                buryBones.execute();
                break;
            case Loot:
                if (healingMethod.equals("Prayer") && ctx.prayer.prayersActive()) {
                    ctx.prayer.quickPrayer(false);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !ctx.prayer.quickSelectionActive();
                        }
                    }, 100, 10);
                }
                lootAction.execute();
                sleep();
                if (ctx.inventory.select().id(grimyHerbs).count() > 0 && ctx.inventory.select().id(Items.HERB_SACK_13226).count() == 1) {
                    ctx.inventory.select().id(Items.HERB_SACK_13226).poll().interact("Fill");
                    sleep();
                }
                break;
            case B2PLoot:
                bonesToPeachesLootAction.execute();
                break;
            case SuperiorCombat:
                if (healingMethod.equals("Prayer") && !ctx.prayer.prayersActive()) {
                    ctx.prayer.quickPrayer(true);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.prayer.quickSelectionActive();
                        }
                    }, 100, 10);
                }
                superiorCombatAction.execute();
                break;
            case Combat:
                if (healingMethod.equals("Prayer") && !ctx.prayer.prayersActive()) {
                    ctx.prayer.quickPrayer(true);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.prayer.quickSelectionActive();
                        }
                    }, 100, 10);
                }
                combatAction.execute();
                break;
            case Spec:
                ctx.widgets.component(160, 32).click();
                break;
            case MoveAwayFromTarget:
                moveAwayFromTarget.execute();
                break;
            case SafeTile:
                moveToSafeTile.execute();
                break;
            case Alch:
                highAlch.execute();
                break;
            case LevelUp:
                toggleLevelUp.execute();
                break;
            case Teletab:
                teletab.execute();
                break;
            default: // waiting
                status = "Waiting";
                if (getRuntime() < nextAntibanActionTime) {
                    AntibanTools.runCommonAntiban(ctx);
                    nextAntibanActionTime = getRuntime() + AntibanTools.getRandomInRange(1, 3);
                }

        }
    }

    @Override
    public void repaint(Graphics g) {
        if (!ctx.controller.isSuspended() && taskConfig != null) {
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

            if (taskConfig.isUsingPrayer())
                g.drawString("Doses Used: " + potionDosesUsed, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));

        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

<<<<<<< HEAD:src/scripts/slayer_simple/_SimpleSlayerPlus.java
        if (msg.toLowerCase().contains("return to a slayer master")) {
            CommonActions.slayerRingTeleport(ctx, 1);
            ctx.controller.stop();
        }
=======
        if (msg.contains("return to a slayer master")) {
            CommonActions.slayerRingTeleport(ctx, 1);
            ctx.controller.stop();
        }
        if (msg.contains("some of your") && msg.contains("potion")) {
            this.potionDosesUsed++;
        }
>>>>>>> 9aa08e59baaf883509287c175f84823629cb01dd:src/non_active/slayer_simple/_SimpleSlayerPlus.java
        if (msg.contains("monsters to complete your current slayer assignment")) {
            String[] msgparts = msg.split(" ");

            remainingKills = Integer.parseInt(msgparts[5]);
            startXP = ctx.skills.experience(Constants.SKILLS_SLAYER);
        }
        if (msg.contains("assigned to kill") && msg.contains("more to go")) {
            String[] msgparts = msg.split("; ")[1].split(" ");

            remainingKills = Integer.parseInt(msgparts[1]);
            startXP = ctx.skills.experience(Constants.SKILLS_SLAYER);
        }
        if (msg.contains("no ammo left")) {
            ctx.controller.stop();
        }
    }

    // Slayer Configurations
    private void slayerConfig() {

        switch (npcName) {
            case "Bloodveld":
                bloodveld();
                break;
            case "Dagannoth":
                dagannoth();
                break;
            case "Fire giant":
                fireGiant();
                break;
            case "Mountain troll":
                mountainTroll();
                break;
            case "Ankou":
                ankou();
                break;
            case "Aberrant spectre":
                aberrantSpectre();
                break;
            case "Deviant spectre":
                deviantSpectre();
                break;
            case "Black demon":
                blackDemon();
                break;
            case "Greater demon":
                greaterDemon();
                break;
            case "Hellhound":
                hellhound();
                break;
            case "Jelly":
                jelly();
                break;
            case "Turoth":
                turoth();
                break;
            case "Kurask":
                kurask();
                break;
            case "Kalphite Worker":
                kalphite();
                break;
            case "Blue dragon":
                blueDragon();
                break;
            case "Wyrm":
                wyrm();
                break;
            case "Warped Jelly":
                warpedJelly();
                break;
            case "Mutated Bloodveld":
                mutatedBloodveld();
                break;
            case "Gargoyle":
                gargoyle();
                break;
            case "Nechryael":
                nechryael();
                break;
            case "Greater Nechryael":
                greaterNechryael();
                break;
            default:
                gemDropTable();
                standardRareDropTable();
        }

        konarDropTable();

    }

    // Loot Configs
    private void bloodveld() {
        lootList.addLootItem(new LootItem(Items.RUNE_MED_HELM_1147));
        lootList.addLootItem(new LootItem(Items.MITHRIL_CHAINBODY_1109));
        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
        lootList.addLootItem(new LootItem(Items.FIRE_RUNE_554));
        lootList.addLootItem(new LootItem(Items.MEAT_PIZZA_2293));

        taskConfig.setXpPerKill(120);
        taskConfig.setUsingBonesToPeaches(true);
        taskConfig.setWaitingForLootDrop(false);
    }

    private void mutatedBloodveld() {
        lootList.addLootItem(new LootItem(Items.RUNE_MED_HELM_1147));
        lootList.addLootItem(new LootItem(Items.MITHRIL_PLATEBODY_1121));
        lootList.addLootItem(new LootItem(Items.RUNE_DAGGER_1213));
        lootList.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        lootList.addLootItem(new LootItem(Items.ADAMANT_CHAINBODY_1111));
        lootList.addLootItem(new LootItem(Items.ADAMANT_SCIMITAR_1331));
        lootList.addLootItem(new LootItem(Items.ADAMANT_LONGSWORD_1301));
        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
        lootList.addLootItem(new LootItem(Items.SOUL_RUNE_566));

        gemDropTable();

        lootList.addLootItem(new LootItem(Items.DUST_BATTLESTAFF_20736));
        lootList.addLootItem(new LootItem(Items.MIST_BATTLESTAFF_20730));


        taskConfig.setXpPerKill(170);
        taskConfig.setUsingPrayer(true);

        taskConfig.setSuperiorCombatConfig(new CombatConfig("Insatiable mutated Bloodveld", -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, ctx.combat.inMultiCombat(), safetile));

    }

    private void mountainTroll() {
        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));
        lootList.addLootItem(new LootItem(Items.EARTH_RUNE_557));
        lootList.addLootItem(new LootItem(Items.RANARR_SEED_5295));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));
        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));
        lootList.addLootItem(new LootItem(Items.COAL_NOTED_454));
        standardRareDropTable();

        taskConfig.setXpPerKill(90);
        taskConfig.setUsingGuthans(true);

    }

    private void fireGiant() {
        lootList.addLootItem(new LootItem(Items.FIRE_BATTLESTAFF_1393));
        lootList.addLootItem(new LootItem(Items.RUNE_SCIMITAR_1333));
        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
        lootList.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        lootList.addLootItem(new LootItem(Items.LAW_RUNE_563));

        lootList.addLootItem(new LootItem(Items.RUNE_ARROW_892));

        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));
        lootList.addLootItem(new LootItem(Items.GRIMY_AVANTOE_211));

        gemDropTable();
        standardRareDropTable();

        taskConfig.setXpPerKill(111);
    }

    private void ankou() {
        lootList.addLootItem(new LootItem(Items.ADAMANT_ARROW_890));

        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        lootList.addLootItem(new LootItem(Items.LAW_RUNE_563));

        lootList.addLootItem(new LootItem(Items.RANARR_SEED_5295));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));

        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));
        lootList.addLootItem(new LootItem(Items.GRIMY_AVANTOE_211));

        lootList.addLootItem(new LootItem(Items.PURE_ESSENCE_NOTED_7937));
        lootList.addLootItem(new LootItem(Items.MITHRIL_ORE_NOTED_448));
        lootList.addLootItem(new LootItem(Items.LEFT_SKULL_HALF_9008));

        gemDropTable();

        taskConfig.setXpPerKill(60);
        taskConfig.setUsingGuthans(true);
    }

    private void aberrantSpectre() {
        lootList.addLootItem(new LootItem(Items.LAVA_BATTLESTAFF_3053));
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        lootList.addLootItem(new LootItem(Items.MYSTIC_ROBE_BOTTOM_DARK_4103));
        lootList.addLootItem(new LootItem(Items.ADAMANT_PLATELEGS_1073));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        lootList.addLootItem(new LootItem(Items.LAW_RUNE_563));

        lootList.addLootItem(new LootItem(Items.CADANTINE_SEED_5301));
        lootList.addLootItem(new LootItem(Items.LANTADYME_SEED_5302));
        lootList.addLootItem(new LootItem(Items.DWARF_WEED_SEED_5303));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));
        lootList.addLootItem(new LootItem(Items.SNAPE_GRASS_SEED_22879));

        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));
        lootList.addLootItem(new LootItem(Items.GRIMY_SNAPDRAGON_3051));
        lootList.addLootItem(new LootItem(Items.GRIMY_LANTADYME_2485));
        lootList.addLootItem(new LootItem(Items.GRIMY_CADANTINE_215));
        lootList.addLootItem(new LootItem(Items.GRIMY_AVANTOE_211));
        lootList.addLootItem(new LootItem(Items.GRIMY_KWUARM_213));
        lootList.addLootItem(new LootItem(Items.GRIMY_HARRALANDER_205));
        lootList.addLootItem(new LootItem(Items.GRIMY_IRIT_LEAF_209));

        lootList.addLootItem(new LootItem(Items.BONES_526, 1, 1));


        gemDropTable();

        taskConfig.setXpPerKill(90);
        taskConfig.setUsingPrayer(true);
        taskConfig.setSuperiorCombatConfig(new CombatConfig("Abhorrent spectre", -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, ctx.combat.inMultiCombat(), safetile));
        taskConfig.setWaitingForLootDrop(true);
    }

    private void deviantSpectre() {
        lootList.addLootItem(new LootItem(Items.LAVA_BATTLESTAFF_3053));
        lootList.addLootItem(new LootItem(Items.BATTLESTAFF_1391));
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        lootList.addLootItem(new LootItem(Items.RUNE_CHAINBODY_1113));
        lootList.addLootItem(new LootItem(Items.MYSTIC_ROBE_BOTTOM_DARK_4103));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.CADANTINE_SEED_5301));
        lootList.addLootItem(new LootItem(Items.LANTADYME_SEED_5302));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));
        lootList.addLootItem(new LootItem(Items.AVANTOE_SEED_5298));
        lootList.addLootItem(new LootItem(Items.SNAPE_GRASS_SEED_22879));

        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));

        // Superior Loot
        lootList.addLootItem(new LootItem(Items.DUST_BATTLESTAFF_20736));
        lootList.addLootItem(new LootItem(Items.MIST_BATTLESTAFF_20730));

        gemDropTable();

        taskConfig.setXpPerKill(194.5);
        taskConfig.setUsingPrayer(true);
        taskConfig.setWaitingForLootDrop(true);
        taskConfig.setSuperiorCombatConfig(new CombatConfig("Abhorrent spectre", -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, ctx.combat.inMultiCombat(), safetile));
    }

    private void blackDemon() {
        lootList.addLootItem(new LootItem(Items.RUNE_MED_HELM_1147));
        lootList.addLootItem(new LootItem(Items.RUNE_CHAINBODY_1113));

        alchables = lootList.allItemIds();

        gemDropTable();

        taskConfig.setXpPerKill(157);
    }

    private void greaterDemon() {
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        gemDropTable();

        taskConfig.setXpPerKill(87);


        if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() == Items.DRAGON_CROSSBOW_21902) {
            moveToSafeTile = new MoveToSafeTile(ctx, ctx.players.local().tile());
        } else {
            taskConfig.setUsingGuthans(true);
            taskConfig.setEquipGuthansMinHealthPercentage(60);
            taskConfig.setEatFoodMinHealthPercentage(40);
        }
    }

    private void hellhound() {

        lootList.addLootItem(new LootItem(Items.SMOULDERING_STONE_13233));
//        lootList.addLootItem(new LootItem(Items.BONES_526, 1, 10));
        taskConfig.setXpPerKill(116);
        taskConfig.setUsingGuthans(true);

//        taskConfig.setUsingBonesToPeaches(true);

    }

    private void turoth() {
        lootList.addLootItem(new LootItem(Items.ADAMANT_FULL_HELM_1161));
        lootList.addLootItem(new LootItem(Items.RUNE_DAGGER_1213));
        lootList.addLootItem(new LootItem(Items.LEAF_BLADED_SWORD_11902));
        lootList.addLootItem(new LootItem(Items.MYSTIC_ROBE_BOTTOM_LIGHT_4113));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));

        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));

        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));
        lootList.addLootItem(new LootItem(Items.GRIMY_AVANTOE_211));

        standardRareDropTable();

        taskConfig.setXpPerKill(76);
        taskConfig.setBonesToPeachesBoneId(Items.BONES_526);
        taskConfig.setUsingBonesToPeaches(true);
    }

    private void kurask() {
        lootList.addLootItem(new LootItem(Items.ADAMANT_PLATEBODY_1123));
        lootList.addLootItem(new LootItem(Items.MYSTIC_ROBE_TOP_LIGHT_4111));
        lootList.addLootItem(new LootItem(Items.RUNE_LONGSWORD_1303));
        lootList.addLootItem(new LootItem(Items.RUNE_LONGSWORD_6897));
        lootList.addLootItem(new LootItem(Items.RUNE_AXE_1359));
        lootList.addLootItem(new LootItem(Items.LEAF_BLADED_SWORD_11902));
        lootList.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        lootList.addLootItem(new LootItem(Items.RUNE_2H_SWORD_1319));
        lootList.addLootItem(new LootItem(Items.MITHRIL_KITESHIELD_1197));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.LEAF_BLADED_BATTLEAXE_20727));
        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));

        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));

        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));

        lootList.addLootItem(new LootItem(Items.BIG_BONES_NOTED_533));
        lootList.addLootItem(new LootItem(Items.PAPAYA_FRUIT_NOTED_5973));
        lootList.addLootItem(new LootItem(Items.WHITE_BERRIES_NOTED_240));
        lootList.addLootItem(new LootItem(Items.COCONUT_NOTED_5975));


        standardRareDropTable();

        taskConfig.setXpPerKill(97);
        taskConfig.setUsingBonesToPeaches(true);
        taskConfig.setBonesToPeachesBoneId(Items.BONES_526);
        taskConfig.setEatFoodMinHealthPercentage(75);

    }

    private void kalphite() {


        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));

        gemDropTable();

        taskConfig.setXpPerKill(40);
    }

    private void blueDragon() {

        lootList.addLootItem(new LootItem(Items.RUNE_DAGGER_1213));
        lootList.addLootItem(new LootItem(Items.DRAGON_BONES_536));

        lootList.addLootItem(new LootItem(Items.BLUE_DRAGONHIDE_1751));
        lootList.addLootItem(new LootItem(Items.ENSOULED_DRAGON_HEAD_13510));
        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));
        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));

        gemDropTable();

        taskConfig.setXpPerKill(107.5);

        // Teleport Out
        teletab = new Teletab(ctx, Items.FALADOR_TELEPORT_8009, true);

        // Use Safetile for Blue Dragons in Taverly.
        safetile = ctx.players.local().tile();
        moveToSafeTile = new MoveToSafeTile(ctx, safetile);
    }

    private void wyrm() {

        lootList.addLootItem(new LootItem(Items.EARTH_BATTLESTAFF_1399));
        lootList.addLootItem(new LootItem(Items.RUNE_MED_HELM_1147));
        lootList.addLootItem(new LootItem(Items.DRAGON_DAGGER_1215));
        lootList.addLootItem(new LootItem(Items.ADAMANT_SQ_SHIELD_1183));
        lootList.addLootItem(new LootItem(Items.RED_DHIDE_CHAPS_2495));
        lootList.addLootItem(new LootItem(Items.RED_DHIDE_CHAPS_20567));
        lootList.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        lootList.addLootItem(new LootItem(Items.ADAMANT_2H_SWORD_1317));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.RUNE_ARROWTIPS_44));
        lootList.addLootItem(new LootItem(Items.DRAGON_SWORD_21009));
        lootList.addLootItem(new LootItem(Items.DRAGON_HARPOON_21028));
        lootList.addLootItem(new LootItem(22804)); // Dragon Knife
        lootList.addLootItem(new LootItem(Items.DRAGON_THROWNAXE_20849));
        lootList.addLootItem(new LootItem(Items.DRAGON_THROWNAXE_21207));

        lootList.addLootItem(new LootItem(Items.SOUL_RUNE_566));
        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
//        lootList.addLootItem(new LootItem(Items.FIRE_RUNE_554, 200, 1000));
<<<<<<< HEAD:src/scripts/slayer_simple/_SimpleSlayerPlus.java
        lootList.addLootItem(new LootItem(Items.EARTH_RUNE_557, 75, 5000));
=======
//        lootList.addLootItem(new LootItem(Items.EARTH_RUNE_557, 75, 1000));
>>>>>>> 9aa08e59baaf883509287c175f84823629cb01dd:src/non_active/slayer_simple/_SimpleSlayerPlus.java

        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));
        lootList.addLootItem(new LootItem(Items.SNAPE_GRASS_SEED_22879));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));

        standardRareDropTable();

        taskConfig.setXpPerKill(133.2);
        taskConfig.setUsingPrayer(true);
        taskConfig.setCustomCombatConfig(new CombatConfig(npcName, -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, false, safetile, -1));
        taskConfig.setCustomCombatAction(new WyrmCombat(ctx, "Combat", taskConfig.getCustomCombatConfig(), alchables));

        // Bury bones if using dragonbone amulet
        if (CommonActions.promptForYesNo("Bury Bones", "")) {
            this.buryBones = new BuryBones(ctx, "Bury", 22780, 4, true);
            this.lootList.addLootItem(new LootItem(22780, 1, 5));
        }

        // Add task to shift away from melee range
        moveAwayFromTarget = new MoveAwayFromTarget(ctx, 3);
    }

    private void drake() {

        lootList.addLootItem(new LootItem(Items.MYSTIC_EARTH_STAFF_1407));
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        lootList.addLootItem(new LootItem(Items.DRAGON_MACE_1434));
        lootList.addLootItem(new LootItem(Items.BLACK_DHIDE_VAMB_2491));
        lootList.addLootItem(new LootItem(Items.RED_DHIDE_BODY_2501));
        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.DRAGON_THROWNAXE_21207));
        lootList.addLootItem(new LootItem(22804)); // Dragon Knife

        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        lootList.addLootItem(new LootItem(Items.LAW_RUNE_563));
        lootList.addLootItem(new LootItem(Items.RUNE_ARROW_892));

        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_207));
        lootList.addLootItem(new LootItem(Items.GRIMY_RANARR_WEED_NOTED_208));
        lootList.addLootItem(new LootItem(Items.GRIMY_SNAPDRAGON_3051));
        lootList.addLootItem(new LootItem(Items.GRIMY_SNAPDRAGON_NOTED_3052));

        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));

        // Uniques
        lootList.addLootItem(new LootItem(Items.DIAMOND_NOTED_1602));
        lootList.addLootItem(new LootItem(22960)); // Drakes Tooth
        lootList.addLootItem(new LootItem(22957)); // Drakes Claw


        taskConfig.setXpPerKill(268);
        taskConfig.setUsingPrayer(true);
        taskConfig.setCustomCombatConfig(new CombatConfig(npcName, -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, false, safetile, 3));

        // Add task to shift away from melee range
        moveAwayFromTarget = new MoveAwayFromTarget(ctx, taskConfig.getCustomCombatConfig().getMinDistanceToTarget());
    }

    private void warpedJelly() {

        alchables = new int[0];

        lootList.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        lootList.addLootItem(new LootItem(Items.ANCIENT_SHARD_19677));

        gemDropTable();

        taskConfig.setXpPerKill(140);
        taskConfig.setUsingPrayer(true);
        taskConfig.setSuperiorCombatConfig(new CombatConfig("Vitreous Jelly", -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, ctx.combat.inMultiCombat(), safetile));

    }

    private void jelly() {

        //lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));

        alchables = new int[0];

        lootList.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));

        gemDropTable();

        taskConfig.setXpPerKill(75);
        taskConfig.setUsingPrayer(true);

        taskConfig.setSuperiorCombatConfig(new CombatConfig("Vitreous Jelly", -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, ctx.combat.inMultiCombat(), safetile));
    }

    private void dagannoth() {
        taskConfig.setXpPerKill(120);
        taskConfig.setUsingGuthans(true);
        taskConfig.setEatFoodMinHealthPercentage(60);
    }

    private void gargoyle() {

        lootList.addLootItem(new LootItem(Items.ADAMANT_PLATELEGS_1073));
        lootList.addLootItem(new LootItem(Items.RUNE_PLATELEGS_1079));
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        lootList.addLootItem(new LootItem(Items.RUNE_2H_SWORD_1319));
        lootList.addLootItem(new LootItem(Items.ADAMANT_BOOTS_4129));
        lootList.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        lootList.addLootItem(new LootItem(Items.GRANITE_MAUL_4153));
        lootList.addLootItem(new LootItem(Items.MYSTIC_ROBE_TOP_DARK_4101));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));

        lootList.addLootItem(new LootItem(Items.STEEL_BAR_NOTED_2354));
        lootList.addLootItem(new LootItem(Items.MITHRIL_BAR_NOTED_2360));
        lootList.addLootItem(new LootItem(Items.RUNITE_ORE_451));
        lootList.addLootItem(new LootItem(Items.GOLD_ORE_NOTED_445));

        standardRareDropTable();

        taskConfig.setXpPerKill(105);
        taskConfig.setUsingGuthans(true);
        taskConfig.setCustomCombatConfig(new CombatConfig("Gargoyle", -1, taskConfig.getEatFoodMinHealthPercentage(), lootList, ctx.combat.inMultiCombat(), null));
        taskConfig.setCustomCombatAction(new GargoyleCombat(ctx, "Combat", taskConfig.getCustomCombatConfig()));

        waitForLoot = new WaitForCombatLoot(ctx);
    }

    private void nechryael() {
        lootList.addLootItem(new LootItem(Items.RUNE_2H_SWORD_1319));
        lootList.addLootItem(new LootItem(Items.ADAMANT_KITESHIELD_1199));
        lootList.addLootItem(new LootItem(Items.ADAMANT_PLATELEGS_1073));
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        lootList.addLootItem(new LootItem(Items.RUNE_BOOTS_4131));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.LAW_RUNE_563));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        lootList.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));

        lootList.addLootItem(new LootItem(Items.SNAPE_GRASS_SEED_22879));
        lootList.addLootItem(new LootItem(Items.AVANTOE_SEED_5298));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.CADANTINE_SEED_5301));
        lootList.addLootItem(new LootItem(Items.LANTADYME_SEED_5302));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));

        lootList.addLootItem(new LootItem(Items.SOFT_CLAY_NOTED_1762));
        lootList.addLootItem(new LootItem(Items.COINS_995, 1000));

        taskConfig.setXpPerKill(105);
        taskConfig.setUsingGuthans(true);
        taskConfig.setEatFoodMinHealthPercentage(50);
        taskConfig.setEquipGuthansMinHealthPercentage(70);
        taskConfig.setWaitingForLootDrop(true);

    }

    private void greaterNechryael() {
        lootList.addLootItem(new LootItem(Items.RUNE_2H_SWORD_1319));
        lootList.addLootItem(new LootItem(Items.RUNE_AXE_1359));
        lootList.addLootItem(new LootItem(Items.ADAMANT_KITESHIELD_1199));
        lootList.addLootItem(new LootItem(Items.RUNE_SQ_SHIELD_1185));
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        lootList.addLootItem(new LootItem(Items.RUNE_BOOTS_4131));
        lootList.addLootItem(new LootItem(Items.RUNE_MED_HELM_1147));
        lootList.addLootItem(new LootItem(Items.RUNE_CHAINBODY_1113));
        lootList.addLootItem(new LootItem(Items.MYSTIC_AIR_STAFF_1405));

        alchables = lootList.allItemIds();

        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        lootList.addLootItem(new LootItem(Items.CHAOS_RUNE_562));
        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
        lootList.addLootItem(new LootItem(Items.SOUL_RUNE_566));

        lootList.addLootItem(new LootItem(Items.AVANTOE_SEED_5298));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.CADANTINE_SEED_5301));
        lootList.addLootItem(new LootItem(Items.LANTADYME_SEED_5302));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));

        lootList.addLootItem(new LootItem(Items.WINE_OF_ZAMORAK_NOTED_246));
        lootList.addLootItem(new LootItem(Items.GOLD_BAR_NOTED_2358));
        lootList.addLootItem(new LootItem(Items.LOBSTER_379, 1, 2));
        lootList.addLootItem(new LootItem(Items.TUNA_361, 1, 2));

        standardRareDropTable();

        taskConfig.setXpPerKill(210);
        taskConfig.setUsingPrayer(true);
        taskConfig.setUsingGuthans(true);
        taskConfig.setEatFoodMinHealthPercentage(60);
        taskConfig.setWaitingForLootDrop(true);

    }

    // Static Drop Tables
    private void gemDropTable() {
        lootList.addLootItem(new LootItem(Items.UNCUT_DIAMOND_1617));
        lootList.addLootItem(new LootItem(Items.RUNE_JAVELIN_830));
        lootList.addLootItem(new LootItem(Items.LOOP_HALF_OF_KEY_987));
        lootList.addLootItem(new LootItem(Items.TOOTH_HALF_OF_KEY_985));
        lootList.addLootItem(new LootItem(Items.RUNE_SPEAR_1247));
        lootList.addLootItem(new LootItem(Items.SHIELD_LEFT_HALF_2366));
        lootList.addLootItem(new LootItem(Items.DRAGON_SPEAR_1249));

    }

    private void standardRareDropTable() {
        lootList.addLootItem(new LootItem(Items.RUNITE_BAR_2363));
        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));
        lootList.addLootItem(new LootItem(Items.RUNE_2H_SWORD_1319));
        lootList.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        lootList.addLootItem(new LootItem(Items.RUNE_KITESHIELD_1201));
        lootList.addLootItem(new LootItem(Items.DRAGONSTONE_1615));
        lootList.addLootItem(new LootItem(Items.LAW_RUNE_563, 45));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560, 45));
        lootList.addLootItem(new LootItem(Items.SILVER_ORE_NOTED_443, 100));
        lootList.addLootItem(new LootItem(Items.DRAGON_MED_HELM_1149));
        lootList.addLootItem(new LootItem(Items.SHIELD_LEFT_HALF_2366));
    }

    private void superiorSlayerLoot() {
        lootList.addLootItem(new LootItem(Items.IMBUED_HEART_20724));
        lootList.addLootItem(new LootItem(Items.ETERNAL_GEM_21270));
    }

    private void konarDropTable() {
        lootList.addLootItem(new LootItem(23083)); // Brimstone Key
        lootList.addLootItem(new LootItem(Items.COINS_995, 800)); // 800gp Coins
    }
}


