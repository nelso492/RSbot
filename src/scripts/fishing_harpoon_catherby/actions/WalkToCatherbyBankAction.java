package scripts.fishing_harpoon_catherby.actions;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import shared.models.BaseAction;
import shared.tools.CommonActions;
import shared.tools.CommonAreas;

public class WalkToCatherbyBankAction extends BaseAction<ClientContext> {
    private static final Tile[] path = {new Tile(3088, 3226, 0), new Tile(3090, 3230, 0), new Tile(3090, 3234, 0), new Tile(3089, 3238, 0), new Tile(3087, 3242, 0), new Tile(3087, 3246, 0), new Tile(3091, 3247, 0), new Tile(3092, 3243, 0)};
    private final Area bank = CommonAreas.catherybyBank();

    public WalkToCatherbyBankAction(ClientContext ctx) {
        super(ctx, "To Bank");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull();
    }

    @Override
    public void execute() {
        path[path.length - 1] = bank.getRandomTile();
        CommonActions.traversePath(ctx, path);
    }
}