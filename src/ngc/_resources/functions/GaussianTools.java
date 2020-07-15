package ngc._resources.functions;

import org.powerbot.script.Random;

public class GaussianTools {
    public static boolean takeActionAlways() {
        return Math.abs(baseTenGaussian()) <= 30; // 99.7&
    }

    public static boolean takeActionDefinite() {
        return Math.abs(baseTenGaussian()) <= 20; // 97%
    }

    public static boolean takeActionLikely() {
        double n = baseTenGaussian();
        return (n >= -10) && (n <= 20); // 81%
    }

    public static boolean takeActionNormal() {
        return Math.abs(baseTenGaussian()) <= 10; // 68%
    }

    public static boolean takeActionUnlikely() {
        return baseTenGaussian() > 10; // 15%
    }

    public static boolean takeActionRarely() {
        return Math.abs(baseTenGaussian()) > 20; // 5%
    }

    public static boolean takeActionNever() {
        return baseTenGaussian() > 20; // 2.2%
    }

    private static double baseTenGaussian() {
        return Random.nextGaussian() * 10;
    }

    public static int getRandomGaussian(int median, int deviation) {
        return (int) (Random.nextGaussian() * median) + deviation;
    }
}
