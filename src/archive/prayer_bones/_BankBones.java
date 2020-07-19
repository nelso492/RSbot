package ngc.prayer_bones;

import resources.constants.Items;
import resources.actions.BankAction;
import resources.actions.InteractWithAllInventory;
import resources.action_config.BankConfig;
import resources.models.BaseAction;
import resources.tools.CommonActions;
import resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "Pray - Bank Bones", description = "Bury bones at a bank", properties = "client=4")
public class _BankBones extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Task List
    private List<BaseAction> taskList = new ArrayList<>();

    // GUI Tracking
    private String status;

    // XP Tracking
    private double startPrayerXp;
    private int boneId;
    private double boneExp;
    private double bonesToLevel;
    private int levelsGained;
    private int buryCount;
    private int prayerLevel;
    private int buryQuota;


    @Override
    public void start() {
        status = "Bones Selection";

        // Prompt for bone type
        String boneType = CommonActions.promptForSelection("Bones Type", "What type of bones?", new String[] {"Bones", "Big Bones", "Monkey Bones"});

        switch( boneType ) {
            case "Bones":
                boneExp = 5;
                boneId = Items.BONES_526;
                break;
            case "Big Bones":
                boneExp = 15;
                boneId = Items.BIG_BONES_532;
                break;
            default:
                boneExp = 0;
                boneId = 0;
                ctx.controller.stop();
                break;
        }

        // Prompt for amount to bury
        buryQuota = CommonActions.promptForQuantity("How many to bury before shutting down?");

        status = "Loading";

        // Bank Task
        BankConfig bankConfig = new BankConfig(-1, -1, boneId, 28, 0, 0, false, true, true);
        BankAction bankAction = new BankAction(ctx, "Banking", bankConfig);

        // Bury Bones
        InteractWithAllInventory interactWithAllInventory = new InteractWithAllInventory(ctx, "Burying", "Bury", boneId, 1000, 100);

        // Misc Configs
        ctx.properties.setProperty("randomevents.disable", "false"); //Ignore random events
        startPrayerXp = ctx.skills.experience(Constants.SKILLS_PRAYER);
        levelsGained = 0;
        buryCount = 0;
        prayerLevel = ctx.skills.realLevel(Constants.SKILLS_PRAYER);


        // Build Task List
        taskList.addAll(Arrays.asList(bankAction, interactWithAllInventory));


    }

    @Override
    public void poll() {
        for( BaseAction action : taskList ) {
            if( action.activate() ) {
                status = action.getStatus();
                action.execute();
            }
        }
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        String msg = messageEvent.text();

        if( msg.contains("advanced your") ) {
            levelsGained++;
            prayerLevel++;
        }

        if( msg.contains("dig a hole") ) {
            buryCount++;

            if( buryCount >= buryQuota ) {
                ctx.controller.stop();
            }
        }
    }

    @Override
    public void repaint(Graphics g) {
        calculateXP();

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
        g.drawString("Level  : " + (prayerLevel) + " [" + levelsGained + "]", GuiHelper.getStartX(), GuiHelper.getStartY(4));
        g.drawString("Buried : " + (buryCount) + " / " + buryQuota, GuiHelper.getStartX(), GuiHelper.getStartY(5));
        g.drawString("BTL    : " + (bonesToLevel), GuiHelper.getStartX(), GuiHelper.getStartY(6));
    }

    private void calculateXP() {
        bonesToLevel = Math.round(((ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_PRAYER) + 1) - (ctx.skills.experience(Constants.SKILLS_PRAYER))) / boneExp) + 1);
    }
}
