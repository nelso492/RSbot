package ngc._resources.actions._config;

public class HealConfig {
    private int[] foodIds;
    private int healthPercent;

    public HealConfig(int[] foodIds, int healthPercent) {
        this.foodIds = foodIds;
        this.healthPercent = healthPercent;
    }

    public int[] getFoodIds() {
        return foodIds;
    }

    public int getHeathPercent() {
        return healthPercent;
    }
}
