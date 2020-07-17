package ngc._resources.actions;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.AntibanActions;
import ngc._resources.functions.CommonFunctions;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;

import java.util.concurrent.Callable;

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
        CommonFunctions.openTab(ctx, Game.Tab.INVENTORY);

        ctx.inventory.select().id(arrowId).poll().interact("Wield");
    }
}

