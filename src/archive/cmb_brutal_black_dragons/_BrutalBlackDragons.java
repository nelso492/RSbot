package ngc.cmb_brutal_black_dragons;

import ngc._resources.Items;
import ngc._resources.actions.*;
import ngc._resources.actions._config.CombatConfig;
import ngc._resources.actions._config.HealConfig;
import ngc._resources.functions.CommonFunctions;
import ngc._resources.functions.GuiHelper;
import ngc._resources.models.LootItem;
import ngc._resources.models.LootList;
import ngc.slayer_simple.Teletab;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Brutal Black Dragons AIO", description = "Kills BBD. Bank in Ardy. Pray restore in Kourend", properties = "client=4; topic=051515; author=Bowman")
public class _BrutalBlackDragons extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private String status;
    private Phase currentPhase;
    private int combatMinHealthPercent;
    private int[] combatPotionIds;
    private int[] prayerPotionIds;
    private int[] antifirePotionIds;
    private int closedMonestaryDoorId;
    private int prayerAltarId;
    private int pitId;
    private Npc currentTarget;
    private int boltId;
    private int primaryBoltId;
    private int secondaryBoltId;
    private int nextSpecPercent;
    private int[] droppables; //Droppable items that aren't pots, bolts, etc
    private Long lastGuiUpdateTimestamp;
    private String readableTimestamp;
    private int tripCount;
    private int invCount;

    // Paths
    private Tile[] pathToBank;
    private Tile[] pathToMonastary;
    private Tile[] pathToPit;

    // Areas
    private Area monestaryDoorArea = new Area(new Tile(1534, 3810), new Tile(1537, 3806));
    private Area pitArea = new Area(new Tile(1561, 3791), new Tile(1564, 3794));
    private Tile monestaryTile;

    // Loot
    private LootList lootList;

    // Tasks
    private CombatConfig combatConfig;
    private HealAction eatFood;
    private CombatAction combatAction;
    private BbdLoot lootAction;
    private UsePotion combatPotion;
    private UsePotion prayerPotion;
    private UsePotion antifirePotion;
    private MoveAwayFromTarget moveAwayFromTarget;
    private Teletab faladorTeleport;
    private BbdBankAction bankAction;
    private WaitForCombatLoot waitForCombatLoot;


    // ENUMS
    private enum Phase {
        Start, Combat, Banking, Reset, Prayer
    }

    private enum Step {
        // Combat Steps
        FightDragon,
        WaitForDrop,
        SpecAttack,
        Loot,
        EatFood,
        DrinkPrayerPotion,
        DrinkRangingPotion,
        MoveAwayFromTarget,
        TeleportToBank,

        // Banking Steps
        ToggleQuickPrayerOff,
        RunToBank,
        BankSupplies,
        TeleportToKourend,

        // Prayer Steps
        RunToAltar,
        OpenChurchDoor,
        RestorePrayerPoints,
        RunToPit,

        // Reset
        ClimbDownHole,

        // Misc
        LevelUp,
        Wait,
        EquipBolts,
        Stop
    }

    @Override
    public void start() {
        // Initial Setup
        status = "Setup";
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events
        combatMinHealthPercent = 40;
        currentPhase = Phase.Start;
        secondaryBoltId = 0;
        primaryBoltId = Items.AMETHYST_BROAD_BOLTS_21316; //Ruby E
        boltId = Items.AMETHYST_BROAD_BOLTS_21316; //Ruby E
        currentTarget = ctx.npcs.nil();
        droppables = new int[] {boltId, Items.COINS_995, Items.JUG_1935, Items.JUG_OF_WINE_1993, Items.ANGLERFISH_13441};
        lastGuiUpdateTimestamp = 0L;
        tripCount = 0;
        invCount = ctx.inventory.select().count();

        setNextSpec();

        // Potions
        combatPotionIds = new int[] {Items.RANGING_POTION1_173, Items.RANGING_POTION2_171, Items.RANGING_POTION3_169, Items.RANGING_POTION4_2444};
        prayerPotionIds = new int[] {Items.PRAYER_POTION4_2434, Items.PRAYER_POTION3_139, Items.PRAYER_POTION2_141, Items.PRAYER_POTION1_143};
        antifirePotionIds = new int[] {Items.EXTENDED_ANTIFIRE4_11951, Items.EXTENDED_ANTIFIRE3_11953, Items.EXTENDED_ANTIFIRE2_11955, Items.EXTENDED_ANTIFIRE1_11957};
        combatPotion = new UsePotion(ctx, "", combatPotionIds, Constants.SKILLS_RANGE, ctx.skills.realLevel(Constants.SKILLS_RANGE) + 4, ctx.skills.realLevel(Constants.SKILLS_RANGE) + 6, true);
        prayerPotion = new UsePotion(ctx, "", prayerPotionIds, Constants.SKILLS_PRAYER, 15, 20, true);
        antifirePotion = new UsePotion(ctx, "", antifirePotionIds, 0, 0, 0, true);

        // -- Combat Phase --
        // Heal Config
        HealConfig healConfig = new HealConfig(CommonFunctions.allFoodIds(), 80);
        eatFood = new HealAction(ctx, "Healing", healConfig);

        // Loot Task
        buildLootList();
        lootAction = new BbdLoot(ctx, "", lootList);

        // Combat Config
        combatConfig = new CombatConfig("Brutal black dragon", -1, combatMinHealthPercent, lootList, ctx.combat.inMultiCombat(), null, 5);
        combatAction = new CombatAction(ctx, "Combat", combatConfig);

        // Move Away
        moveAwayFromTarget = new MoveAwayFromTarget(ctx, 5);

        // Wait for loot
        waitForCombatLoot = new WaitForCombatLoot(ctx);

        // Fally Teleport
        faladorTeleport = new Teletab(ctx, Items.FALADOR_TELEPORT_8009, false);

        // -- Bank Phase --
        pathToBank = new Tile[] {new Tile(2961, 3381, 0), new Tile(2957, 3381, 0), new Tile(2953, 3378, 0), new Tile(2949, 3376, 0), new Tile(2946, 3373, 0), new Tile(2946, 3369, 0)};
        bankAction = new BbdBankAction(ctx);

        // -- Prayer Phase --
        prayerAltarId = 28566;
        closedMonestaryDoorId = 28479;
        pathToMonastary = new Tile[] {new Tile(1507, 3812, 0), new Tile(1511, 3812, 0), new Tile(1515, 3812, 0), new Tile(1519, 3812, 0), new Tile(1523, 3812, 0), new Tile(1527, 3812, 0), new Tile(1531, 3812, 0), new Tile(1535, 3810, 0)};
        pathToPit = new Tile[] {new Tile(1505, 3816, 0), new Tile(1509, 3815, 0), new Tile(1513, 3814, 0), new Tile(1517, 3812, 0), new Tile(1521, 3812, 0), new Tile(1525, 3812, 0), new Tile(1529, 3812, 0), new Tile(1533, 3812, 0), new Tile(1533, 3808, 0), new Tile(1535, 3804, 0), new Tile(1539, 3803, 0), new Tile(1543, 3803, 0), new Tile(1547, 3803, 0), new Tile(1551, 3803, 0), new Tile(1555, 3801, 0), new Tile(1559, 3799, 0), new Tile(1560, 3795, 0), new Tile(1563, 3792, 0)};
        pitId = 28915;
        monestaryTile = new Tile(1540, 3808);

        // -- MISC --
        // Level Up

        status = "Started";

        // Check Phase
        checkPhase();

        updateGUIData();
    }

    @Override
    public void poll() {
/*        if( !currentTarget.valid() ) {
            currentTarget = ;
        }*/
        Step currentState = null;

        if( !currentTarget.valid() ) {
            currentTarget = ctx.players.local().interacting().valid() ?
                    (Npc) ctx.players.local().interacting() :
                    ctx.npcs.select(new Filter<Npc>() {
                        @Override
                        public boolean accept(Npc npc) {
                            return npc.interacting().name().equals(ctx.players.local().name());
                        }
                    }).poll();
        } else {
            // Check for finishing blow and move
            if( currentTarget.healthPercent() == 0 && !ctx.players.local().inMotion() ) {
                CommonFunctions.walkToSafespot(ctx, new Tile(currentTarget.tile().x() + Random.nextInt(0, -4), currentTarget.tile().y() + Random.nextInt(0, -4)));
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.players.local().inMotion();
                    }
                }, 200, 20);

                // force next iteration before taking action. Allows loot to trigger next and pick up loot under player.
                currentState = Step.Wait;
            }
        }

        // Set state if not already set
        if( currentState == null ) {
            currentState = checkState();
        }

        // Reset any errant inventory selection
        if( ctx.inventory.selectedItem().valid() ) {
            ctx.inventory.selectedItem().click(); // unselect itself
        }

        switch( currentState ) {
            case FightDragon:
                combatAction.execute();
                break;

            case WaitForDrop:
                waitForCombatLoot.execute();
                break;
            case Loot:
                if( ctx.inventory.select().id(boltId).poll().valid() && ctx.inventory.select().id(boltId).poll().stackSize() > 4 ) {
                    ctx.inventory.select().id(boltId).poll().interact("Wield");
                }
                if( !ctx.players.local().inMotion() ) {
                    lootAction.execute();

                    // We looted, so update inventory count
                    invCount = ctx.inventory.select().count();
                }
                break;
            case MoveAwayFromTarget:
                moveAwayFromTarget.execute();
                break;
            case DrinkPrayerPotion:
                prayerPotion.execute();
                if( !ctx.prayer.quickPrayer() ) {
                    ctx.prayer.quickPrayer(true);
                }
                break;
            case DrinkRangingPotion:
                combatPotion.execute();
                break;
            case TeleportToBank:
                faladorTeleport.execute();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().tile().x() > 2000 && ctx.players.local().tile().x() < 3000;
                    }
                }, 100, 12);
                if( ctx.inventory.select().id(Items.FALADOR_TELEPORT_8009).count() == 0 ) {
                    ctx.controller.stop();
                }
                // Continue on to toggle the quick prayers off.
            case ToggleQuickPrayerOff:
                ctx.prayer.quickPrayer(false);
                break;
            case RunToBank:
                runToBank();
                break;
            case BankSupplies:
                bankAction.execute();
                break;
            case EatFood:
                eatFood.execute();
                break;
            case TeleportToKourend:
                xericTeleport();
                break;
            case RunToAltar:
                runToAltar();
                break;
            case OpenChurchDoor:
                openMonestaryDoor();
                break;
            case RestorePrayerPoints:
                restorePrayerPoints();
                break;
            case RunToPit:
                runToPit();
                break;
            case ClimbDownHole:
                resetPotsAndClimb();
                break;
            default:
                status = "Waiting";
        }

        // Drop inventory
        if( currentState != Step.Loot ) {
            dropInventory();
        }


        // Update readable timestamp
        updateGUIData();
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        String msg = messageEvent.text();

        if( msg.contains("dead") ) {
            currentPhase = Phase.Reset;
            ctx.controller.stop();
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.drawString("Trips: " + tripCount, GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(6));
        g.drawString("Runtime: " + readableTimestamp, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));

        // Update gui update timestamp
           /* //  Draw Background
            g.setColor(GuiHelper.getBaseColor());
            g.fillRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
            g.setColor(GuiHelper.getTextColorWhite());
            g.drawRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));

            //   Draw Data
            g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(1));
            g.drawString("Trip: " + tripCount, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(3));

            g.setColor(GuiHelper.getTextColorImportant());
            g.drawString("Range : " + ctx.skills.level(Constants.SKILLS_RANGE), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(5));
            g.drawString("Antifire Active: " + !antifireExpired, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));

            g.setColor(GuiHelper.getTextColorInformation());
            g.drawString("Target HP: " + (currentTarget.valid() ? currentTarget.healthPercent() : 0) + "%", GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(1));
*/
    }

    // Loot
    private void buildLootList() {
        lootList = new LootList();

        // Uniques
        lootList.addLootItem(new LootItem(Items.DRAGON_PLATELEGS_4087));
        lootList.addLootItem(new LootItem(Items.DRAGON_PLATESKIRT_4585));
        lootList.addLootItem(new LootItem(Items.DRAGON_SPEAR_1249));
        lootList.addLootItem(new LootItem(Items.UNCUT_DRAGONSTONE_1631));
        lootList.addLootItem(new LootItem(Items.ENSOULED_DRAGON_HEAD_13510));
        lootList.addLootItem(new LootItem(Items.DRACONIC_VISAGE_11286));

        // Weps
        lootList.addLootItem(new LootItem(Items.RUNE_SPEAR_1247));
        lootList.addLootItem(new LootItem(Items.RUNE_LONGSWORD_1303));
        lootList.addLootItem(new LootItem(Items.DRAGON_LONGSWORD_1305));
        lootList.addLootItem(new LootItem(Items.DRAGON_DAGGER_1215));

        // Armor
        lootList.addLootItem(new LootItem(Items.RUNE_PLATELEGS_1079));
        lootList.addLootItem(new LootItem(Items.RUNE_FULL_HELM_1163));
        lootList.addLootItem(new LootItem(Items.RUNE_PLATEBODY_1127));
        lootList.addLootItem(new LootItem(Items.BLACK_DHIDE_VAMB_2491));
        lootList.addLootItem(new LootItem(Items.BLACK_DHIDE_BODY_2503));
        lootList.addLootItem(new LootItem(Items.DRAGON_MED_HELM_1149));

        // Runes & Ammo
        lootList.addLootItem(new LootItem(Items.RUNE_JAVELIN_830));
        lootList.addLootItem(new LootItem(Items.BLOOD_RUNE_565));
        lootList.addLootItem(new LootItem(Items.LAW_RUNE_563));
        lootList.addLootItem(new LootItem(Items.SOUL_RUNE_566));
        lootList.addLootItem(new LootItem(Items.DEATH_RUNE_560));
        lootList.addLootItem(new LootItem(Items.RUNE_ARROW_892));
        lootList.addLootItem(new LootItem(Items.RUNE_DART_811));
        lootList.addLootItem(new LootItem(Items.RUNE_KNIFE_868));
        lootList.addLootItem(new LootItem(Items.RUNE_THROWNAXE_805));

        // Consumables
        lootList.addLootItem(new LootItem(Items.LAVA_SCALE_NOTED_11993));
        lootList.addLootItem(new LootItem(Items.DRAGON_DART_TIP_11232));
        lootList.addLootItem(new LootItem(Items.DRAGON_JAVELIN_HEADS_19582));
        lootList.addLootItem(new LootItem(Items.DRAGON_ARROWTIPS_11237));
        lootList.addLootItem(new LootItem(Items.RUNITE_ORE_NOTED_452));

        // Rare & Jem Drop Table
        lootList.addLootItem(new LootItem(Items.LOOP_HALF_OF_KEY_987));
        lootList.addLootItem(new LootItem(Items.RUNITE_BAR_2363));
        lootList.addLootItem(new LootItem(Items.NATURE_RUNE_561));
        lootList.addLootItem(new LootItem(Items.RUNE_2H_SWORD_1319));
        lootList.addLootItem(new LootItem(Items.RUNE_BATTLEAXE_1373));
        lootList.addLootItem(new LootItem(Items.RUNE_SQ_SHIELD_1185));
        lootList.addLootItem(new LootItem(Items.DRAGONSTONE_1615));
        lootList.addLootItem(new LootItem(Items.RUNE_KITESHIELD_1201));
        lootList.addLootItem(new LootItem(Items.SHIELD_LEFT_HALF_2366));
        lootList.addLootItem(new LootItem(Items.UNCUT_DIAMOND_1617));

        // Always
        lootList.addLootItem(new LootItem(Items.DRAGON_BONES_536));
        lootList.addLootItem(new LootItem(Items.BLACK_DRAGONHIDE_1747));

    }

    // State
    private Step checkState() {
        // Confirm State
        currentPhase = checkNextPhase();

        /* -- COMBAT -- */
        if( currentPhase == Phase.Combat ) {


            // Teleport To Bank
            if( ctx.players.local().healthPercent() <= 40 || (ctx.prayer.prayerPoints() == 0 && ctx.players.local().healthPercent() < 50) || isInvFullOfLoot() ) {
                status = "Bank Teleport";
                return Step.TeleportToBank;
            }

            // Loot
            if( lootAction.activate() ) {
                status = "Loot";
                return Step.Loot;
            }

            // Move Away
            if( moveAwayFromTarget.activate() ) {
                status = "Move";
                return Step.MoveAwayFromTarget;
            }

            // Emergency Eat to prolong trip.
            if( eatFood.activate() ) {
                return Step.EatFood;
            }

            // Attack
            if( combatAction.activate() ) {
                return Step.FightDragon;
            }

            // Sip Prayer Pot
            if( prayerPotion.activate() && ctx.inventory.select().id(prayerPotionIds).count() > 0 ) {
                status = "Pray Potion";
                return Step.DrinkPrayerPotion;
            }

            // Sip Range Pot
            if( combatPotion.activate() && ctx.inventory.select().id(combatPotionIds).count() > 0 ) {
                status = "Range Potion";
                return Step.DrinkRangingPotion;
            }
        }

        /* -- PRAYER -- */
        if( currentPhase == Phase.Prayer ) {

            if( isPitNearby() ) {
                status = "Reset";
                return Step.ClimbDownHole;
            }

            // Open Door From Inside
            if( isPrayerFull() && monestaryTile.matrix(ctx).reachable() && !monestaryDoorArea.getCentralTile().matrix(ctx).reachable() ) {
                status = "Open Door";
                return Step.OpenChurchDoor;
            }

            // Open Door from outside
            if( monestaryDoorArea.contains(ctx.players.local()) && !monestaryTile.matrix(ctx).reachable() && !isPrayerFull() ) {
                status = "Open Door";
                return Step.OpenChurchDoor;
            }

            // Restore Prayer at Altar
            if( ctx.objects.select().id(prayerAltarId).poll().valid() && monestaryTile.matrix(ctx).reachable() && !isPrayerFull() ) {
                status = "Praying";
                return Step.RestorePrayerPoints;
            }

            // Run to Monestary
            if( !monestaryDoorArea.contains(ctx.players.local()) && !isPrayerFull() ) {
                status = "To Altar";
                return Step.RunToAltar;
            }

            // Run to pit
            if( isPrayerFull() && !isPitNearby() ) {
                status = "To Pit";
                return Step.RunToPit;
            }

        }

        /* -- BANKING -- */
        if( currentPhase == Phase.Banking ) {
            // Turn Quick Prayer Off
/*
            if( ctx.prayer.quickPrayer() ) {
                return Step.ToggleQuickPrayerOff;
            }
*/

            // Run To Bank
            if( !ctx.bank.inViewport() && (ctx.inventory.select().id(lootList.allItemIds()).count() > 0 || ctx.inventory.select().id(Items.EXTENDED_ANTIFIRE1_11957).count() == 0) ) {
                status = "To Bank";
                return Step.RunToBank;
            }

            // Bank Supplies
            if( ctx.bank.inViewport() && !isInventoryReset() ) {
                status = "Banking";
                return Step.BankSupplies;
            }

            // Teleport to Kourend
            if( ctx.bank.inViewport() && isInventoryReset() ) {
                status = "Xeric Teleport";
                return Step.TeleportToKourend;
            }
        }

        // Check Phase
        return Step.Wait;
    }

    // Run To Bank
    private void runToBank() {
        if( !ctx.bank.inViewport() ) {
            ctx.movement.newTilePath(pathToBank).traverse();
            sleep();
        }
    }

    private void runToAltar() {
        pathToMonastary[pathToMonastary.length - 1] = monestaryDoorArea.getRandomTile();
        ctx.movement.newTilePath(pathToMonastary).traverse();
    }

    private void runToPit() {
        pathToPit[pathToPit.length - 1] = pitArea.getRandomTile();
        if( eatFood.activate() ) {
            sleep(500);
            eatFood.execute();
        }
        ctx.movement.newTilePath(pathToPit).traverse();
    }

    private void openMonestaryDoor() {
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !ctx.players.local().inMotion();
            }
        }, 250, 20);

        if( ctx.objects.select().id(closedMonestaryDoorId).nearest().poll().inViewport() ) {
            // Set camera angle
            if( ctx.camera.pitch() > 25 ) {
                ctx.camera.angle(Random.nextInt(225, 273));
                ctx.camera.pitch(Random.nextInt(0, 40));
            }

            // Wait till player stops moving to continue
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion();
                }
            }, 150, 20);

            if( !ctx.players.local().inMotion() ) {
                GameObject door = ctx.objects.select().id(closedMonestaryDoorId).nearest().poll();

                Point doorPoint = new Point(door.centerPoint().x + Random.nextInt(-30, 0), door.centerPoint().y + Random.nextInt(-50, 0));
                ctx.input.move(doorPoint);
                sleep(Random.nextInt(200, 400));
                String action = ctx.menu.items()[0].split(" ")[0];

                if( action.equals("Open") ) {
                    ctx.input.click(doorPoint, true);
                }

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !door.valid();
                    }
                }, 100, 30);
            }
        }
    }

    private void xericTeleport() {
        if( ctx.game.tab() != Game.Tab.INVENTORY ) {
            ctx.game.tab(Game.Tab.INVENTORY);
            sleep();
        }
        ctx.inventory.select().id(Items.XERICS_TALISMAN_13393).poll().interact("Rub");
        sleep();

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.widgets.component(187, 3, 2).visible();
            }
        }, 100, 10);

        if( ctx.widgets.component(187, 3, 2).visible() ) {
            ctx.input.send("3");

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().tile().x() < 2600;
                }
            }, 250, 20);

            if( ctx.players.local().tile().x() < 2600 ) {
                tripCount++;
            }
        }
    }

    private void restorePrayerPoints() {
        GameObject altar = ctx.objects.select().id(prayerAltarId).poll();

        if( !altar.inViewport() ) {
            ctx.movement.step(altar);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return altar.inViewport();
                }
            }, 100, 20);

            if( !altar.inViewport() ) {
                ctx.camera.turnTo(altar);
                sleep();
            }
        } else {
            altar.interact("Pray");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.prayer.prayerPoints() == ctx.skills.realLevel(Constants.SKILLS_PRAYER);
                }
            }, 100, 40);
        }
    }

    private void resetPotsAndClimb() {
        invCount = ctx.inventory.select().count();
        // Pot Range
        if( combatPotion.activate() ) {
            combatPotion.execute();
        }

        // Antifire pot
        antifirePotion.execute();

        // Enable Quick Prayers
        ctx.prayer.quickPrayer(true);

        // Climb Hole
        ctx.camera.pitch(Random.nextInt(45, 70));
        ctx.objects.select().select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject gameObject) {
                return gameObject.name().equals("Hole");
            }
        }).poll().interact("Enter");

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return isDragonNearby();
            }
        }, 125, 10);
    }

    private boolean isPrayerFull() {
        return ctx.skills.realLevel(Constants.SKILLS_PRAYER) - ctx.skills.level(Constants.SKILLS_PRAYER) < 5;
    }

    private boolean isDragonNearby() {
        return ctx.npcs.select().id(7275).nearest().poll().valid();
    }

    private boolean isPitNearby() {
        return pitArea.contains(ctx.players.local());
    }

    private void setNextSpec() {
        nextSpecPercent = Random.nextInt(31, 51);
    }

    private boolean isInventoryReset() {
        boolean hasLoot = ctx.inventory.select().id(lootList.allItemIds()).count() > 0;
        boolean hasAntifire = ctx.inventory.select().id(Items.EXTENDED_ANTIFIRE1_11957).count() == 1;
        boolean hasPrayer = ctx.inventory.select().id(prayerPotionIds).count() > 0;
        boolean hasCombatPot = ctx.inventory.select().id(combatPotionIds).count() > 0;
        boolean hasTalisman = ctx.inventory.select().id(Items.XERICS_TALISMAN_13393).count() == 1;
        boolean hasTeleports = ctx.inventory.select().id(Items.FALADOR_TELEPORT_8009).count() == 1;

        return !hasLoot && hasAntifire && hasPrayer && hasCombatPot && hasTalisman && hasTeleports;
    }

    private boolean isInvFullOfLoot() {
        return (ctx.inventory.isFull() && ctx.inventory.select().id(droppables).count() == 0);
    }

    private void checkPhase() {
        // Check Phase
        if( isDragonNearby() ) {
            currentPhase = Phase.Combat;
        } else if( ctx.players.local().tile().x() > 2000 ) {
            currentPhase = Phase.Banking;
        } else {
            currentPhase = Phase.Prayer;
        }
    }

    private Phase checkNextPhase() {

        switch( currentPhase ) {
            case Combat:
                // Bank Check
                if( ctx.players.local().tile().x() > 2000 ) {
                    status = "Waiting";
                    return Phase.Banking;
                }

                // Prayer Check
                if( isPitNearby() ) {
                    status = "Waiting";
                    currentPhase = Phase.Prayer;
                }
                break;
            case Banking:
                // Prayer Check
                if( ctx.players.local().tile().x() < 2000 ) {
                    status = "Waiting";
                    return Phase.Prayer;
                }
                break;
            case Prayer:
                if( isDragonNearby() ) {
                    status = "Waiting";
                    return Phase.Combat;
                }
                break;
        }

        return currentPhase;
    }

    private void dropInventory() {
        // Drop Inventory
        if( currentPhase == Phase.Combat ) {

            // Drop inventory not on the loot list
            Item i = ctx.inventory.select().id(droppables).poll();

            switch( i.id() ) {
                case Items.JUG_OF_WINE_1993:
                    if( invCount >= 24 ) {
                        if( ctx.combat.healthPercent() < 100 ) {
                            i.interact("Drink", i.name());
                        } else {
                            CommonFunctions.dropItem(ctx, i.id());
                        }
                    }
                    break;
                case Items.ANGLERFISH_13441:
                    if( ctx.combat.healthPercent() < 100 ) {
                        i.interact("Eat", i.name());
                    } else {
                        CommonFunctions.dropItem(ctx, i.id());
                    }
                    break;
                case Items.AMETHYST_BROAD_BOLTS_21316:
                    if( invCount >= 24 ) {
                        i.interact("Wield", i.name());
                    }
                    break;
                default:
                    // Check if standing on loot pile
                    Tile playerTile = ctx.players.local().tile();
                    boolean onLoot = ctx.groundItems.select().id(lootList.allItemIds()).select(new Filter<GroundItem>() {
                        @Override
                        public boolean accept(GroundItem groundItem) {
                            return groundItem.tile().distanceTo(playerTile) == 0;
                        }
                    }).poll().valid();

                    if( onLoot ) {
                        // Move off
                        CommonFunctions.walkToSafespot(ctx, getClosestReachableTile());

                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return !ctx.players.local().inMotion();
                            }
                        }, 100, 10);
                    }

                    // Drop Item
                    CommonFunctions.dropItem(ctx, i.id());


            }

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !i.valid();
                }
            }, 200, 10);

        }
    }

    private Tile getClosestReachableTile() {
        Tile playerTile = ctx.players.local().tile();
        Area playerArea = new Area(new Tile(playerTile.x() - 1, playerTile.y() - 1), new Tile(playerTile.x() + 1, playerTile.y() + 1));

        for( Tile t : playerArea.tiles() ) {
            if( t.distanceTo(playerTile) != 0 && t.matrix(ctx).reachable() ) {
                return t;
            }
        }
        return playerTile;
    }


    private void updateGUIData() {
        if( getRuntime() - lastGuiUpdateTimestamp > 10000 ) {
            lastGuiUpdateTimestamp = getRuntime();
            readableTimestamp = GuiHelper.getReadableRuntime(lastGuiUpdateTimestamp);
        }
    }


}


