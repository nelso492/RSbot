package shared.actions;


import shared.templates.AbstractAction;
import shared.tools.CommonActions;
import shared.tools.GaussianTools;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

/**
 * Potion use for skill boosting.
 */
public class UsePotion extends AbstractAction<ClientContext> {
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

    public UsePotion(ClientContext ctx, String status) {
        super(ctx, status);
    }

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
                && ctx.inventory.select().id(this.potionIds).count() > 0
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
                drinkPotion = !GaussianTools.takeActionLikely();
            }else{
                drinkPotion = true;
            }
        }

        if(drinkPotion) {
            ctx.game.tab(Game.Tab.INVENTORY);
            sleep(Random.nextInt(500, 1200));
            CommonActions.usePotion(ctx, potionIds);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.skills.level(targetSkill) > skillLevelMax;
                }
            }, 1000, 5);
        }
    }


    public int[] getPotionIds() {
        return potionIds;
    }

    public void setPotionIds(int[] potionIds) {
        this.potionIds = potionIds;
    }

    public int getTargetSkill() {
        return targetSkill;
    }

    public void setTargetSkill(int targetSkill) {
        this.targetSkill = targetSkill;
    }

    public int getSkillLevelMin() {
        return skillLevelMin;
    }

    public void setSkillLevelMin(int skillLevelMin) {
        this.skillLevelMin = skillLevelMin;
    }

    public int getSkillLevelMax() {
        return skillLevelMax;
    }

    public void setSkillLevelMax(int skillLevelMax) {
        this.skillLevelMax = skillLevelMax;
    }

    public boolean isSkillDropping() {
        return isSkillDropping;
    }

    public void setSkillDropping(boolean skillDropping) {
        isSkillDropping = skillDropping;
    }

    public int getSkillRange() {
        return skillRange;
    }

    public void setSkillRange(int skillRange) {
        this.skillRange = skillRange;
    }

    public int getSkill75() {
        return skill75;
    }

    public void setSkill75(int skill75) {
        this.skill75 = skill75;
    }

    public int getSkill50() {
        return skill50;
    }

    public void setSkill50(int skill50) {
        this.skill50 = skill50;
    }

    public int getSkill25() {
        return skill25;
    }

    public void setSkill25(int skill25) {
        this.skill25 = skill25;
    }

    public int getLastSkillLevel() {
        return lastSkillLevel;
    }

    public void setLastSkillLevel(int lastSkillLevel) {
        this.lastSkillLevel = lastSkillLevel;
    }

    public int getSkillDistance() {
        return skillDistance;
    }

    public void setSkillDistance(int skillDistance) {
        this.skillDistance = skillDistance;
    }
}
