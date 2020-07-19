package ngc.herblore_herb_cleaner;

import resources.constants.Items;
import resources.actions.BankAction;
import resources.action_config.BankConfig;
import resources.models.BaseAction;
import resources.tools.CommonActions;
import resources.tools.GuiHelper;
import resources.actions.InteractWithAllInventory;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "HERB - Clean Herbs", description = "Clean herbs at a bank", properties = "client=4")
public class _CleanHerbs extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Task List
    private List<BaseAction> taskList = new ArrayList<>();

    // GUI Tracking
    private String status;

    // XP Tracking
    private int grimyHerbId;
    private double herbExp;
    private double herbsToLevel;
    private int levelsGained;
    private int cleanCount;
    private int herbloreLevel;
    private int cleanQuota;


    @Override
    public void start() {
        status = "Herb Selection";

        // Prompt for bone type
        String boneType = CommonActions.promptForSelection("Herb Type", "What type of herb?", new String[] {"Tarromin", "Harralander", "Ranarr", "Toadflax", "Irit leaf", "Avantoe", "Kwuarm", "Snapdragon", "Cadantine", "Lantadyme", "Dwarf weed", "Torstol"});

        switch( boneType ) {
            case "Tarromin":
                herbExp = 5;
                grimyHerbId = Items.GRIMY_TARROMIN_203;
                break;
            case "Harralander":
                herbExp = 6.3;
                grimyHerbId = Items.GRIMY_HARRALANDER_205;
                break;
            case "Ranarr":
                herbExp = 7.5;
                grimyHerbId = Items.GRIMY_RANARR_WEED_207;
                break;
            case "Toadflax":
                herbExp = 8;
                grimyHerbId = Items.GRIMY_TOADFLAX_3049;
                break;

        }

        // Prompt for amount to bury
        cleanQuota = CommonActions.promptForQuantity("How many to clean before shutting down?");

        status = "Loading";

        // Bank Task
        BankConfig bankConfig = new BankConfig(0, 0, grimyHerbId, 28, 0, 0, false, true, true);
        BankAction bankAction = new BankAction(ctx, "Banking", bankConfig);

        // Bury Bones
        InteractWithAllInventory interactWithAllInventory = new InteractWithAllInventory(ctx, "Cleaning", "Clean", grimyHerbId, 250, 75);

        // Misc Configs
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events
        levelsGained = 0;
        cleanCount = 0;
        herbloreLevel = ctx.skills.realLevel(Constants.SKILLS_HERBLORE);


        // Build Task List
        taskList.addAll(Arrays.asList(bankAction, interactWithAllInventory));


    }

    @Override
    public void poll() {
        for( BaseAction action : taskList ) {
            if(!ctx.controller.isSuspended() && !ctx.controller.isStopping()) {
                if( action.activate() ) {
                    status = action.getStatus();
                    action.execute();
                }
            }
        }
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        String msg = messageEvent.text();

        if( msg.contains("advanced your") ) {
            levelsGained++;
            herbloreLevel++;
        }

        if( msg.contains("You clean the") ) {
            cleanCount++;

            if( cleanCount >= cleanQuota ) {
                ctx.controller.stop();
            }

            calculateXP();
        }
    }

    @Override
    public void repaint(Graphics g) {

        /*Draw Background*/
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        /*Draw Data*/
        g.drawString("Status : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));

        /*Prayer*/
        g.drawString("Level  : " + (herbloreLevel) + " [" + levelsGained + "]", GuiHelper.getStartX(), GuiHelper.getStartY(4));
        g.drawString("Cleaned : " + (cleanCount) + " / " + cleanQuota, GuiHelper.getStartX(), GuiHelper.getStartY(5));
        g.drawString("HTL    : " + (herbsToLevel), GuiHelper.getStartX(), GuiHelper.getStartY(6));
    }

    private void calculateXP() {
        herbsToLevel = Math.round(((ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_HERBLORE) + 1) - (ctx.skills.experience(Constants.SKILLS_HERBLORE))) / herbExp) + 1);
    }
}
