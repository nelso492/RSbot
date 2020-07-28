package scripts.nmz;


import org.powerbot.script.rt4.ClientContext;
import shared.constants.Items;
import shared.templates.AbstractAction;
import shared.tools.CommonActions;

import static java.lang.Integer.parseInt;

public class UseAbsorptionPotion extends AbstractAction<ClientContext> {

    public UseAbsorptionPotion(ClientContext ctx) {
        super(ctx, "Absorption");
    }

    @Override
    public boolean activate() {
        return parseInt(ctx.widgets.component(202, 3, 5).text()) < 100;
    }

    @Override
    public void execute() {
        while( parseInt(ctx.widgets.component(202, 3, 5).text()) < 250 ) {
            CommonActions.usePotion(ctx, new int[] {Items.ABSORPTION_1_11737, Items.ABSORPTION_2_11736, Items.ABSORPTION_3_11735, Items.ABSORPTION_4_11734});
        }
    }
}
