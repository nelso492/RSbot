package scripts.wine_maker;

import shared.constants.Items;
import shared.actions.ToggleLevelUp;
import shared.tools.CommonActions;
import shared.tools.GaussianTools;
import shared.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Wine Maker", description = "Make wine & ingredients at a bank", properties = "client=4")
public class _WineMaker extends PollingScript<ClientContext> implements MessageListener, PaintListener {


    // GUI Tracking
    private String status;
    private long lastBreak;
    private long totalBreakTime;

    // XP Tracking
    private int levelsGained;
    private int skillLevel;

    // Action Tracking
    private int actionsTaken;

    // Actions
    private ToggleLevelUp levelUp;

    // Steps
    private enum State {
        Combine, Level, Bank, Waiting
    }

    @Override
    public void start() {

        status = "Loading";

        // Bank Task
       /* if( bankConfig == null ) {
            // use default
            bankConfig = new BankConfig(0, 0, primaryComponentId, 14, consumableComponentId, 14, false, true, true);
        }
        bankAction = new BankAction(ctx, "Banking", bankConfig);*/

        // Misc Configs
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events
        levelsGained = 0;
        lastBreak = 0L;
        totalBreakTime = 0L;
        skillLevel = ctx.skills.realLevel(Constants.SKILLS_COOKING);

        levelUp = new ToggleLevelUp(ctx);
    }

    private State checkState() {

        if( ctx.inventory.select().id(Items.GRAPES_1987).count() > 0 && ctx.inventory.select().id(Items.JUG_OF_WATER_1937).count() > 0 ) {
            return State.Combine;
        } else {
            return State.Bank;
        }
    }

    @Override
    public void poll() {
        switch( checkState() ) {
            case Bank:
                status = "Bank";
                bankTask();
                break;
            case Combine:
                status = "Combine";
                makeWines();
                break;
            case Level:
                status = "Level Up";
                levelUp.execute();
                break;
            default:
                status = "Waiting";
        }
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        String msg = messageEvent.text();

        if( msg.contains("advanced your") ) {
            levelsGained++;
            skillLevel++;
        }

        if( msg.contains("ferment") ) {
            actionsTaken += 14;
        }
    }

    @Override
    public void repaint(Graphics g) {

        /*Draw Background*/
        g.drawString("Wines : " + (actionsTaken), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));

      /*  g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getDialogStartX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getDialogStartX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        *//*Draw Data*//*
        g.drawString("Status : " + (status), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(2));

        *//*Level*//*
        g.setColor(GuiHelper.getTextColorInformation());
        g.drawString("Level : " + (skillLevel) + " [" + levelsGained + "]", GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(4));
        g.drawString("Wines : " + (actionsTaken), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(5));
        g.drawString("To Lvl: " + ((ctx.skills.experienceAt(skillLevel +1) - ctx.skills.experience(Constants.SKILLS_COOKING)) / 200), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(6));

        g.setColor(GuiHelper.getTextColorImportant());
        g.drawString("Break Time: " + GuiHelper.getReadableRuntime(totalBreakTime), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(1));
        g.drawString("Last Break: " + GuiHelper.getReadableRuntime(lastBreak), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(2));
*/
    }

    private void bankTask() {
        if( !ctx.bank.opened() ) {
            ctx.bank.open();
        }

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.bank.opened();
            }
        }, 100, 20);

        if( ctx.bank.opened() ) {

            // Deposit
            if( ctx.bank.withdrawModeQuantity() != Bank.Amount.X ) {
                ctx.bank.withdrawModeQuantity(Bank.Amount.X);
                sleep();
            }

            if( ctx.inventory.select().count() > 0 ) {
                //  ctx.inventory.select().shuffle().poll().click();
                //                sleep(100);

                if( !ctx.inventory.select().isEmpty() ) {
                    ctx.bank.depositInventory();
                }
            }

            // Withdraw
            ctx.bank.select().id(Items.JUG_OF_WATER_1937).poll().click();
            ctx.bank.select().id(Items.GRAPES_1987).poll().click();

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.isFull();
                }
            }, 100, 30);

            ctx.bank.close();
        }
    }

    private void makeWines() {
        if( !ctx.widgets.component(270, 14).valid() ) {
            // Temporary break
            if( GaussianTools.takeActionRarely() ) {
                // Click break
                int breakTime = Random.nextInt(200, 5000);
                status = "Break: " + breakTime / 1000 + "s";
                totalBreakTime += (long) breakTime;
                if( GaussianTools.takeActionUnlikely() ) {
                    CommonActions.moveMouseOffscreen(ctx, false);
                }
                sleep(breakTime);
                status = "Combine";
            }
            // Select
            ctx.inventory.select().id(Items.JUG_OF_WATER_1937).shuffle().poll().click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.selectedItem().valid();
                }
            }, 50, 20);
            if( !ctx.widgets.component(270, 14).valid() ) {
                // break up the automation timing
                sleep(GaussianTools.getRandomGaussian(150, 40));

                // Combine
                ctx.inventory.select().id(Items.GRAPES_1987).shuffle().poll().click();

                // Prompt
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.widgets.component(270, 14).valid();
                    }
                }, 100, 30);
            }
        }

        // click on prompt component (should be same for all potions)
        if( ctx.widgets.component(270, 14).valid() ) {
            ctx.widgets.component(270, 14).click();

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().id(Items.GRAPES_1987).count() == 0;
                }
            }, 250, 80);
        }
    }
}
