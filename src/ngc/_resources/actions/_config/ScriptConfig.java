package ngc._resources.actions._config;

import ngc._resources.actions.ToggleLevelUp;
import ngc._resources.functions.CommonFunctions;
import ngc._resources.functions.GuiHelper;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;

public class ScriptConfig {

    // Powerbot
    ClientContext ctx;

    // GUI
    private String status;
    private int levelsGained = 0;
    private int[] trackedSkillIds;


    // DEFAULT TASKS
    private ToggleLevelUp toggleLevelUp;

    // CONSTRUCTOR
    public ScriptConfig(ClientContext ctx, int[] skillIds) {
        this.ctx = ctx;

        // Default Properties
        this.status = "Configuration";
        this.ctx.properties.setProperty("randomevents.disable", "true");
        this.trackedSkillIds = skillIds;

        // Action Setup
        this.toggleLevelUp = new ToggleLevelUp(this.ctx);
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
        // Default Paint
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        g.drawString("Status : " + (this.status), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(runtime), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(2));
        g.drawString("Lvls Gained: " + (this.levelsGained), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(3));

        g.setColor(GuiHelper.getTextColorImportant());
        if(this.trackedSkillIds != null) {
            for (int i = 0; i < this.trackedSkillIds.length; i++) {
                g.drawString(CommonFunctions.getCombatStyleName(this.trackedSkillIds[i]) + ": " + ctx.skills.level(this.trackedSkillIds[i]), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(i + 1));
            }
        }
    }

    // Check any non-phase STATE
    private void checkMiscState() {
        if (this.toggleLevelUp.activate()) {
            this.status = "Level Up";
            this.levelsGained++;
            this.toggleLevelUp.execute();
        }
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
