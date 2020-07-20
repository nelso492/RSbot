package scripts.fishing_harpoon_catherby;


import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import scripts.fishing_harpoon_catherby.actions.FishingAction;
import scripts.fishing_harpoon_catherby.actions.WalkToCatherbyBankAction;
import scripts.fishing_harpoon_catherby.phases.BankingPhase;
import scripts.fishing_harpoon_catherby.phases.FishingPhase;
import shared.action_config.ScriptConfig;
import shared.actions.BankAction;
import shared.actions.ToggleRunAction;
import shared.constants.Items;
import shared.models.BasePhase;
import shared.tools.AntibanTools;
import shared.tools.CommonAreas;
import shared.tools.GaussianTools;
import shared.tools.GuiHelper;

import java.awt.*;

@Script.Manifest(name = "fishing_CatherbyFisher", description = "Net fishing at Catherby village w/ banking", properties = "client=4; topic=051515; author=NGC")
public class CatherbyFisher extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    //region Config
    private ScriptConfig scriptConfig = new ScriptConfig(ctx, null);
    private BasePhase<ClientContext> currentPhase;

    private boolean antiBanInProgress;
    private int startExp;
    private int numToLevel;
    private int catchCount;
    private int avgCatchXp;
    //endregion

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
        nextBreakInMinutes = Random.nextInt(3, 10);

        // Actions
        //region Actions
        BankAction bankAction = new BankAction(ctx, "", Items.RAW_TUNA_359, Items.RAW_ANCHOVIES_321, -1, -1, -1, -1, true, false, false, CommonAreas.catherybyBank());
        bankAction.setStatus("Deposit Fish");

        WalkToCatherbyBankAction walkToBankAction = new WalkToCatherbyBankAction(ctx);

        FishingAction fishingAction = new FishingAction(ctx);
        fishingAction.setStatus("Net Fishing");
        ToggleRunAction toggleRunAction = new ToggleRunAction(ctx, "", 50);

        // Phases
        FishingPhase fishingPhase = new FishingPhase(ctx);
        fishingPhase.addAction(fishingAction);
        fishingPhase.addAction(walkToBankAction);
        fishingPhase.setName("Fishing");

        BankingPhase bankingPhase = new BankingPhase(ctx);
        bankingPhase.setName("Banking");
        bankingPhase.addAction(toggleRunAction);
        bankingPhase.addAction(bankAction);

        // Phase Transitions
        fishingPhase.setNextPhase(bankingPhase);
        bankingPhase.setNextPhase(fishingPhase);

        this.currentPhase = fishingPhase;

        this.scriptConfig.setStatus("Start");
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
            this.nextBreakInMinutes = Random.nextInt(1, 4);
            this.antiBanInProgress = false;

            if (GaussianTools.takeActionUnlikely()) {
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
                    case 3:
                        AntibanTools.resetCamera(ctx);
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