package scripts.combat_minotaur.phases;

import org.powerbot.script.rt4.ClientContext;
import shared.actions.*;
import shared.constants.Items;
import shared.models.LootItem;
import shared.models.LootList;
import shared.templates.AbstractAction;
import shared.templates.PollingPhase;
import shared.tools.AntibanTools;
import shared.tools.CommonActions;

public class MinotaurCombatPhase extends PollingPhase {

    public MinotaurCombatPhase(ClientContext ctx, String name) {
        super(ctx, name);

        LootList lootList = new LootList();
        lootList.addLootItem(new LootItem(Items.RUNE_ESSENCE_NOTED_1437));
        lootList.addLootItem(new LootItem(Items.IRON_ARROW_884, 5));
        lootList.addLootItem(new LootItem(Items.RIGHT_SKULL_HALF_9007));

        AbstractAction minotairCombat = new CombatAction(ctx, "Attack", "Minotaur", -1, 20, lootList, ctx.combat.inMultiCombat(), null, 0);
        AbstractAction healAction = new HealAction(ctx, "Heal", CommonActions.allFoodIds(), 30);
        AbstractAction toggleRunAction = new ToggleRunAction(ctx, "Run", 20);
        AbstractAction waitForLoot = new WaitForCombatLoot(ctx);
        AbstractAction lootAction = new LootAction(ctx, "Loot", lootList, 15, true);
        AbstractAction equipArrows = new EquipArrows(ctx, Items.IRON_ARROW_884, AntibanTools.getRandomInRange(20, 200));

        this.addAction(healAction);
        this.addAction(lootAction);
        this.addAction(minotairCombat);
        this.addAction(waitForLoot);
        this.addAction(toggleRunAction);
        this.addAction(equipArrows);
    }

    @Override
    public boolean moveToNextPhase() {
        return false;
    }
}
