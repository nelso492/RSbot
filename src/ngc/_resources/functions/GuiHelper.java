package ngc._resources.functions;

import java.awt.*;

public class GuiHelper {
    private static int baseX = 547;
    private static int baseY = 205;
    private static int widthX = 190;
    private static int widthY = 261;

    private static int dialogX = 2;
    private static int dialogY = 345 - 135;
    private static int dialogWidth = 514;
    private static int dialogHeight = 128;

    private static Color baseColor = new Color(0, 0, 0, 180);
    private static Color baseColorDark = new Color(0, 0, 0);

    private static Color textColorWhite = Color.WHITE;
    private static Color textColorImportant = Color.GREEN;
    private static Color textColorInformation = Color.CYAN;
    private static Color textColorWarning = Color.YELLOW;
    private static Color textColorDanger = Color.RED;

    public static String getReadableRuntime(Long runtimeMillis) {
        return ((runtimeMillis / 1000) / 60) + "m " + ((runtimeMillis / 1000) % 60) + "s";
    }

    public static Long getRuntimeMinutes(Long runtime) {
        return (runtime / 1000) / 60;
    }

    public static int getBaseX() {
        return baseX;
    }

    public static int getBaseY() {
        return baseY;
    }

    public static int getWidthX() {
        return widthX;
    }

    public static int getWidthY() {
        return widthY;
    }


    public static int getDialogX() {
        return dialogX;
    }

    public static int getDialogY() {
        return dialogY;
    }

    public static int getDialogWidth() {
        return dialogWidth;
    }

    public static int getDialogHeight() {
        return dialogHeight;
    }

    public static int getStartX() {
        return baseX + 5;
    }

    public static int getDialogStartX() {
        return dialogX + 5;
    }

    public static int getDialogMiddleX() {
        return (int)(dialogX + (.65 * dialogWidth));
    }

    public static int getStartY(int line) {
        return baseY + (25 * line);
    }

    public static int getDialogStartY(int line) {
        return dialogY + (20 * line);
    }

    public static Color getBaseColor() {
        return baseColor;
    }

    public static Color getBaseColorDark() {
        return baseColorDark;
    }

    public static Color getTextColorWhite() {
        return textColorWhite;
    }

    public static Color getTextColorImportant() {
        return textColorImportant;
    }

    public static Color getTextColorInformation() {
        return textColorInformation;
    }

    public static Color getTextColorWarning() {
        return textColorWarning;
    }

    public static Color getTextColorDanger() {
        return textColorDanger;
    }
}
