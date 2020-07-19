package scripts.slayer_simple;

import resources.action_config.CombatConfig;
import resources.models.BaseAction;

public class SlayerTaskConfig {

    // Protection/Healing Method
    private boolean usingGuthans;
    private boolean usingPrayer;
    private boolean usingBonesToPeaches;
    private int equipGuthansMinHealthPercentage;
    private int eatFoodMinHealthPercentage;
    private int bonesToPeachesBoneId;

    // Combat
    private BaseAction customCombatAction;
    private CombatConfig customCombatConfig;
    private boolean waitingForLootDrop;

    // Slayer Specific
    private double xpPerKill;
    private CombatConfig superiorCombatConfig;

    // Constructor
    public SlayerTaskConfig(){
        this.usingGuthans = false;
        this.usingPrayer = false;
        this.usingBonesToPeaches = false;
        this.bonesToPeachesBoneId = 0;
        this.equipGuthansMinHealthPercentage = 70;
        this.eatFoodMinHealthPercentage = 50;
        this.customCombatAction = null;
        this.waitingForLootDrop = false;
        this.xpPerKill = 0.0;
        this.customCombatConfig = null;
        this.superiorCombatConfig = null;
    }

    public boolean isUsingGuthans() {
        return usingGuthans;
    }

    public void setUsingGuthans(boolean usingGuthans) {
        this.usingGuthans = usingGuthans;
    }

    public boolean isUsingPrayer() {
        return usingPrayer;
    }

    public void setUsingPrayer(boolean usingPrayer) {
        this.usingPrayer = usingPrayer;
    }

    public boolean isUsingBonesToPeaches() {
        return usingBonesToPeaches;
    }

    public void setUsingBonesToPeaches(boolean usingBonesToPeaches) {
        this.usingBonesToPeaches = usingBonesToPeaches;
    }

    public int getEquipGuthansMinHealthPercentage() {
        return equipGuthansMinHealthPercentage;
    }

    public void setEquipGuthansMinHealthPercentage(int equipGuthansMinHealthPercentage) {
        this.equipGuthansMinHealthPercentage = equipGuthansMinHealthPercentage;
    }

    public int getEatFoodMinHealthPercentage() {
        return eatFoodMinHealthPercentage;
    }

    public void setEatFoodMinHealthPercentage(int eatFoodMinHealthPercentage) {
        this.eatFoodMinHealthPercentage = eatFoodMinHealthPercentage;
    }

    public BaseAction getCustomCombatAction() {
        return customCombatAction;
    }

    public void setCustomCombatAction(BaseAction customCombatAction) {
        this.customCombatAction = customCombatAction;
    }

    public boolean isWaitingForLootDrop() {
        return waitingForLootDrop;
    }

    public void setWaitingForLootDrop(boolean waitingForLootDrop) {
        this.waitingForLootDrop = waitingForLootDrop;
    }

    public double getXpPerKill() {
        return xpPerKill;
    }

    public void setXpPerKill(double xpPerKill) {
        this.xpPerKill = xpPerKill;
    }

    public CombatConfig getCustomCombatConfig() {
        return customCombatConfig;
    }

    public void setCustomCombatConfig(CombatConfig customCombatConfig) {
        this.customCombatConfig = customCombatConfig;
    }

    public CombatConfig getSuperiorCombatConfig() {
        return superiorCombatConfig;
    }

    public void setSuperiorCombatConfig(CombatConfig superiorCombatConfig) {
        this.superiorCombatConfig = superiorCombatConfig;
    }

    public int getBonesToPeachesBoneId() {
        return bonesToPeachesBoneId;
    }

    public void setBonesToPeachesBoneId(int bonesToPeachesBoneId) {
        this.bonesToPeachesBoneId = bonesToPeachesBoneId;
    }
}
