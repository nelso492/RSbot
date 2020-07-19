package resources.actions;


import resources.models.BaseAction;
import resources.tools.CommonActions;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;

/**
 * Equip arrows, bolts, and other projectiles
 */
public class EquipArrows extends BaseAction<ClientContext> {
    private int arrowId;
    private int equipQuantity;

    public EquipArrows(ClientContext ctx, int arrowId, int equipQuantity) {
        super(ctx, "Equip Arrows");
        this.arrowId = arrowId;
        this.equipQuantity = equipQuantity;
    }

    public EquipArrows(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(arrowId).poll().stackSize() >= equipQuantity;
    }

    @Override
    public void execute() {
        CommonActions.openTab(ctx, Game.Tab.INVENTORY);

        ctx.inventory.select().id(arrowId).poll().interact("Wield");
    }

    public int getArrowId() {
        return arrowId;
    }

    public void setArrowId(int arrowId) {
        this.arrowId = arrowId;
    }

    public int getEquipQuantity() {
        return equipQuantity;
    }

    public void setEquipQuantity(int equipQuantity) {
        this.equipQuantity = equipQuantity;
    }
}

