package ngc.cmb_cows;

import ngc._resources.Items;
import ngc._resources.Npcs;
import ngc._resources.actions.CombatAction;
import ngc._resources.actions.HealAction;
import ngc._resources.actions.TeleTab;
import ngc._resources.actions.WaitForCombatLoot;
import ngc._resources.actions._config.CombatConfig;
import ngc._resources.actions._config.HealConfig;
import ngc._resources.functions.CommonFunctions;
import ngc._resources.functions.GuiHelper;
import ngc._resources.models.LootItem;
import ngc._resources.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Npc;

import java.awt.*;

@Script.Manifest(name = "CowKiller", description = "Kills Cows. Nuff Said.", properties = "client=4; topic=051515; author=Bowman")
public class CowKiller extends PollingScript<ClientContext> implements MessageListener, PaintListener {
    // App Tracking
    private String status;
    private Phase currentPhase;
    private int combatMinHealthPercent;
    private Npc currentTarget;
    private int arrowId;
    private Long lastGuiUpdateTimestamp;
    private String readableTimestamp;
    private int invCount;
    private int[] droppables; //Droppable items that aren't pots, bolts, etc


    // Loot
    private LootList lootList;

    // Tasks
    private CombatConfig combatConfig;
    private HealAction eatFood;
    private CombatAction combatAction;
    private CowKiller_Loot lootAction;
    private WaitForCombatLoot waitForCombatLoot;

    // Phases
    private enum Phase {
        Start, Combat, Misc, Reset
    }

    private enum Step {
        // Combat Steps
        FightCow,
        WaitForDrop,
        Loot,
        EatFood,

        // Misc
        LevelUp,
        Wait,
        EquipArrows,
        Stop,

        Reset
    }

    @Override
    public void start(){
        // Initial Setup
        status = "Setup";
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events
        combatMinHealthPercent = 20;
        currentPhase = Phase.Start;
        arrowId = Items.IRON_ARROW_884;
        currentTarget = ctx.npcs.nil();
        droppables = new int[] {arrowId, Items.COWHIDE_1739, Items.RAW_BEEF_2132, Items.BONES_526};
        lastGuiUpdateTimestamp = 0L;
        invCount = ctx.inventory.select().count();


        // -- Combat Phase --
        // Heal Config
        HealConfig healConfig = new HealConfig(CommonFunctions.allFoodIds(), 40);
        eatFood = new HealAction(ctx, "Healing", healConfig);

        // Loot Task
        buildLootList();
        lootAction = new CowKiller_Loot(ctx, "", lootList);

        // Combat Config
        combatConfig = new CombatConfig("Cow", -1, combatMinHealthPercent, lootList, ctx.combat.inMultiCombat(), null, 1);
        combatAction = new CombatAction(ctx, "Combat", combatConfig);

        // Wait for loot
        waitForCombatLoot = new WaitForCombatLoot(ctx);

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
        }

        // Set state if not already set
        currentState = checkState();


        // Reset any errant inventory selection
        if( ctx.inventory.selectedItem().valid() ) {
            ctx.inventory.selectedItem().click(); // unselect itself
        }

        switch( currentState ) {
                        case WaitForDrop:
                waitForCombatLoot.execute();
                break;

            case Loot:
                if( ctx.inventory.select().id(arrowId).poll().valid() && ctx.inventory.select().id(arrowId).poll().stackSize() > 50 ) {
                    ctx.inventory.select().id(arrowId).poll().interact("Wield");
                }
                if( !ctx.players.local().inMotion() ) {
                    lootAction.execute();

                    // We looted, so update inventory count
                    invCount = ctx.inventory.select().count();
                }
                break;
            case EatFood:
                eatFood.execute();
                break;

            case FightCow:
                combatAction.execute();
                break;
            default:
                status = "Waiting";
        }

        // Update readable timestamp
        updateGUIData();
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        String msg = messageEvent.text();

        if( msg.contains("you are dead") ) {
            currentPhase = Phase.Reset;
            ctx.controller.stop();
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.drawString("Runtime: " + readableTimestamp, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));

        g.setColor(GuiHelper.getTextColorImportant());
        g.drawString("Range : " + ctx.skills.level(Constants.SKILLS_ATTACK), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(5));

         //  Draw Background
           /* g.setColor(GuiHelper.getBaseColor());
            g.fillRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
            g.setColor(GuiHelper.getTextColorWhite());
            g.drawRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));

            //   Draw Data
            g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(1));

            g.setColor(GuiHelper.getTextColorImportant());
            g.drawString("Range : " + ctx.skills.level(Constants.SKILLS_RANGE), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(5));

            g.setColor(GuiHelper.getTextColorInformation());

            if(currentTarget != null)
            g.drawString("Target HP: " + (currentTarget.valid() ? currentTarget.healthPercent() : 0) + "%", GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(1));*/

    }

    // Loot
    private void buildLootList() {
        lootList = new LootList();

        // Runes & Ammo
        lootList.addLootItem(new LootItem(Items.IRON_ARROW_884, 5));
    }

    // State
    private Step checkState() {
        // Confirm State
        currentPhase = checkNextPhase();

        /* -- COMBAT -- */
        if( currentPhase == Phase.Combat ) {

            // Loot
            if( lootAction.activate() ) {
                status = "Loot";
                return Step.Loot;
            }

            // Emergency Eat to prolong trip.
            if( eatFood.activate() ) {
                return Step.EatFood;
            }

            // Attack
            if( combatAction.activate() ) {
                return Step.FightCow;
            }
        }

        // Check Phase
        return Step.Wait;
    }

    private boolean isNpcNearby() {
        return ctx.npcs.select().name("Cow").nearest().poll().valid();
    }


    private void checkPhase() {
        // Check Phase
        if( isNpcNearby() ) {
            currentPhase = Phase.Combat;
        }
    }

    private Phase checkNextPhase() {
        return currentPhase;
    }

    private void updateGUIData() {
        if( getRuntime() - lastGuiUpdateTimestamp > 10000 ) {
            lastGuiUpdateTimestamp = getRuntime();
            readableTimestamp = GuiHelper.getReadableRuntime(lastGuiUpdateTimestamp);
        }
    }




}