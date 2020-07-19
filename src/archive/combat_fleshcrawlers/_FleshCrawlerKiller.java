package scripts.combat_fleshcrawlers;

import resources.constants.Items;
import resources.actions.CombatAction;
import resources.actions.EquipArrows;
import resources.actions.HealAction;
import resources.actions.LootAction;
import resources.action_config.CombatConfig;
import resources.action_config.HealConfig;
import resources.models.BaseAction;
import resources.tools.CommonActions;
import resources.tools.GuiHelper;
import resources.models.LootItem;
import resources.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name = "CMB - SOS Flesh Crawlers", description = "Kills FleshCrawlers & loots scepter pieces", properties = "client=4; topic=051515; author=Bowman")
public class _FleshCrawlerKiller extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Tasks
    private List<BaseAction> taskList = new ArrayList<>();

    // GUI
    private String status;
    private int scepterPiecesFound;

    // XP
    private int combatSkill;
    private double xpToLevel;
    private double startXP;
    private double xpPerHour;
    private int currentLevel;
    private int lvlsGained;

    @Override
    public void start() {
        status = "Loading";

        // Init
        lvlsGained = 0;
        scepterPiecesFound = 0;

        // Loot
        LootList lootList = new LootList();

        // Combat Config
        combatSkill = CommonActions.promptForCombatStyle(ctx);
        if( combatSkill == Constants.SKILLS_RANGE ) {
            // Ranged
            int arrowId = CommonActions.promptForArrowType();

            if( arrowId > 0 ) {
                // Add arrows to loot
                lootList.addLootItem(new LootItem(arrowId, 4));
                taskList.add(new EquipArrows(ctx, arrowId, 120));
            }
        }

        // Pull XP for combat style
        startXP = ctx.skills.experience(combatSkill);
        currentLevel = ctx.skills.level(combatSkill);
        lvlsGained = 0;
        calculateXP();


        // Combat Config
        CombatConfig combatConfig = new CombatConfig("Flesh Crawler", 0, 50, lootList, ctx.combat.inMultiCombat());
        taskList.add(new CombatAction(ctx, "Combat", combatConfig));

        // Loot Config
        lootList.addLootItem(new LootItem(Items.BOTTOM_OF_SCEPTRE_9011));
        taskList.add(new LootAction(ctx, "Loot", lootList, 4));

        // Heal Config
        Item food = ctx.inventory.select().first().poll();
        if( food.valid() ) {
            int[] foodIds = {food.id()};

            HealConfig healConfig = new HealConfig(foodIds, 50);
            taskList.add(new HealAction(ctx, "Healing", healConfig));
        }

        // Start
        status = "Start";
    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {
            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }
        }

        scepterPiecesFound = ctx.inventory.select().id(Items.BOTTOM_OF_SCEPTRE_9011).count();
    }


    @Override
    public void repaint(Graphics g) {
        calculateXP();

        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        g.drawString("Status : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));
        g.drawString("Pieces : " + scepterPiecesFound, GuiHelper.getStartX(), GuiHelper.getStartY(3));
        g.drawString("Level  : " + (currentLevel), GuiHelper.getStartX(), GuiHelper.getStartY(5));
        g.drawString("Gained : " + (lvlsGained), GuiHelper.getStartX(), GuiHelper.getStartY(6));
        g.drawString("EXP TL : " + (xpToLevel), GuiHelper.getStartX(), GuiHelper.getStartY(8));
        g.drawString("XP /HR : " + (xpPerHour), GuiHelper.getStartX(), GuiHelper.getStartY(9));


    }

    private void calculateXP() {
        // Pull XP for combat style
        double currentXP = ctx.skills.experience(combatSkill);
        double xpGained = currentXP - startXP;
        xpToLevel = ctx.skills.experienceAt(ctx.skills.level(combatSkill) + 1) - currentXP;
        xpPerHour = Math.round((xpGained / ((getRuntime() / 1000) + 1)) * 3600);
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

        if( msg.contains("advanced your") ) {
            lvlsGained++;
        }
    }
}


