package scripts.fight_caves_jad;

import shared.models.BaseAction;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.Prayer;

import static org.powerbot.script.Condition.sleep;

public class PrayerSwitcher extends BaseAction<ClientContext> {

    private int animation;
    private Prayer.Effect prayer;
    private int npcId;

    public PrayerSwitcher(ClientContext ctx, String status, int npcId, int npcAnimation, Prayer.Effect prayer) {
        super(ctx, status);
        this.npcId = npcId;
        this.animation = npcAnimation;
        this.prayer = prayer;
    }

    @Override
    public boolean activate() {

        Npc npc = ctx.npcs.select().id(npcId).nearest().poll();

        return npc.valid() && npc.animation() == animation;

    }


    @Override
    public void execute() {
        if( !ctx.prayer.prayerActive(prayer) ) {
            if( ctx.game.tab() != Game.Tab.PRAYER ) {
                ctx.game.tab(Game.Tab.PRAYER);
                sleep();
            }
            ctx.prayer.prayer(this.prayer, true);
        }
    }

}
