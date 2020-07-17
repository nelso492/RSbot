package ngc.cmb_cows;

import ngc._resources.actions.CombatAction;
import ngc._resources.actions.EquipArrows;
import ngc._resources.actions.HealAction;
import ngc._resources.actions.LootAction;
import ngc._resources.actions._config.CombatConfig;
import ngc._resources.actions._config.HealConfig;
import ngc._resources.actions._config.ScriptConfig;
import ngc._resources.functions.AntibanActions;
import ngc._resources.functions.CommonFunctions;
import ngc._resources.functions.GaussianTools;
import ngc._resources.functions.GuiHelper;
import ngc._resources.models.LootItem;
import ngc._resources.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;

@Script.Manifest(name = "CowKillerAIO", description = "Kills Cows.", properties = "client=4; topic=051515; author=Bowman")
public class CowKillerAIO extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Config
    private ScriptConfig scriptConfig = new ScriptConfig(ctx, null);
    private LootList lootList;
    private boolean antiBanInProgress;

    //region Antiban
    private long lastBreakTimestamp;
    private int nextBreakInMinutes;
    //endregion

    //region Phases & Steps

    // PHASE

    private CowKillerAIO_Combat combatPhase = new CowKillerAIO_Combat(ctx);
    ;
    //endregion

    //region Actions
    private CombatAction combatCowAction;
    private CombatAction combatCalfAction;
    private HealAction healAction;
    private EquipArrows equipArrows;
    private LootAction lootAction;
    //endregion

    //region start
    @Override
    public void start() {
        // Tracked Skills
        int[] skills = new int[1];
        skills[0] = CommonFunctions.promptForCombatStyle(ctx);

        this.scriptConfig = new ScriptConfig(ctx, skills);
        this.scriptConfig.setStatus("Config");

        // Antiban
        lastBreakTimestamp = 0L;
        nextBreakInMinutes = Random.nextInt(1, 3);

        // Init Config
        scriptConfig = new ScriptConfig(ctx, skills);

        // Loot
        int projectileId = 0;
        if (skills[0] == Constants.SKILLS_RANGE) {
            projectileId = CommonFunctions.promptForArrowType();
            this.lootList = new LootList();
            this.lootList.addLootItem(new LootItem(projectileId, 3));
            this.lootAction = new LootAction(ctx, "Loot", lootList, 10, false, true);
        }

        // Action Setup
        CombatConfig combatCowConfig = new CombatConfig("Cow", -1, 20, lootList, ctx.combat.inMultiCombat(), null, 0);
        this.combatCowAction = new CombatAction(ctx, "Attack Cows", combatCowConfig);

        CombatConfig combatCalfConfig = new CombatConfig("Cow calf", -1, 20, lootList, ctx.combat.inMultiCombat(), null);
        this.combatCalfAction = new CombatAction(ctx, "Attack Calf", combatCalfConfig);

        HealConfig healConfig = new HealConfig(CommonFunctions.allFoodIds(), 50);
        this.healAction = new HealAction(ctx, "Healing", healConfig);

        this.equipArrows = new EquipArrows(ctx, projectileId, 60);

        this.combatPhase.setName("Combat");
        this.combatPhase.addAction(this.healAction);
        this.combatPhase.addAction(this.lootAction);
        this.combatPhase.addAction(this.equipArrows);
//        this.combatPhase.addAction(this.combatCalfAction);
        this.combatPhase.addAction(this.combatCowAction);

        this.scriptConfig.setStatus("Start");
    }
    //endregion

    //region poll
    @Override
    public void poll() {
        // Pre State Check Action
        this.scriptConfig.prePollAction();

        // Antiban Check
        if (getRuntime() - lastBreakTimestamp > (1000 * 60 * nextBreakInMinutes)) {
            this.antiBanInProgress = false;
            if (GaussianTools.takeActionNever()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;

                switch (Random.nextInt(0, 2)) {
                    case 0:
                        AntibanActions.hoverRandomNPC(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        AntibanActions.hoverRandomObject(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        AntibanActions.toggleXPDrops(ctx);
                        this.antiBanInProgress = false;
                        break;
                }
            }
            if (!this.antiBanInProgress && GaussianTools.takeActionRarely()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;

                switch (Random.nextInt(0, 3)) {
                    case 0:
                        AntibanActions.moveMouseOffScreen(ctx, true);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        AntibanActions.checkStat(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        AntibanActions.jiggleMouse(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 3:
                        AntibanActions.doNothing();
                        this.antiBanInProgress = false;
                        break;
                }
            }
            if (!this.antiBanInProgress && GaussianTools.takeActionUnlikely()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;
                switch (Random.nextInt(0, 4)) {
                    case 0:
                        AntibanActions.setRandomCameraAngle(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        AntibanActions.setRandomCameraPitch(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        AntibanActions.checkCombatLevel(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 3:
                        AntibanActions.resetCamera(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 4:
                        AntibanActions.toggleRun(ctx);
                        this.antiBanInProgress = false;
                        break;
                }
            }
        }

        if (!this.antiBanInProgress) {
            this.scriptConfig.setStatus("Combat");
            this.combatPhase.activate();
        }
    }
    //endregion

    //region messaged
    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text();

        if (msg.contains("you are dead")) {
            ctx.controller.stop();
        }
    }
    //endregion

    //region repaint
    @Override
    public void repaint(Graphics g) {
        this.scriptConfig.paint(g, getRuntime());

        g.drawString("Phase:" + this.combatPhase.getName(), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(3));

    }
    //endregion
}