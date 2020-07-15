package ngc.fishing_cage_harpoon;


import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;

import static org.powerbot.script.Condition.sleep;

public class HarpoonSpecial extends BaseAction<ClientContext> {
    public HarpoonSpecial(ClientContext ctx) {
        super(ctx, "Special Attack");
    }

    @Override
    public boolean activate() {
        return ctx.combat.specialPercentage() == 100;
    }

    @Override
    public void execute() {
        if( !ctx.combat.specialAttack() ) {
            ctx.game.tab(Game.Tab.ATTACK);
            sleep();
            ctx.combat.specialAttack(true);
            sleep();
            ctx.game.tab(Game.Tab.INVENTORY);
        }
    }
}

