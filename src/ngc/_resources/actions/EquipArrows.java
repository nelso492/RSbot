package ngc._resources.actions;


import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;

public class EquipArrows extends BaseAction<ClientContext> {
    private int arrowId;
    private int equipQuantity;

    public EquipArrows(ClientContext ctx, int arrowId, int equipQuantity) {
        super(ctx, "Equip Arrows");
        this.arrowId = arrowId;
        this.equipQuantity = equipQuantity;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(arrowId).poll().stackSize() >= equipQuantity;
    }

    @Override
    public void execute() {
        ctx.game.tab(Game.Tab.INVENTORY);
        ctx.inventory.select().id(arrowId).poll().interact("Wield");
    }
}

