package scripts.misc_warriors_guild_animator;

import resources.constants.Items;
import resources.actions.BankAction;
import resources.actions.HealAction;
import resources.actions.ToggleLevelUp;
import resources.actions.ToggleRunAction;
import resources.action_config.BankConfig;
import resources.action_config.HealConfig;
import resources.action_config.RunConfig;
import resources.models.BaseAction;
import resources.tools.CommonActions;
import resources.tools.GuiHelper;
import resources.models.LootItem;
import resources.models.LootList;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name = "Misc - Warrior Guild Armour Animator", description = "Racks up tokens and banks for food", properties = "client=4; topic=051515; author=Bowman")
public class _WarriorGuildAnimator extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // App Tracking
    private List<BaseAction> taskList = new ArrayList<>();
    private String status = "";

    private int helm, body, legs;

    @Override
    public void start() {
        // Config
        startConfigs();

        // Build Loot List
        buildLootList();

        // Heal Action
        Item food = ctx.inventory.select().first().poll();

        System.out.println(food.name() + " " + food.id());

        int[] foodIds = {food.id()};
        HealConfig healConfig = new HealConfig(foodIds, 60);
        taskList.add(new HealAction(ctx, "Healing", healConfig));

        // Toggle Run
        taskList.add(new ToggleRunAction(ctx, "Toggle Run", new RunConfig(30)));

        // Level Up
        taskList.add(new ToggleLevelUp(ctx));

        // animate
        taskList.add(new AnimateArmour(ctx, helm, body, legs, food.id()));

        // leave animator
        taskList.add(new NavigateAnimatorDoor(ctx, helm, body, legs, food.id()));

        // walk to bank
        taskList.add(new WalkToBank(ctx, helm, body, legs, food.id()));

        // bank
        BankConfig config = new BankConfig(-1, -1, food.id(), 28, -1, 0, false, true, false);
        taskList.add(new BankAction(ctx, "Banking", config));

        // walk to animator
        taskList.add(new WalkToAnimator(ctx, helm, body, legs, food.id()));

        status = "Started";


    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {

            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }

            status = "Waiting";
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

    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

    }


    private void buildLootList() {
        // Loot
        LootList lootList = new LootList();

        lootList.addLootItem(new LootItem(helm));
        lootList.addLootItem(new LootItem(body));
        lootList.addLootItem(new LootItem(legs));
        lootList.addLootItem(new LootItem(Items.WARRIOR_GUILD_TOKEN_8851));

        taskList.add(new SimpleLoot(ctx, "Loot", lootList));
    }

    private void startConfigs() {
        status = "Configuring";
        ctx.properties.setProperty("randomevents.disable", "true"); //Ignore random events

        String armorType = CommonActions.promptForSelection("Armor Type", "Choose Armor Type", new String[] {"Mithril", "Adamant", "Rune"});

        switch( armorType ) {
            case "Mithril":
                helm = Items.MITHRIL_FULL_HELM_1159;
                body = Items.MITHRIL_PLATEBODY_1121;
                legs = Items.MITHRIL_PLATELEGS_1071;
                break;
            case "Adamant":
                helm = Items.ADAMANT_FULL_HELM_1161;
                body = Items.ADAMANT_PLATEBODY_1123;
                legs = Items.ADAMANT_PLATELEGS_1073;
                break;
            case "Rune":
                helm = Items.RUNE_FULL_HELM_1163;
                body = Items.RUNE_PLATEBODY_1127;
                legs = Items.RUNE_PLATELEGS_1079;
                break;
        }
    }
}


