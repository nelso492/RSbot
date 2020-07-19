package ngc.cooking_lumby_castle;


import resources.actions.BankAction;
import resources.actions.ToggleLevelUp;
import resources.action_config.BankConfig;
import resources.action_config.ScriptConfig;
import resources.models.BasePhase;
import resources.tools.AntibanTools;
import resources.tools.CommonAreas;
import resources.tools.GaussianTools;
import resources.tools.GuiHelper;
import ngc.cooking_lumby_castle.actions.*;
import ngc.cooking_lumby_castle.phases.BankingPhase;
import ngc.cooking_lumby_castle.phases.CookingPhase;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Item;

import java.awt.*;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "CastleCooker", description = "Kills Cows.", properties = "client=4; topic=051515; author=Bowman")
public class CastleCooker extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Config
    private ScriptConfig scriptConfig = new ScriptConfig(ctx, null);
    private boolean antiBanInProgress;

    private int cookCount;
    private int cooksToLvl;
    private int lvlsGained;
    private int startExp;
    private double avgExpPerCook;

    private String rawItemName;

    //region Antiban
    private long lastBreakTimestamp;
    private int nextBreakInMinutes;
    //endregion

    //region Phases

    private CookingPhase cookingPhase = new CookingPhase(ctx, 0);
    private BankingPhase bankingPhase = new BankingPhase(ctx, 0);
    private BasePhase<ClientContext> currentPhase;
    //endregion

    //region Actions
    private BankAction bankAction;
    private CookFood cookFoodAction;
    private NavigateStairs navigateStairsAction;
    private WalkLumbyBankToStairs walkBankToStairsAction;
    private WalkKitchenToStairs walkKitchenToStairsAction;
    private ToggleLevelUp toggleLevelUpAction;
    private WalkStairsToLumbyBank walkStairsToBank;
    //endregion

    //region start
    @Override
    public void start() {
        // Tracked Skills
        int[] skills = new int[1];
        skills[0] = Constants.SKILLS_COOKING;
        this.startExp = ctx.skills.experience(skills[0]);

        this.scriptConfig = new ScriptConfig(ctx, skills);
        this.scriptConfig.setStatus("Config");

        // Antiban
        lastBreakTimestamp = 0L;
        nextBreakInMinutes = Random.nextInt(3, 7);

        // Find Raw Item to cook
        Item i = ctx.inventory.select().select(new Filter<Item>() {
            @Override
            public boolean accept(Item item) {
                return item.name().toLowerCase().contains("raw");
            }
        }).first().poll();

        if (i.valid()) {
            rawItemName = i.name().substring(4, 5).toUpperCase() + i.name().substring(5);
        } else {
            ctx.controller.stop();
        }

        // Actions
        BankConfig _bank = new BankConfig(0, -1, i.id(), 28, -1, -1, false, true, true);
        _bank.setBankArea(CommonAreas.lumbridgeCastleBank());
        //_bank.setToBankPath(new Tile[]{new Tile(3205, 3210, 2), new Tile(3206, 3215, 2)});


        this.bankAction = new BankAction(ctx, "Banking", _bank);
        this.walkBankToStairsAction = new WalkLumbyBankToStairs(ctx, i.id());
        this.walkKitchenToStairsAction = new WalkKitchenToStairs(ctx, i.id());
        this.walkStairsToBank = new WalkStairsToLumbyBank(ctx, i.id());
        this.navigateStairsAction = new NavigateStairs(ctx, i.id());
        this.cookFoodAction = new CookFood(ctx, i.id());
        this.toggleLevelUpAction = new ToggleLevelUp(ctx);

        // Phases
        this.cookingPhase.setName("Cooking");
        this.cookingPhase.setRawId(i.id());
        this.cookingPhase.addAction(this.cookFoodAction);
        this.cookingPhase.addAction(this.toggleLevelUpAction);

        this.bankingPhase.setName("Banking");
        this.bankingPhase.setRawId(i.id());
        this.bankingPhase.addAction(this.walkKitchenToStairsAction);
        this.bankingPhase.addAction(this.navigateStairsAction);
        this.bankingPhase.addAction(this.walkStairsToBank);
        this.bankingPhase.addAction(this.bankAction);
        this.bankingPhase.addAction(this.walkBankToStairsAction);

        // Phase Transitions
        this.cookingPhase.setNextPhase(this.bankingPhase);
        this.bankingPhase.setNextPhase(this.cookingPhase);

        // Starting Location
        if (this.cookFoodAction.activate()) {
            this.currentPhase = this.cookingPhase;
        } else {
            this.currentPhase = this.bankingPhase;
        }

        this.scriptConfig.setStatus(this.currentPhase.getStatus());

    }
    //endregion

    //region poll
    @Override
    public void poll() {
        // Pre State Check Action
        this.scriptConfig.prePollAction();

        cooksToLvl = 1 + (int) ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_COOKING) + 1) - (ctx.skills.experience(Constants.SKILLS_COOKING))) / avgExpPerCook);

        // Antiban Check
        if (getRuntime() - lastBreakTimestamp > (1000 * 60 * nextBreakInMinutes)) {
            this.lastBreakTimestamp = getRuntime();
            this.nextBreakInMinutes = Random.nextInt(1, 4);
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
                        AntibanTools.checkStat(ctx, Constants.SKILLS_COOKING);
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
            this.scriptConfig.setStatus(this.currentPhase.getStatus());
            this.currentPhase.activate();

            if (this.currentPhase.moveToNextPhase()) {
                this.currentPhase = this.currentPhase.getNextPhase();
                this.scriptConfig.setStatus(this.currentPhase.getStatus());
            }
        }
    }
    //endregion

    //region messaged
    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text();
        if (msg.contains("cook") || msg.contains("roast")) {
            cookCount++;
        }

        if (msg.contains("advanced your cooking level.")) {
            lvlsGained++;
        }

        if (msg.contains("can't reach that") && ctx.game.floor() == 2) {
            ctx.movement.step(ctx.bank.nearest());
            sleep(1000);
        }

        avgExpPerCook = (ctx.skills.experience(Constants.SKILLS_COOKING) - startExp) / cookCount;

    }
    //endregion

    //region repaint
    @Override
    public void repaint(Graphics g) {
        this.scriptConfig.paint(g, getRuntime());
        g.drawString("Item : " + rawItemName, GuiHelper.getDialogMiddleX(), GuiHelper.getStartY(1));
        g.drawString("Level: " + ctx.skills.level(Constants.SKILLS_COOKING) + " [" + lvlsGained + " ]", GuiHelper.getDialogMiddleX(), GuiHelper.getStartY(2));
        g.drawString("CTL  : " + cooksToLvl, GuiHelper.getDialogMiddleX(), GuiHelper.getStartY(3));
        g.drawString("Count: " + cookCount, GuiHelper.getDialogMiddleX(), GuiHelper.getStartY(4));

    }
    //endregion
}