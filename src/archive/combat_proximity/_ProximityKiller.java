package scripts.combat_proximity;

import resources.actions.*;
import resources.action_config.CombatConfig;
import resources.action_config.HealConfig;
import resources.action_config.LootConfig;
import resources.action_config.RunConfig;
import resources.models.BaseAction;
import resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "CMB - Proximity Killer", description = "Kills NPC's near player w/out looting", properties = "client=4; topic=051515; author=Bowman")
public class _ProximityKiller extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private GuiHelper helper = new GuiHelper();

    // Params
    private String status = "";
    private int combatSkill = 0;
    private double xpToLevel = 0;
    private int startXP = 0;
    private double xpPerHour = 0;
    private int currentLevel;
    private int lvlsGained;
    private String foodName = "None";
    private long timeToLevel;

    private int minLevel;
    private int[] loot = new int[1];


    @Override
    public void start() {
        status = "Configuring";
        ctx.properties.setProperty("randomevents.disable", "false"); //Ignore random events

        // Min Level Settings
        minLevel = 2; // Need GUI to configure this

        // Combat Style

        // Open Equipment
        ctx.game.tab(Game.Tab.EQUIPMENT);

        // Check for weapon type
        Item wep = ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND);

        if( wep.valid() && wep.name().contains("bow") ) {
            combatSkill = Constants.SKILLS_RANGE;
            Item arrow = ctx.equipment.itemAt(Equipment.Slot.QUIVER);

            if( arrow.valid() ) {
                // Add arrows to loot
                loot[0] = arrow.id();
                LootConfig lootConfig = new LootConfig(loot, 4, 8);
                taskList.add(new LootAction(ctx, "Arrows", lootConfig));
            } else {
                status = "no arrows";
                ctx.controller.suspend();
            }
        } else {
            switch( ctx.combat.style().name().toLowerCase() ) {
                case "accurate":
                    combatSkill = Constants.SKILLS_ATTACK;
                    break;
                case "aggressive":
                    combatSkill = Constants.SKILLS_STRENGTH;
                    break;
                case "defensive":
                    combatSkill = Constants.SKILLS_DEFENSE;
                    break;
            }
        }

        // Pull XP for combat style
        startXP = ctx.skills.experience(combatSkill);
        currentLevel = ctx.skills.level(combatSkill);
        lvlsGained = 0;
        calculateXP();

        // Combat Config
        int[] npcIds = new int[10];
        int index = 0;
        for( Npc npc : ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.inViewport() && npc.combatLevel() > minLevel;
            }
        }) ) {
            if( index < 10 ) {
                npcIds[index] = npc.id();
                index++;
            }
        }
        CombatConfig combatConfig = new CombatConfig(npcIds, "", 0, 50, loot, ctx.combat.inMultiCombat(), 4);


        // Heal Action
        Item food = ctx.inventory.select().first().poll();
        if( food.valid() ) {
            int[] foodIds = {food.id()};
            foodName = food.name();
            HealConfig healConfig = new HealConfig(foodIds, 50);
            taskList.add(new HealAction(ctx, "Healing", healConfig));
        }

        taskList.addAll(Arrays.asList(new ToggleRunAction(ctx, "", new RunConfig(30)), new CombatAction(ctx, "Combat", combatConfig), new SkullScepterTeleport(ctx, "Teleporting", food.id())));
        status = "Started";

        ctx.game.tab(Game.Tab.INVENTORY);


    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {
            calculateXP();

            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }
        }

        // If food is empty and

    }


    @Override
    public void repaint(Graphics g) {

        g.setColor(helper.getBaseColor());
        g.fillRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));

        g.drawString("Status : " + (status), helper.getStartX(), helper.getStartY(1));
        g.drawString("Runtime: " + helper.getReadableRuntime(getRuntime()), helper.getStartX(), helper.getStartY(2));
        g.drawString("Food   : " + foodName, helper.getStartX(), helper.getStartY(3));
        g.drawString("Level  : " + (currentLevel), helper.getStartX(), helper.getStartY(5));
        g.drawString("Gained : " + (lvlsGained), helper.getStartX(), helper.getStartY(6));
        g.drawString("XP/HR : " + (xpPerHour), helper.getStartX(), helper.getStartY(8));
      //  g.drawString("EXP TL : " + (xpToLevel), helper.getStartX(), helper.getStartY(9));
        g.drawString("TTL   : " + helper.getReadableRuntime(timeToLevel), helper.getStartX(), helper.getStartY(9));

    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

        if( msg.contains("advanced your") ) {
            lvlsGained++;
        }
    }

    private void calculateXP() {
        // Pull XP for combat style
        int currentXP = ctx.skills.experience(combatSkill);
        double xpGained = currentXP - startXP;
        xpToLevel = ctx.skills.experienceAt(ctx.skills.level(combatSkill) + 1) - currentXP;
        long millisPerHour = (1000 * 60 * 60);
        xpPerHour = ((millisPerHour / getRuntime()) * xpGained);
        timeToLevel = (long)((xpToLevel / xpPerHour) * 3600000);
    }
}


