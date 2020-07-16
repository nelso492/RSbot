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

    private int skillsXOffset = 60;
    private int skillsYOffset = 20;
    private int skillsXStart = 550;
    private int skillsYStart = 210;

    private void openTab(ClientContext ctx, Game.Tab tab) {
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

    private void sleepDelay(int length) {
        for (int i = 0; i <= length; i++) {
            sleep();
        }
    }

    public void checkStat(ClientContext ctx) {

        this.openTab(ctx, Game.Tab.STATS);

        // Hover Random Stat
        sleep();
        ctx.input.move(Random.nextInt(550, 735), Random.nextInt(210, 460));

        sleepDelay(Random.nextInt(1, 3));
    }

    public void checkStat(ClientContext ctx, int skillId) {
        this.openTab(ctx, Game.Tab.STATS);

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

        int x = this.skillsXStart + (col * this.skillsXOffset);
        int y = this.skillsYStart + (row * this.skillsYOffset);

        ctx.input.move(Random.nextInt(x, x + this.skillsXOffset), Random.nextInt(y, y + this.skillsYOffset));

        sleepDelay(Random.nextInt(1, 3));
    }

    public void checkCombatLevel(ClientContext ctx) {
        this.openTab(ctx, Game.Tab.ATTACK);

        sleepDelay(Random.nextInt(1, 6));
    }

    public void setRandomCameraAngle(ClientContext ctx) {
        ctx.camera.angle(Random.nextInt(0, 359));
    }

    public void setRandomCameraPitch(ClientContext ctx) {
        ctx.camera.pitch(Random.nextInt(0, 99));
    }

    public void jiggleMouse(ClientContext ctx) {
        int x = ctx.input.getLocation().x;
        int y = ctx.input.getLocation().y;

        int iterations = Random.nextInt(1, 4);

        for (int i = 0; i < iterations; i++) {
            ctx.input.move(new Point(x + Random.nextInt(-30, 30), y + Random.nextInt(-30, 30)));
        }
    }

    public void moveMouseOffScreen(ClientContext ctx, boolean leftSide) {
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
        this.sleepDelay(Random.nextInt(3, 10));
    }

    public void hoverRandomObject(ClientContext ctx) {
        var objects = ctx.objects.get(5);

        ctx.input.move(objects.get(Random.nextInt(0, objects.size())).nextPoint());

        sleepDelay(2);
    }

    public void hoverRandomNPC(ClientContext ctx) {
        var npcs = ctx.npcs.get();

        ctx.input.move(npcs.get(Random.nextInt(0, npcs.size())).nextPoint());

        sleepDelay(2);
    }

    public void toggleRun(ClientContext ctx) {
        if (ctx.movement.energyLevel() > 20 && !ctx.movement.running()) {
            ctx.movement.running(true);
        }
    }

    public void resetCamera(ClientContext ctx) {
        ctx.widgets.widget(548).component(7).click();
        sleepDelay(1);
    }

    public void toggleXPDrops(ClientContext ctx) {
        ctx.widgets.widget(160).component(1).click();
    }
}
