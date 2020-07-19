package scripts.combat_minotaur;

import shared.actions.CombatAction;
import shared.actions.HealAction;
import shared.actions.LootAction;
import shared.actions.ToggleRunAction;
import shared.action_config.CombatConfig;
import shared.action_config.HealConfig;
import shared.action_config.LootConfig;
import shared.action_config.RunConfig;
import shared.models.BaseAction;
import shared.enums.ITEM_IDS;
import shared.enums.NPC_IDS;
import shared.tools.GuiHelper;
import shared.tools.RsLookup;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Script.Manifest(name = "CMB - Minotaur Killer", description = "Kills Minotaurs & loots arrows, ess, half skulls.", properties = "client=4; topic=051515; author=Bowman")
public class _MinotaurKiller extends PollingScript<ClientContext> implements PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private GuiHelper helper = new GuiHelper();
    private RsLookup lookup = new RsLookup();
    private HashMap<Integer, Integer> itemPrices = new HashMap<>();

    // Params
    private String status = "";
    //private int profit = 0;
    //private long lastProfitUpdate = 0;


    @Override
    public void start() {
        status = "Configuring";
        // NPC & Loot
        int[] NPCS = {lookup.getId(NPC_IDS.Minotaur_2483)};
        int[] lootList = {lookup.getId(ITEM_IDS.UncutSapphire_1084),lookup.getId(ITEM_IDS.RightSkullHalf_9007),lookup.getId(ITEM_IDS.IronArrow_884),lookup.getId(ITEM_IDS.RuneEssNoted_1437)};


        // Combat Config
        CombatConfig combatConfig = new CombatConfig(NPCS, "", lookup.getId(NPC_IDS.MinotaurDeath_4265), 25, lootList, false, 1);

        // Loot Config
        LootConfig lootConfig = new LootConfig(lootList, 1, 10);

        // Heal Config
        Item food = ctx.inventory.select().first().poll();
        if( food.valid() ) {
            int[] foodIds = {food.id()};
            HealConfig healConfig = new HealConfig(foodIds, 50);
            taskList.add(new HealAction(ctx, "Healing", healConfig));
        }

        // Add Actions
        taskList.addAll(Arrays.asList(new ToggleRunAction(ctx, "", new RunConfig(30)), new CombatAction(ctx, "Combat", combatConfig), new LootAction(ctx, "Looting", lootConfig)));
        status = "Started";



    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {
            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }
        }

        // Update Profits every 60 seconds
        /*if( (getRuntime() - lastProfitUpdate > 60000 || lastProfitUpdate == 0) && itemPrices.size() > 0 ) {
            profit = 0;

            for( Item i : ctx.inventory.items() ) {
*//*                System.out.println("Items: " + i.id());
                System.out.println("PRICE: " + itemPrices.get(i.id()));*//*
                if( itemPrices.get(i.id()) != null ) {
                    profit += i.stackSize() * itemPrices.get(i.id());
                }
            }

            lastProfitUpdate = getRuntime();
        }*/
    }


    @Override
    public void repaint(Graphics g) {
        g.setColor(helper.getBaseColor());
        g.fillRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("Status : " + (status), helper.getStartX(), helper.getStartY(1));
        g.drawString("Runtime: " + helper.getReadableRuntime(getRuntime()), helper.getStartX(), helper.getStartY(2));
        // g.drawString("Profit: " + ((profit == 0) ? "calculating" : profit), helper.getStartX(), helper.getStartY(4));

    }

}


