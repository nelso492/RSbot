package scripts.combat_minotaur;


import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import scripts.combat_minotaur.phases.MinotaurCombatPhase;
import shared.action_config.ScriptConfig;
import shared.templates.PollingPhase;
import shared.tools.AntibanTools;
import shared.tools.GaussianTools;

import java.awt.*;

@Script.Manifest(name = "CMB - Minotaur", description = "Kills in stronghold. loots ess, gems, arrows", properties = "client=4; topic=051515; author=Bowman")
public class CombatMinotaur extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Config
    private ScriptConfig scriptConfig = new ScriptConfig(ctx, null);
    private boolean antiBanInProgress;
    private PollingPhase currentPhase;

    //region Antiban
    private long lastBreakTimestamp;
    private int nextBreakInMinutes;
    //endregion

    //region start
    @Override
    public void start() {

        // Tracked Skills
        int[] skills = new int[4];
        skills[0] = Constants.SKILLS_HITPOINTS;
        skills[1] = Constants.SKILLS_RANGE;
        skills[2] = -1;
        skills[3] = -1;
//        skills[1] = Constants.SKILLS_ATTACK;
//        skills[2] = Constants.SKILLS_STRENGTH;
//        skills[3] = Constants.SKILLS_DEFENSE;

        this.scriptConfig = new ScriptConfig(ctx, skills);
        this.scriptConfig.setStatus("Config");

        // Antiban
        lastBreakTimestamp = 0L;
        nextBreakInMinutes = AntibanTools.getRandomInRange(3, 7);

        // Starting Location
        this.currentPhase = new MinotaurCombatPhase(ctx, "Combat");

        // Initial Status
        this.scriptConfig.setStatus(this.currentPhase.getStatus());

    }
    //endregion

    //region poll
    @Override
    public void poll() {
        // Pre State Check Action
        this.scriptConfig.prePollAction();


        // Antiban Check
        if (getRuntime() - lastBreakTimestamp > (1000 * 60 * nextBreakInMinutes)) {
            this.lastBreakTimestamp = getRuntime();
            this.nextBreakInMinutes = AntibanTools.getRandomInRange(1, 4);
            this.antiBanInProgress = false;

            if (GaussianTools.takeActionNever()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;

                switch (AntibanTools.getRandomInRange(0, 2)) {
                    case 0:
                        this.scriptConfig.setStatus("Antiban.RandomNPC");
                        AntibanTools.hoverRandomNPC(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        this.scriptConfig.setStatus("Antiban.RandomObj");
                        AntibanTools.hoverRandomObject(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        this.scriptConfig.setStatus("Antiban.ToggleXP");
                        AntibanTools.toggleXPDrops(ctx);
                        this.antiBanInProgress = false;
                        break;
                }
            }
            if (!this.antiBanInProgress && GaussianTools.takeActionRarely()) {
                this.antiBanInProgress = true;

                switch (AntibanTools.getRandomInRange(0, 3)) {
                    case 0:
                        this.scriptConfig.setStatus("Antiban.MoveMouse");
                        AntibanTools.moveMouseOffScreen(ctx, true);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        this.scriptConfig.setStatus("Antiban.CheckStat");
                        AntibanTools.checkStat(ctx, Constants.SKILLS_RANGE);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        this.scriptConfig.setStatus("Antiban.JiggleMouse");
                        AntibanTools.jiggleMouse(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 3:
                        this.scriptConfig.setStatus("Antiban.DoNothing");
                        AntibanTools.doNothing();
                        this.antiBanInProgress = false;
                        break;
                }
            }
            if (!this.antiBanInProgress && GaussianTools.takeActionUnlikely()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;
                switch (AntibanTools.getRandomInRange(0, 4)) {
                    case 0:
                        this.scriptConfig.setStatus("Antiban.RandomAngle");

                        AntibanTools.setRandomCameraAngle(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        this.scriptConfig.setStatus("Antiban.RandomPitch");

                        AntibanTools.setRandomCameraPitch(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        this.scriptConfig.setStatus("Antiban.CombatLvl");

                        AntibanTools.checkCombatLevel(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 3:
                        this.scriptConfig.setStatus("Antiban.ResetCamera");

                        AntibanTools.resetCamera(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 4:
                        this.scriptConfig.setStatus("Antiban.ToggleRun");

                        AntibanTools.toggleRun(ctx);
                        this.antiBanInProgress = false;
                        break;
                }
            }
        }

        if (!this.antiBanInProgress && this.currentPhase != null) {
            this.scriptConfig.setStatus(this.currentPhase.getStatus());

            this.currentPhase.activate();
        }
    }
    //endregion

    //region messaged
    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text();

        if (msg.contains("just advanced your")) {
            this.scriptConfig.incrementLevelsGained();
        }
    }
    //endregion

    //region repaint
    @Override
    public void repaint(Graphics g) {
        this.scriptConfig.paint(g, getRuntime());
    }
    //endregion
}