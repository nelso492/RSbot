package scripts.combat_ogress;

import resources.constants.Npcs;
import resources.action_config.CombatConfig;
import resources.models.BaseAction;
import resources.models.LootItem;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class SafeSpotOgress extends BaseAction<ClientContext> {
    private CombatConfig config;
    private int[] targetNpcIds;
    private int maxDistanceToLoot;

    public SafeSpotOgress(ClientContext ctx, String status, CombatConfig _config, int maxDistanceToLoot) {
        super(ctx, status);
        config = _config;
        this.targetNpcIds = new int[] {Npcs.OGRESS_WARRIOR_7989, Npcs.OGRESS_WARRIOR_7990};
        this.maxDistanceToLoot = maxDistanceToLoot;
    }

    @Override
    public boolean activate() {
        boolean hasMinHealth = ctx.combat.healthPercent() >= config.getMinHealthPercent();
        boolean interacting = ctx.players.local().interacting().valid();
        boolean lootNearby = ctx.groundItems.select().id(config.getLoot().allItemIds()).select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem groundItem) {
                LootItem i = config.getLoot().getLootItemById(groundItem.id());
                if( i != null ) {
                    // Item can be picked up, stack is above min size, item in viewport
                    return (!ctx.inventory.isFull() || (groundItem.stackable() && ctx.inventory.select().id(groundItem.id()).count() > 0))
                            && groundItem.stackSize() >= i.getMinStackSize()
                            && ctx.inventory.select().id(groundItem.id()).count() < i.getMaxInventoryCount()
                            && groundItem.inViewport() && groundItem.tile().distanceTo(ctx.players.local()) <= maxDistanceToLoot;
                } else {
                    // Ground Item not in our loot list
                    return false;
                }
            }
        }).peek().valid();

        boolean validTarget = ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.valid() &&
                        (npc.id() == targetNpcIds[0] || npc.id() == targetNpcIds[1]) &&
                        (npc.tile().x() < 2015 || (npc.tile().y() <= 9000 && npc.tile().y() > 8988)) &&
                        npc.healthPercent() > 0 &&
                        (!npc.healthBarVisible() || (!npc.interacting().valid() || npc.interacting().name().equals(ctx.players.local().name())));
            }
        }).nearest().peek().valid();

        return !ctx.players.local().inMotion() && hasMinHealth && !interacting && !lootNearby && validTarget && ctx.game.floor() == 1;
    }

    @Override
    public void execute() {
        // Target Npc
        Npc npc = ctx.npcs.nearest().poll();

        // Attack and Move
        if( npc.valid() && npc.inViewport() ) {
            npc.interact("Attack", npc.name());

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() != -1;
                }
            }, 100, 20);
        } else {
            ctx.camera.turnTo(npc);
            sleep();

            if( !npc.inViewport() ) {
                ctx.camera.pitch(Random.nextInt(0,30));
                sleep();
            }
        }
    }
}
