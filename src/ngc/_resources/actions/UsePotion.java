package ngc._resources.actions;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonFunctions;
import ngc._resources.functions.GaussianTools;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class UsePotion extends BaseAction<ClientContext> {
    private int[] potionIds;
    private int targetSkill;
    private int skillLevelMin;
    private int skillLevelMax;
    private boolean isSkillDropping;
    private int skillRange;
    private int skill75;
    private int skill50;
    private int skill25;
    private int lastSkillLevel;
    private int skillDistance;

    public UsePotion(ClientContext ctx, String status, int[] potionIds, int targetSkill, int skillLevelMin, int skillLevelMax, boolean isSkillDropping) {
        super(ctx, status);

        this.potionIds = potionIds;
        this.targetSkill = targetSkill;
        this.skillLevelMin = skillLevelMin;
        this.skillLevelMax = skillLevelMax;
        this.isSkillDropping = isSkillDropping;
        this.lastSkillLevel = 0;

        this.skillRange = this.skillLevelMax - this.skillLevelMin;
        this.skill75 = (int) (this.skillRange * .75) + this.skillLevelMin;
        this.skill50 = (int) (this.skillRange * .5) + this.skillLevelMin;
        this.skill25 = (int) (this.skillRange * .25) + this.skillLevelMin;
    }

    @Override
    public boolean activate() {
        // Calculate distance to bounds
        if( isSkillDropping ) {
            skillDistance = ctx.skills.level(targetSkill) - skillLevelMin;
        } else {
            skillDistance = skillLevelMax - ctx.skills.level(targetSkill);
        }

        return (ctx.skills.level(targetSkill) != lastSkillLevel)
                && ( skillDistance < 0 || (ctx.skills.level(targetSkill) >= skillLevelMin && ctx.skills.level(targetSkill) <= skillLevelMax));
    }

    @Override
    public void execute() {
        lastSkillLevel = ctx.skills.level(targetSkill);
        boolean drinkPotion;

        // check no range given
        if( skillRange == 0 || skillDistance <= 0 ) {
            drinkPotion = true;
        } else {
            // Check distance to bound
            if( skillDistance >= skill75){
                drinkPotion = GaussianTools.takeActionRarely();
            }else if( skillDistance >= skill50){
                drinkPotion = GaussianTools.takeActionUnlikely();
            }else if( skillDistance >= skill25){
                drinkPotion = GaussianTools.takeActionLikely();
            }else{
                drinkPotion = true;
            }
        }

        if(drinkPotion) {
            ctx.game.tab(Game.Tab.INVENTORY);
            sleep(Random.nextInt(500, 1200));
            CommonFunctions.usePotion(ctx, potionIds);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.skills.level(targetSkill) > skillLevelMax;
                }
            }, 100, 10);
        }
    }
}
