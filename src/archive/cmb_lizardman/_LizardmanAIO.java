package ngc.cmb_lizardman;

import ngc._resources.constants.Items;
import ngc._resources.actions.*;
import ngc._resources.actions._config.HealConfig;
import ngc._resources.tools.CommonActions;
import ngc._resources.tools.GuiHelper;
import ngc._resources.models.LootItem;
import ngc._resources.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;

@Script.Manifest(name = "Lizardman AIO", description = "Kills Lizardman in canyon", properties = "client=4; topic=051515; author=Bowman")
public class _LizardmanAIO extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private String status;
    private boolean lootDuringCombat;
    private int minHealthPercent;
    private int[] combatPotionIds;
    private boolean reloadCannonFlag;
    private boolean takeAntidoteFlag;

    // Loot
    private LootList lootList;

    // Tasks
    private HealAction healAction;
    private LootAction lootAction;
    private ToggleLevelUp toggleLevelUp;
    private UsePotion combatPotion;
    private UsePotion antiPoisonPotion;

    @Override
    public void start() {
        // Config
        startConfigs();

        // Heal Config
        HealConfig healConfig = new HealConfig(CommonActions.allFoodIds(), minHealthPercent);
        healAction = new HealAction(ctx, "Healing", healConfig);

        // Slayer Config
        lizardmanLoot();

        // Loot Task
        lootAction = new LootAction(ctx, "Loot", lootList, -1, lootDuringCombat, true);

        // Level Up
        toggleLevelUp = new ToggleLevelUp(ctx);

        // Using melee with defender
        combatPotion = new UsePotion(ctx, "Potion", combatPotionIds, Constants.SKILLS_STRENGTH, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 2, ctx.skills.realLevel(Constants.SKILLS_STRENGTH) + 4, true);
        antiPoisonPotion = new UsePotion(ctx, "Poison", new int[] {Items.ANTIDOTE__1_5958, Items.ANTIDOTE__2_5956, Items.ANTIDOTE__3_5954, Items.ANTIDOTE__4_5952}, 0, 0, 0, true);
        status = "Started";

    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        String msg = messageEvent.text();

        if(msg.contains("poison resistance has worn off")){
            takeAntidoteFlag = true;
        }

        if(msg.contains("cannon is out of ammo")){
            reloadCannonFlag = true;
        }
    }

    @Override
    public void poll() {
        switch( checkState() ) {
            case ReloadCannon:
                CommonActions.reloadCannon(ctx);
                reloadCannonFlag = false;
                break;
            case Poison:
                antiPoisonPotion.execute();
                takeAntidoteFlag = false;
                break;
            case Loot:
                lootAction.execute();
                break;
            case Healing:
                healAction.execute();
                break;
            case LevelUp:
                toggleLevelUp.execute();
                break;
            case Potion:
                combatPotion.execute();
                break;
            case Stop:
                ctx.game.logout();
                ctx.controller.stop();

            default: // waiting
                status = "Waiting";
        }
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
        reloadCannonFlag = false;
        takeAntidoteFlag = false;

        status = "";
        lootDuringCombat = true;
        minHealthPercent = 50;
        combatPotionIds = new int[] {Items.SUPER_COMBAT_POTION1_12701, Items.SUPER_COMBAT_POTION2_12699, Items.SUPER_COMBAT_POTION3_12697, Items.SUPER_COMBAT_POTION4_12695};
    }

    // Loot Configs
    private void lizardmanLoot() {

        // Unique Drop Table Alchables
        lootList.addLootItem(new LootItem(Items.LIZARDMAN_FANG_13391));
        lootList.addLootItem(new LootItem(Items.XERICS_TALISMAN_INERT_13392));
        lootList.addLootItem(new LootItem(Items.RANARR_SEED_5295));
        lootList.addLootItem(new LootItem(Items.SNAPDRAGON_SEED_5300));
        lootList.addLootItem(new LootItem(Items.TORSTOL_SEED_5304));
    }

    private enum State {
        Poison, ReloadCannon, Loot, Healing, Wait, Stop, LevelUp, Potion
    }

    private State checkState() {

        // Heal poison
        if(takeAntidoteFlag){
            status = "Poison";
            return State.Poison;
        }

        // Reload Cannon

        if(reloadCannonFlag){
            status = "Reload";
            return State.ReloadCannon;
        }

        if( healAction.activate() ) {
            status = "Heal";
            return State.Healing;
        }

        if( lootAction.activate() ) {
            status = "Loot";
            return State.Loot;
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


