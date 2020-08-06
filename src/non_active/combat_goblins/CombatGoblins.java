package disabled.combat_goblins;


import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import shared.action_config.ScriptConfig;
import shared.actions.CombatAction;
import shared.actions.HealAction;
import shared.actions.LootAction;
import shared.constants.Items;
import shared.models.LootItem;
import shared.models.LootList;
import shared.templates.AbstractAction;
import shared.templates.PollingPhase;
import shared.tools.AntibanTools;
import shared.tools.GaussianTools;

import java.awt.*;

@Script.Manifest(name = "combat_Goblins", description = "Kills in lumby. loots stackables and coins", properties = "client=4; topic=051515; author=Bowman")
public class CombatGoblins extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Config
    private ScriptConfig scriptConfig = new ScriptConfig(ctx, null);
    private boolean antiBanInProgress;
    private PollingPhase currentPhase;


    //region Antiban
    private long lastBreakTimestamp;
    private int nextBreakInMinutes;
    //endregion


    //region start
    @Override
    public void start() {

        // Tracked Skills
        int[] skills = new int[4];
        skills[0] = Constants.SKILLS_HITPOINTS;
        skills[1] = Constants.SKILLS_ATTACK;
        skills[2] = Constants.SKILLS_STRENGTH;
        skills[3] = Constants.SKILLS_DEFENSE;

        this.scriptConfig = new ScriptConfig(ctx, skills);
        this.scriptConfig.setStatus("Config");

        // Antiban
        lastBreakTimestamp = 0L;
        nextBreakInMinutes = AntibanTools.getRandomInRange(3, 7);

        // Starting Location
        this.currentPhase = new PollingPhase(ctx, "Combat") {
            @Override
            public boolean moveToNextPhase() {
                return false;
            }
        };

        LootList lootList = new LootList();
        lootList.addLootItem(new LootItem(Items.EARTH_RUNE_557, 2));
        lootList.addLootItem(new LootItem(Items.WATER_RUNE_555, 2));

        AbstractAction combatAction = new CombatAction(ctx, "Attack", "Goblin",-1,20,lootList,ctx.combat.inMultiCombat(),null,0);
        AbstractAction healAction = new HealAction(ctx, "Heal", new int[] {Items.TROUT_333}, 30);

        this.currentPhase.addAction(new LootAction(ctx, "Looting", lootList, 3, false));
        this.currentPhase.addAction(combatAction);
        this.currentPhase.addAction(healAction);

        // Initial Status
        this.scriptConfig.setStatus(this.currentPhase.getStatus());

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

            if (GaussianTools.takeActionNever()) {
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
                        AntibanTools.checkStat(ctx, Constants.SKILLS_ATTACK);
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
                switch (AntibanTools.getRandomInRange(0, 4)) {
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

        if (!this.antiBanInProgress && this.currentPhase != null) {
            this.scriptConfig.setStatus(this.currentPhase.getStatus());

            this.currentPhase.activate();
        }
    }
    //endregion

    //region messaged
    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text();

        if(msg.contains("just advanced your")){
            this.scriptConfig.incrementLevelsGained();
        }
    }
    //endregion

    //region repaint
    @Override
    public void repaint(Graphics g) {
        this.scriptConfig.paint(g, getRuntime());
    }
    //endregion
}