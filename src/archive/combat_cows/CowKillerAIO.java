package scripts.combat_cows;

import shared.actions.CombatAction;
import shared.actions.EquipArrows;
import shared.actions.HealAction;
import shared.actions.LootAction;
import shared.action_config.ScriptConfig;
import shared.tools.AntibanTools;
import shared.tools.CommonActions;
import shared.tools.GaussianTools;
import shared.tools.GuiHelper;
import shared.models.LootItem;
import shared.models.LootList;
import scripts.combat_cows.phases.CombatPhase;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;

@Script.Manifest(name = "cmb_CowKillerAIO", description = "Kills Cows.", properties = "client=4; topic=051515; author=Bowman")
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

    private final CombatPhase combatPhase = new CombatPhase(ctx);
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
        skills[0] = CommonActions.promptForCombatStyle(ctx);

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
            projectileId = CommonActions.promptForArrowType();
            this.lootList = new LootList();
            this.lootList.addLootItem(new LootItem(projectileId, 3));
            this.lootAction = new LootAction(ctx, "Loot", lootList, 10, false);
        }

        // Action Setup
        this.combatCowAction = new CombatAction(ctx, "Attack Cow", "Cow", -1, 20,lootList, ctx.combat.inMultiCombat(),null,0 );
        this.combatCowAction = new CombatAction(ctx, "Attack Cow calf", "Cow calf", -1, 20,lootList, ctx.combat.inMultiCombat(),null,0 );
        this.healAction = new HealAction(ctx, "Healing", CommonActions.allFoodIds(), 50);

        this.equipArrows = new EquipArrows(ctx, projectileId, 60);

        this.combatPhase.setStatus("Combat");
        this.combatPhase.addAction(this.healAction);
        this.combatPhase.addAction(this.lootAction);
        this.combatPhase.addAction(this.equipArrows);
        this.combatPhase.addAction(this.combatCalfAction);
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
                        AntibanTools.hoverRandomNPC(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        AntibanTools.hoverRandomObject(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        AntibanTools.toggleXPDrops(ctx);
                        this.antiBanInProgress = false;
                        break;
                }
            }
            if (!this.antiBanInProgress && GaussianTools.takeActionRarely()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;

                switch (Random.nextInt(0, 3)) {
                    case 0:
                        AntibanTools.moveMouseOffScreen(ctx, true);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        AntibanTools.checkStat(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        AntibanTools.jiggleMouse(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 3:
                        AntibanTools.doNothing();
                        this.antiBanInProgress = false;
                        break;
                }
            }
            if (!this.antiBanInProgress && GaussianTools.takeActionUnlikely()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;
                switch (Random.nextInt(0, 4)) {
                    case 0:
                        AntibanTools.setRandomCameraAngle(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        AntibanTools.setRandomCameraPitch(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        AntibanTools.checkCombatLevel(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 3:
                        AntibanTools.resetCamera(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 4:
                        AntibanTools.toggleRun(ctx);
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

        g.drawString("Phase:" + this.combatPhase.getStatus(), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(3));

    }
    //endregion
}