package scripts.cooking_lumby_castle;


import scripts.cooking_lumby_castle.phases.ClimbStairsPhase;
import scripts.cooking_lumby_castle.phases.DescendStairsPhase;
import shared.actions.BankAction;
import shared.action_config.ScriptConfig;
import shared.models.BasePhase;
import shared.tools.*;
import scripts.cooking_lumby_castle.actions.*;
import scripts.cooking_lumby_castle.phases.BankingPhase;
import scripts.cooking_lumby_castle.phases.CookingPhase;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Item;

import java.awt.*;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "cooking_LumbyCastleCooker", description = "Cooks at the range and banks on the roof.", properties = "client=4; topic=051515; author=Bowman")
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

    private final CookingPhase cookingPhase = new CookingPhase(ctx, 0);
    private final BankingPhase bankingPhase = new BankingPhase(ctx, 0);
    private final ClimbStairsPhase ascendStairsPhase = new ClimbStairsPhase(ctx, 0);
    private final DescendStairsPhase descendStairsPhase = new DescendStairsPhase(ctx, 0);
    private BasePhase<ClientContext> currentPhase;
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
        //region Actions
        BankAction bankAction = new BankAction(ctx, "Banking", 0, -1, i.id(), 28, -1, -1, false, true, false, CommonAreas.lumbridgeCastleBank());
        WalkLumbyBankToStairs walkBankToStairsAction = new WalkLumbyBankToStairs(ctx, i.id());
        WalkKitchenToStairs walkKitchenToStairsAction = new WalkKitchenToStairs(ctx, i.id());
        WalkStairsToLumbyBank walkStairsToBank = new WalkStairsToLumbyBank(ctx, i.id());
        NavigateStairs navigateStairsAction = new NavigateStairs(ctx, i.id());
        CookFood cookFoodAction = new CookFood(ctx, i.id());

        // Phases
        this.cookingPhase.setName("Cooking");
        this.cookingPhase.setRawId(i.id());
        this.cookingPhase.addAction(cookFoodAction);

        this.ascendStairsPhase.setName("Ascending Stairs");
        this.ascendStairsPhase.setRawId(i.id());
        this.ascendStairsPhase.addAction(walkKitchenToStairsAction);
        this.ascendStairsPhase.addAction(navigateStairsAction);

        this.bankingPhase.setName("Banking");
        this.bankingPhase.setRawId(i.id());
        this.bankingPhase.addAction(walkStairsToBank);
        this.bankingPhase.addAction(bankAction);
        this.bankingPhase.addAction(walkBankToStairsAction);


        this.descendStairsPhase.setName("Descending Stairs");
        this.descendStairsPhase.setRawId(i.id());
        this.descendStairsPhase.addAction(navigateStairsAction);


        // Phase Transitions
        this.cookingPhase.setNextPhase(this.ascendStairsPhase);
        this.ascendStairsPhase.setNextPhase(this.bankingPhase);
        this.bankingPhase.setNextPhase(this.descendStairsPhase);
        this.descendStairsPhase.setNextPhase(this.cookingPhase);

        // Starting Location
        if (cookFoodAction.activate()) {
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