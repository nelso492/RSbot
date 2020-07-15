package ngc.nmz;


import ngc._resources.Items;
import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonFunctions;
import org.powerbot.script.rt4.ClientContext;

import static java.lang.Integer.parseInt;
import static org.powerbot.script.Condition.sleep;

public class UseAbsorptionPotion extends BaseAction<ClientContext> {

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
            CommonFunctions.usePotion(ctx, new int[] {Items.ABSORPTION_1_11737, Items.ABSORPTION_2_11736, Items.ABSORPTION_3_11735, Items.ABSORPTION_4_11734});
        }
    }
}
