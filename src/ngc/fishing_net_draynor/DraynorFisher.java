package ngc.fishing_net_draynor;


import ngc._resources.Items;
import ngc._resources.actions.BankAction;
import ngc._resources.actions.ToggleRunAction;
import ngc._resources.actions.WalkToAreaAction;
import ngc._resources.actions._config.BankConfig;
import ngc._resources.actions._config.RunConfig;
import ngc._resources.actions._config.ScriptConfig;
import ngc._resources.actions._config.WalkConfig;
import ngc._resources.actions._template.BasePhase;
import ngc._resources.functions.*;
import ngc.fishing_net_draynor.actions.FishingAction;
import ngc.fishing_net_draynor.phases.BankingPhase;
import ngc.fishing_net_draynor.phases.FishingPhase;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;

@Script.Manifest(name = "DraynorFisher", description = "Kills Cows.", properties = "client=4; topic=051515; author=Bowman")
public class DraynorFisher extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Config
    private ScriptConfig scriptConfig = new ScriptConfig(ctx, null);
    private boolean antiBanInProgress;
    private int startExp;
    private int numToLevel;
    private int catchCount;
    private int avgCatchXp;

    //region Antiban
    private long lastBreakTimestamp;
    private int nextBreakInMinutes;
    //endregion

    //region Phases

    private BankingPhase bankingPhase = new BankingPhase(ctx);
    private FishingPhase fishingPhase = new FishingPhase(ctx);
    private BasePhase<ClientContext> currentPhase;

    //endregion

    //region Actions
    private BankAction bankAction;
    private FishingAction fishingAction;
    private WalkToAreaAction walkToBankAction;
    private ToggleRunAction toggleRunAction;
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
        nextBreakInMinutes = Random.nextInt(3, 10);

        // Actions
        BankConfig bankConfig = new BankConfig(Items.RAW_SHRIMPS_317, Items.RAW_ANCHOVIES_321, -1, -1, -1, -1, true, false, false);
        this.bankAction = new BankAction(ctx, "", bankConfig);
        this.bankAction.setStatus("Deposit Fish");

        WalkConfig walkConfig = new WalkConfig();
        walkConfig.setActivateOnInvCount(true);
        walkConfig.setActivationInvCount(28);
        walkConfig.setTargetArea(CommonAreas.getDraynorBank());
        walkConfig.setPath(new Tile[]{new Tile(3088, 3226, 0), new Tile(3090, 3230, 0), new Tile(3090, 3234, 0), new Tile(3089, 3238, 0), new Tile(3087, 3242, 0), new Tile(3087, 3246, 0), new Tile(3091, 3247, 0), new Tile(3092, 3243, 0)});
        this.walkToBankAction = new WalkToAreaAction(ctx, walkConfig);
        this.walkToBankAction.setStatus("Walking");

        this.fishingAction = new FishingAction(ctx);
        this.fishingAction.setStatus("Net Fishing");
        this.toggleRunAction = new ToggleRunAction(ctx, "", new RunConfig(50));

        // Phases
        this.fishingPhase = new FishingPhase(ctx);
        this.fishingPhase.addAction(this.fishingAction);
        this.fishingPhase.setName("Fishing");

        this.bankingPhase = new BankingPhase(ctx);
        this.bankingPhase.setName("Banking");
        this.bankingPhase.addAction(this.bankAction);
        this.bankingPhase.addAction(this.walkToBankAction);
        this.bankingPhase.addAction(this.toggleRunAction);

        // Phase Transitions
        this.fishingPhase.setNextPhase(this.bankingPhase);
        this.bankingPhase.setNextPhase(this.fishingPhase);

        this.currentPhase = this.fishingPhase;

        this.scriptConfig.setStatus("Start");
    }
    //endregion

    //region poll
    @Override
    public void poll() {
        // Pre State Check Action
        this.scriptConfig.prePollAction();

        numToLevel = 1 +  ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_FISHING) + 1) - (ctx.skills.experience(Constants.SKILLS_FISHING))) / avgCatchXp);


        // Antiban Check
        if (getRuntime() - lastBreakTimestamp > (1000 * 60 * nextBreakInMinutes)) {
            this.lastBreakTimestamp = getRuntime();
            this.nextBreakInMinutes = Random.nextInt(1, 4);
            this.antiBanInProgress = false;

            if (GaussianTools.takeActionUnlikely()) {
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
                    case 3:
                        AntibanActions.resetCamera(ctx);
                        this.antiBanInProgress = false;
                        break;
                }
            }
        }

        if (!this.antiBanInProgress) {
            this.currentPhase.activate();
            this.scriptConfig.setStatus(this.currentPhase.getStatus());


            if (this.currentPhase.moveToNextPhase()) {
                this.currentPhase = this.currentPhase.getNextPhase();
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