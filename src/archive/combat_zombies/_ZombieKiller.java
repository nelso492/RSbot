package scripts.combat_zombies;

import resources.actions.CombatAction;
import resources.actions.HealAction;
import resources.actions.LootAction;
import resources.actions.ToggleRunAction;
import resources.action_config.CombatConfig;
import resources.action_config.HealConfig;
import resources.action_config.LootConfig;
import resources.action_config.RunConfig;
import resources.models.BaseAction;
import resources.enums.ITEM_IDS;
import resources.enums.NPC_IDS;
import resources.tools.GuiHelper;
import resources.tools.RsLookup;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "CMB - SOS Zombies", description = "Kills Zombies & loots steel arrows", properties = "client=4; topic=051515; author=Bowman")
public class _ZombieKiller extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private GuiHelper helper = new GuiHelper();
    private RsLookup lookup = new RsLookup();
    //private HashMap<Integer, Integer> itemPrices = new HashMap<>();
    private int[] lootIds = {lookup.getId(ITEM_IDS.SteelArrow_886)};
    private int[] npcIds = {lookup.getId(NPC_IDS.Zombie_2501), lookup.getId(NPC_IDS.Zombie_2502), lookup.getId(NPC_IDS.Zombie_2503)};

    // Params
    private String status = "";
    private String foodName = "None";
    private boolean activeLoot;

    // To LVL
    private int zombieXP = 120;
    private int zombiesToLvl = 0;
    private int combatSkillUsed;
    private int killCount = 0;
    private int startXP = 0;
    private String skillName;
    private int levelsGained = 0;

    private int stackSize = 4;


    @Override
    public void start() {

        // Init
        status = "Configuring";
        activeLoot = true; // True to loot
        ctx.properties.setProperty("randomevents.disable", "false"); //Ignore random events

        // Open Equipment
        ctx.game.tab(Game.Tab.EQUIPMENT);

        // Check for weapon type
        Item wep = ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND);

        if( wep.valid() && wep.name().contains("bow") ) {
            combatSkillUsed = Constants.SKILLS_RANGE;
            skillName = "Range";
        } else {
            switch( ctx.combat.style().name().toLowerCase() ) {
                case "accurate":
                    combatSkillUsed = Constants.SKILLS_ATTACK;
                    skillName = "Attack";
                    break;
                case "aggressive":
                    combatSkillUsed = Constants.SKILLS_STRENGTH;
                    skillName = "Strength";
                    break;
                case "defensive":
                    combatSkillUsed = Constants.SKILLS_DEFENSE;
                    skillName = "Defense";
                    break;
            }
        }

        // Combat Config
        CombatConfig combatConfig = new CombatConfig(npcIds, "", lookup.getId(NPC_IDS.ZombieDeathAnimation_5569), 50, lootIds, false, stackSize, false);

        // Loot Action
        if( activeLoot ) {
            LootConfig lootConfig = new LootConfig(lootIds, stackSize, 10);
            taskList.add(new LootAction(ctx, "Looting", lootConfig));
        }

        // Heal Action
        Item food = ctx.inventory.select().first().poll();
        if( food.valid() ) {
            int[] foodIds = {food.id()};
            foodName = food.name();
            HealConfig healConfig = new HealConfig(foodIds, 50);
            taskList.add(new HealAction(ctx, "Healing", healConfig));
        }


        startXP = ctx.skills.experience(combatSkillUsed);
        ctx.game.tab(Game.Tab.INVENTORY);

        taskList.addAll(Arrays.asList(new ToggleRunAction(ctx, "", new RunConfig(30)), new CombatAction(ctx, "Combat", combatConfig)));
        status = "Started";


    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {
            // System.out.println("Check " + t.getStatus() + " " + t.activate());
            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }
        }


    }


    @Override
    public void repaint(Graphics g) {
        // Zombies to Level
        zombiesToLvl = ((ctx.skills.experienceAt(ctx.skills.level(combatSkillUsed) + 1) - (ctx.skills.experience(combatSkillUsed))) / zombieXP) + 1;
        killCount = (ctx.skills.experience(combatSkillUsed) - startXP) / zombieXP;

        g.setColor(helper.getBaseColor());
        g.fillRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("Status : " + (status), helper.getStartX(), helper.getStartY(1));
        g.drawString("Runtime: " + helper.getReadableRuntime(getRuntime()), helper.getStartX(), helper.getStartY(2));

        g.drawString("Food: " + (foodName), helper.getStartX(), helper.getStartY(4));

        g.drawString("CMB: " + skillName + " (" + ctx.skills.level(combatSkillUsed) + ")", helper.getStartX(), helper.getStartY(6));
        g.drawString("Gained: " + levelsGained, helper.getStartX(), helper.getStartY(7));
        g.drawString("Kills: " + killCount, helper.getStartX(), helper.getStartY(8));
        g.drawString("KTL: " + zombiesToLvl, helper.getStartX(), helper.getStartY(9));

    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

        if( msg.contains("advanced your") ) {
            levelsGained++;
        }

        if( msg.contains("no ammo left") ) {
            ctx.inventory.select().select(new Filter<Item>() {
                @Override
                public boolean accept(Item item) {
                    return item.name().contains("arrow");
                }
            }).poll().interact("Wield");
        }
    }
}


