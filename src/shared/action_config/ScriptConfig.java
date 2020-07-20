package shared.action_config;

import shared.actions.ToggleLevelUp;
import shared.actions.ToggleRunAction;
import shared.tools.CommonActions;
import shared.tools.GuiHelper;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;

/**
 * Script config to manage default behavior for all bots.
 * Runs default actions
 * Handles default GUI
 * Setup for event handling and xp tracking
 */
public class ScriptConfig {

    // Powerbot
    ClientContext ctx;

    // GUI
    private String status;
    private int levelsGained = 0;
    private final int[] trackedSkillIds;


    // DEFAULT TASKS
    private final ToggleLevelUp toggleLevelUp;
    private final ToggleRunAction toggleRunAction;

    // CONSTRUCTOR
    public ScriptConfig(ClientContext ctx, int[] skillIds) {
        this.ctx = ctx;

        // Default Properties
        this.status = "Configuration";
        this.ctx.properties.setProperty("randomevents.disable", "true");
        this.trackedSkillIds = skillIds;

        // Action Setup
        this.toggleLevelUp = new ToggleLevelUp(this.ctx);
        this.toggleRunAction = new ToggleRunAction(this.ctx, "Run", 30);
    }


    // Pre-Poll Actions
    public void prePollAction() {
        // Reset any errant inventory selection
        if (ctx.inventory.selectedItem().valid()) {
            ctx.inventory.selectedItem().click(); // unselect itself
        }

        this.checkMiscState();
    }

    public void paint(Graphics g, Long runtime) {
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
        g.setColor(GuiHelper.getTextColorWhite());
        g.drawRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);

        // Default Paint
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        g.drawString("Status : " + (this.status), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(runtime), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(2));
        g.drawString("Lvls Gained: " + (this.levelsGained), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(3));

        g.setColor(GuiHelper.getTextColorImportant());
        if (this.trackedSkillIds != null) {
            for (int i = 0; i < this.trackedSkillIds.length; i++) {
                g.drawString(CommonActions.getSkillName(this.trackedSkillIds[i]) + ": " + ctx.skills.level(this.trackedSkillIds[i]), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(i + 4));
            }
        }

        g.setColor(GuiHelper.getTextColorInformation());


    }

    // Check any non-phase STATE
    private void checkMiscState() {
        if (this.toggleLevelUp.activate()) {
            this.status = "Level Up";
            this.levelsGained++;
            this.toggleLevelUp.execute();
        }

        if (this.toggleRunAction.activate()) {
            this.status = "Running";
            this.toggleRunAction.execute();
        }
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
