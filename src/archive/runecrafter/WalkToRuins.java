package scripts.runecrafter;


import shared.models.BaseAction;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import static org.powerbot.script.Condition.sleep;

public class WalkToRuins extends BaseAction<ClientContext> {

    private Tile[] path;
    private int ruinsId;
    private int ruinsCoord;

    public WalkToRuins(ClientContext ctx, Tile[] path, int ruinsId, int ruinsCoord) {
        super(ctx, "To Ruins");
        this.path = path;
        this.ruinsId = ruinsId;
        this.ruinsCoord = ruinsCoord;
    }

    @Override
    public boolean activate() {
        GameObject altar = ctx.objects.select().id(ruinsId).poll();
        return ctx.inventory.isFull() && !(altar.valid() && altar.inViewport()) && ctx.players.local().tile().y() < ruinsCoord;
    }

    @Override
    public void execute() {
        System.out.println(path);
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
