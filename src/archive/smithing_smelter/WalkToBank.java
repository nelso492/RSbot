package scripts.smithing_smelter;


import shared.constants.Items;
import shared.templates.AbstractAction;
import shared.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToBank extends AbstractAction<ClientContext> {
    private Area bankArea = CommonAreas.edgevilleBankNorth();
    private int silverId = Items.SILVER_ORE_442;

    public static final Tile[] path = {new Tile(3109, 3499, 0), new Tile(3105, 3499, 0), new Tile(3101, 3499, 0), new Tile(3097, 3496, 0)};

    public WalkToBank(ClientContext ctx) {
        super(ctx, "To Bank");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(silverId).count() == 0 && !ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        ctx.movement.step(bankArea.getRandomTile());
        sleep(Random.nextInt(500, 2000));
    }
}
