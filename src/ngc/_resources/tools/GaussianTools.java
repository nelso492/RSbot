package ngc._resources.tools;

import org.powerbot.script.Random;

public class GaussianTools {

    public static boolean takeActionLikely() {
        return !takeActionUnlikely();
    }

    /**
     * 68% chance of execution
     *
     * @return is action taken
     */
    public static boolean takeActionNormal() {
        double base = baseGaussian();
        return base >= 35 && base <= 65;
    }

    /**
     * 15% Chance
     *
     * @return is action taken
     */
    public static boolean takeActionUnlikely() {
        return baseGaussian() < 25; // 15%
    }

    /**
     * 10% chance
     *
     * @return is action taken
     */
    public static boolean takeActionRarely() {
        return baseGaussian() > 20; // 10%
    }

    /**
     * 2.2% chance of execution
     *
     * @return is action taken
     */
    public static boolean takeActionNever() {
        return baseGaussian() < 20; // 2.2%
    }

    /**
     * Base Gaussian to compare against
     *
     * @return 0 - 100
     */
    private static double baseGaussian() {
        return Random.nextGaussian(0, 100, 50, 15);
    }
}
