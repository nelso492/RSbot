package ngc.combat_lumby;


import resources.models.BaseAction;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Combat;
import org.powerbot.script.rt4.Constants;

public class SwitchCombatAction extends BaseAction<ClientContext> {
    public SwitchCombatAction(ClientContext ctx) {
        super(ctx, "Switch Style");
    }

    @Override
    public boolean activate() {
        boolean minAtkLevelReached = ctx.skills.levelAt(Constants.SKILLS_ATTACK) < 30;
        boolean minDefLevelReached = ctx.skills.levelAt(Constants.SKILLS_DEFENSE) < 30;
        boolean minStrLevelReached = ctx.skills.levelAt(Constants.SKILLS_STRENGTH) < 30;

        return minAtkLevelReached || minDefLevelReached || minStrLevelReached;
    }

    @Override
    public void execute() {
        Combat.Style current = ctx.combat.style();

        if( ctx.skills.levelAt(Constants.SKILLS_ATTACK) < 30 && !current.name().equalsIgnoreCase("accurate") ) {
            ctx.combat.style(Combat.Style.ACCURATE);
        } else if( ctx.skills.levelAt(Constants.SKILLS_STRENGTH) < 30 && !current.name().equalsIgnoreCase("aggressive") ) {
            ctx.combat.style(Combat.Style.AGGRESSIVE);
        } else if( ctx.skills.levelAt(Constants.SKILLS_DEFENSE) < 30 && !current.name().equalsIgnoreCase("defensive") ) {
            ctx.combat.style(Combat.Style.DEFENSIVE);
        }
    }
}
