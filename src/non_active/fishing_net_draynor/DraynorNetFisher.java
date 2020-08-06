package scripts.fishing_net_draynor;


import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import scripts.fishing_net_draynor.phases.BankingPhase;
import scripts.fishing_net_draynor.phases.DraynorNetFishingPhase;
import shared.action_config.ScriptConfig;
import shared.templates.StructuredPhase;
import shared.tools.AntibanTools;
import shared.tools.GaussianTools;
import shared.tools.GuiHelper;

import java.awt.*;

@Script.Manifest(name = "FISH - DraynorNetFisher", description = "DraynorNetFisher", properties = "client=4; topic=051515; author=Bowman")
public class DraynorNetFisher extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Config
    private ScriptConfig scriptConfig = new ScriptConfig(ctx, null);
    private boolean antiBanInProgress;
    private StructuredPhase currentPhase;

    private int numToLevel;
    private int catchCount;
    private int avgCatchXp;
    private int startExp;

    //region Antiban
    private long lastBreakTimestamp;
    private int nextBreakInMinutes;
    //endregion


    //region start
    @Override
    public void start() {
        // Tracked Skills
        int[] skills = new int[1];
        skills[0] = Constants.SKILLS_FISHING;
        this.startExp = ctx.skills.experience(skills[0]);

        this.catchCount = 0;
        this.avgCatchXp = 1;

        this.scriptConfig = new ScriptConfig(ctx, skills);
        this.scriptConfig.setStatus("Config");

        // Antiban
        lastBreakTimestamp = 0L;
        nextBreakInMinutes = AntibanTools.getRandomInRange(3, 7);

        // Phase
        DraynorNetFishingPhase fishingPhase = new DraynorNetFishingPhase(ctx, "Fishing");
        BankingPhase bankingPhase = new BankingPhase(ctx, "Banking");

        fishingPhase.setNextPhase(bankingPhase);
        bankingPhase.setNextPhase(fishingPhase);

        if (ctx.npcs.select().id(1525).nearest().poll().inViewport()) {
            this.currentPhase = fishingPhase;
        } else {
            this.currentPhase = bankingPhase;
        }

        // Initial Status
        this.scriptConfig.setStatus(this.currentPhase.getStatus());

    }
    //endregion

    //region poll
    @Override
    public void poll() {
        // Pre State Check Action
        this.scriptConfig.prePollAction();

        numToLevel = 1 + ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_FISHING) + 1) - (ctx.skills.experience(Constants.SKILLS_FISHING))) / avgCatchXp);

        // Antiban Check
        if (getRuntime() - lastBreakTimestamp > (1000 * 60 * nextBreakInMinutes)) {
            this.lastBreakTimestamp = getRuntime();
            this.nextBreakInMinutes = AntibanTools.getRandomInRange(1, 4);
            this.antiBanInProgress = false;

            if (GaussianTools.takeActionNever()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;
                AntibanTools.toggleXPDrops(ctx);
                this.antiBanInProgress = false;
            }
            if (!this.antiBanInProgress && GaussianTools.takeActionNormal()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;

                switch (AntibanTools.getRandomInRange(0, 3)) {
                    case 0:
                        AntibanTools.moveMouseOffScreen(ctx, true);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        AntibanTools.checkStat(ctx, Constants.SKILLS_FISHING);
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
            if (!this.antiBanInProgress && GaussianTools.takeActionLikely()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;
                switch (AntibanTools.getRandomInRange(0, 2)) {
                    case 0:
                        AntibanTools.setRandomCameraAngle(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        AntibanTools.setRandomCameraPitch(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 2:
                        AntibanTools.resetCamera(ctx);
                        this.antiBanInProgress = false;
                        break;
                    case 3:
                        AntibanTools.toggleRun(ctx);
                        this.antiBanInProgress = false;
                        break;
                }
            }
        }

        if (!this.antiBanInProgress && this.currentPhase != null) {
            this.scriptConfig.setStatus(this.currentPhase.getStatus());

            this.currentPhase.activate();

            if (this.currentPhase.moveToNextPhase()) {
                this.currentPhase = (StructuredPhase) this.currentPhase.getNextPhase();
                this.currentPhase.resetCurrentAction();
                this.scriptConfig.setStatus(this.currentPhase.getStatus());
            }
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

        if (msg.contains("You catch some")) {
            catchCount++;
            avgCatchXp = (ctx.skills.experience(Constants.SKILLS_FISHING) - startExp) / catchCount;
        }
    }
    //endregion

    //region repaint
    @Override
    public void repaint(Graphics g) {
        this.scriptConfig.paint(g, getRuntime());
        g.drawString("CTL  : " + numToLevel, GuiHelper.getDialogMiddleX(), GuiHelper.getStartY(2));
        g.drawString("Count: " + catchCount, GuiHelper.getDialogMiddleX(), GuiHelper.getStartY(3));
    }
    //endregion
}