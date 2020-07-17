package ngc._resources.functions;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Game;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class AntibanActions {


    private static void openTab(ClientContext ctx, Game.Tab tab) {
        if (ctx.game.tab() != tab) {
            ctx.game.tab(tab);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.tab() == tab;
                }
            }, 250, 10);
        }
    }

    public static void sleepDelay(int length) {
        for (int i = 0; i <= length; i++) {
            sleep();
        }
    }

    public static void checkStat(ClientContext ctx) {

        openTab(ctx, Game.Tab.STATS);

        // Hover Random Stat
        sleep();
        ctx.input.move(Random.nextInt(550, 735), Random.nextInt(210, 460));

        sleepDelay(Random.nextInt(1, 3));
    }

    public static void checkStat(ClientContext ctx, int skillId) {
        openTab(ctx, Game.Tab.STATS);

        int skillsXOffset = 60;
        int skillsYOffset = 20;
        int skillsXStart = 550;
        int skillsYStart = 210;

        int col = 0;
        int row = 0;

        switch (skillId) {
            case Constants.SKILLS_AGILITY:
                col = 1;
                row = 1;
                break;
            case Constants.SKILLS_ATTACK:
                col = 0;
                row = 0;
                break;
            case Constants.SKILLS_DEFENSE:
                col = 0;
                row = 2;
                break;
            case Constants.SKILLS_STRENGTH:
                col = 0;
                row = 1;
                break;
            case Constants.SKILLS_RANGE:
                col = 0;
                row = 3;
                break;
            case Constants.SKILLS_PRAYER:
                col = 0;
                row = 4;
                break;
            case Constants.SKILLS_MAGIC:
                col = 0;
                row = 5;
                break;
            case Constants.SKILLS_RUNECRAFTING:
                col = 0;
                row = 6;
                break;
            case Constants.SKILLS_CONSTRUCTION:
                col = 0;
                row = 7;
                break;
            case Constants.SKILLS_HITPOINTS:
                col = 1;
                row = 0;
                break;
            case Constants.SKILLS_HERBLORE:
                col = 1;
                row = 2;
                break;
            case Constants.SKILLS_THIEVING:
                col = 1;
                row = 3;
                break;
            case Constants.SKILLS_CRAFTING:
                col = 1;
                row = 4;
                break;
            case Constants.SKILLS_FLETCHING:
                col = 1;
                row = 5;
                break;
            case Constants.SKILLS_SLAYER:
                col = 1;
                row = 6;
                break;
            case Constants.SKILLS_HUNTER:
                col = 1;
                row = 7;
                break;
            case Constants.SKILLS_MINING:
                col = 2;
                row = 0;
                break;
            case Constants.SKILLS_SMITHING:
                col = 2;
                row = 1;
                break;
            case Constants.SKILLS_FISHING:
                col = 2;
                row = 2;
                break;
            case Constants.SKILLS_COOKING:
                col = 2;
                row = 3;
                break;
            case Constants.SKILLS_FIREMAKING:
                col = 2;
                row = 4;
                break;
            case Constants.SKILLS_WOODCUTTING:
                col = 2;
                row = 5;
                break;
            case Constants.SKILLS_FARMING:
                col = 2;
                row = 6;
                break;
            default:
                col = 2;
                row = 7;
        }

        int x = skillsXStart + (col * skillsXOffset);
        int y = skillsYStart + (row * skillsYOffset);

        ctx.input.move(Random.nextInt(x, x + skillsXOffset), Random.nextInt(y, y + skillsYOffset));

        sleepDelay(Random.nextInt(1, 3));
    }

    public static void checkCombatLevel(ClientContext ctx) {
        openTab(ctx, Game.Tab.ATTACK);

        sleepDelay(Random.nextInt(1, 6));
    }

    public static void setRandomCameraAngle(ClientContext ctx) {
        ctx.camera.angle(Random.nextInt(0, 359));
    }

    public static void setRandomCameraPitch(ClientContext ctx) {
        ctx.camera.pitch(Random.nextInt(0, 99));
    }

    public static void jiggleMouse(ClientContext ctx) {
        int x = ctx.input.getLocation().x;
        int y = ctx.input.getLocation().y;

        int iterations = Random.nextInt(1, 4);

        for (int i = 0; i < iterations; i++) {
            ctx.input.move(new Point(x + Random.nextInt(-30, 30), y + Random.nextInt(-30, 30)));
        }
    }

    public static void doNothing() {
        sleepDelay(Random.nextInt(0, 10));
    }

    public static void moveMouseOffScreen(ClientContext ctx, boolean leftSide) {
        int x;
        if (leftSide) {
            x = -10;
        } else {
            // Right side (for inventory actions)
            x = 1000;
        }

        int y = Random.nextInt(0, 550);

        ctx.input.move(new Point(x, y));
        sleep();
        ctx.input.defocus();
        sleepDelay(Random.nextInt(3, 10));
    }

    public static void hoverRandomObject(ClientContext ctx) {
        var objects = ctx.objects.get(5);

        ctx.input.move(objects.get(Random.nextInt(0, objects.size())).nextPoint());

        sleepDelay(2);
    }

    public static void hoverRandomNPC(ClientContext ctx) {
        var npcs = ctx.npcs.get();

        ctx.input.move(npcs.get(Random.nextInt(0, npcs.size())).nextPoint());

        sleepDelay(2);
    }

    public static void toggleRun(ClientContext ctx) {
        if (ctx.movement.energyLevel() > 20 && !ctx.movement.running()) {
            ctx.movement.running(true);
        }
    }

    public static void resetCamera(ClientContext ctx) {
        ctx.widgets.widget(548).component(7).click();
        sleepDelay(1);
    }

    public static void toggleXPDrops(ClientContext ctx) {
        ctx.widgets.widget(160).component(1).click();
    }
}
