package disabled.woodcutting_oak_varrock;


import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Equipment;
import disabled.woodcutting_oak_varrock.phases.BankingPhase;
import disabled.woodcutting_oak_varrock.phases.OakPhase;
import disabled.woodcutting_oak_varrock.phases.WalkToBankPhase;
import disabled.woodcutting_oak_varrock.phases.WalkToOaksPhase;
import shared.action_config.ScriptConfig;
import shared.constants.Items;
import shared.templates.StructuredPhase;
import shared.tools.AntibanTools;
import shared.tools.CommonActions;
import shared.tools.GaussianTools;
import shared.tools.GuiHelper;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "woodcutting_VarrockOakChopper", description = "VarrockOakChopper", properties = "client=4; topic=051515; author=Bowman")
public class VarrockOakChopper extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Config
    private ScriptConfig scriptConfig = new ScriptConfig(ctx, null);
    private boolean antiBanInProgress;
    private StructuredPhase currentPhase;

    private int logsToLevel;

    //region Antiban
    private long lastBreakTimestamp;
    private int nextBreakInMinutes;
    //endregion


    //region start
    @Override
    public void start() {
        // Tracked Skills
        int[] skills = new int[1];
        skills[0] = Constants.SKILLS_WOODCUTTING;

        this.scriptConfig = new ScriptConfig(ctx, skills);
        this.scriptConfig.setStatus("Config");

        int axeId = ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).valid() ? ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() : -1;

        if (axeId == -1) {
            var axeType = CommonActions.promptForSelection("Which axe?", null, new String[]{"Mithril", "Adamant", "Rune", "Dragon"});

            switch (axeType) {
                case "Mithirl":
                    axeId = Items.MITHRIL_AXE_1355;
                    break;
                case "Adamant":
                    axeId = Items.ADAMANT_AXE_1357;
                    break;
                case "Rune":
                    axeId = Items.RUNE_AXE_1359;
                    break;
                case "Dragon":
                    axeId = Items.DRAGON_AXE_6739;
                    break;
            }
        }
        // Antiban
        lastBreakTimestamp = 0L;
        nextBreakInMinutes = AntibanTools.getRandomInRange(1, 3);

        // Phase
        OakPhase oakPhase = new OakPhase(ctx, "CUT");
        WalkToBankPhase walkToBankPhase = new WalkToBankPhase(ctx, "WALK");
        BankingPhase bankingPhase = new BankingPhase(ctx, "BANK", axeId);
        WalkToOaksPhase walkToOaksPhase = new WalkToOaksPhase(ctx, "WALK");

        // Phase Transitions
        oakPhase.setNextPhase(walkToBankPhase);
        walkToBankPhase.setNextPhase(bankingPhase);
        bankingPhase.setNextPhase(walkToOaksPhase);
        walkToOaksPhase.setNextPhase(oakPhase);

        // Starting Location
        if (ctx.bank.inViewport()) {
            this.currentPhase = bankingPhase;
        } else {
            this.currentPhase = oakPhase;
        }

        // Initial Status
        this.scriptConfig.setStatus(this.currentPhase.getStatus());

        logsToLevel = 1 + (int) ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_WOODCUTTING) + 1) - (ctx.skills.experience(Constants.SKILLS_WOODCUTTING))) / 37.5);


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

            if (GaussianTools.takeActionRarely()) {
                this.scriptConfig.setStatus("Antiban");
                this.antiBanInProgress = true;

                switch (AntibanTools.getRandomInRange(0, 2)) {
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

                switch (AntibanTools.getRandomInRange(0, 3)) {
                    case 0:
                        AntibanTools.moveMouseOffScreen(ctx, true);
                        this.antiBanInProgress = false;
                        break;
                    case 1:
                        AntibanTools.checkStat(ctx, Constants.SKILLS_WOODCUTTING);
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
            if (!this.antiBanInProgress && GaussianTools.takeActionNormal()) {
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
                }
            }
        }

        if (this.currentPhase.getName().equals("CUT")) {
            // Check for bird nest on ground and loot if found

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

        if (msg.contains("get some oak logs")) {
            logsToLevel = 1 + (int) ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_WOODCUTTING) + 1) - (ctx.skills.experience(Constants.SKILLS_WOODCUTTING))) / 37.5);
        }

        if (msg.contains("nest falls out of the tree") && !ctx.inventory.isFull()) {
            var nest = ctx.groundItems.select(3).name("Bird nest").nearest().poll();

            if(nest.valid()){
                nest.interact("Take");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !nest.valid();
                    }
                }, 100, 10);
            }
        }

    }
    //endregion

    //region repaint
    @Override
    public void repaint(Graphics g) {
        this.scriptConfig.paint(g, getRuntime());
        g.drawString("Logs to LVL: " + logsToLevel, GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(2));

    }
    //endregion
}