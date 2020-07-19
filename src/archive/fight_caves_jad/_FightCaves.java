package ngc.fight_caves_jad;

import ngc._resources.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.ArrayList;

@Script.Manifest(name = "Fight Caves", description = "Prayer Switches against JAD", properties = "client=4")
public class _FightCaves extends PollingScript<ClientContext> implements PaintListener, MessageListener {

    // GUI Tracking
    private String roundPrayerPriority;
    private String nextRoundPrayer;
    private int currentWave;
    private String currentWaveMonsters;
    private String nextWaveMonsters;
    private ArrayList<String> waveMonsters;
    private String activePrayer;

    // Tasks
    private Npc jad;
    private Npc targetNpc;
    private int rangeAnimation;
    private int magicAnimation;

    @Override
    public void start() {

        rangeAnimation = 2652;
        magicAnimation = 2656;

        activePrayer = "None";

        // Build Waves List
        waveMonsters = new ArrayList<>();
        waveMonsters.add("22");
        waveMonsters.add("22, 22");
        waveMonsters.add("45");
        waveMonsters.add("45, 22");
        waveMonsters.add("45, 22, 22");
        waveMonsters.add("45, 45");
        waveMonsters.add("90");
        waveMonsters.add("90, 22");
        waveMonsters.add("90, 22, 22");
        waveMonsters.add("90, 45");
        waveMonsters.add("90, 45, 22");
        waveMonsters.add("90, 45, 22, 22");
        waveMonsters.add("90, 45, 45");
        waveMonsters.add("90, 90");
        waveMonsters.add("180");
        waveMonsters.add("180, 22");
        waveMonsters.add("180, 22, 22");
        waveMonsters.add("180, 45");
        waveMonsters.add("180, 45, 22");
        waveMonsters.add("180, 45, 22, 22");
        waveMonsters.add("180, 45, 45");
        waveMonsters.add("180, 90");
        waveMonsters.add("180, 90, 22");
        waveMonsters.add("180, 90, 22, 22");
        waveMonsters.add("180, 90, 45");
        waveMonsters.add("180, 90, 45, 22");
        waveMonsters.add("180, 90, 45, 22, 22");
        waveMonsters.add("180, 90, 45, 45");
        waveMonsters.add("180, 90, 90");
        waveMonsters.add("180, 180");
        waveMonsters.add("360");
        waveMonsters.add("360, 22");
        waveMonsters.add("360, 22, 22");
        waveMonsters.add("360, 45");
        waveMonsters.add("360, 45, 22");
        waveMonsters.add("360, 45, 22, 22");
        waveMonsters.add("360, 45, 45");
        waveMonsters.add("360, 90");
        waveMonsters.add("360, 90, 22");
        waveMonsters.add("360, 90, 22, 22");
        waveMonsters.add("360, 90, 45");
        waveMonsters.add("360, 90, 45, 22");
        waveMonsters.add("360, 90, 45, 22, 22");
        waveMonsters.add("360, 90, 45, 45");
        waveMonsters.add("360, 90, 90");
        waveMonsters.add("360, 180");
        waveMonsters.add("360, 180, 22");
        waveMonsters.add("360, 180, 22, 22");
        waveMonsters.add("360, 180, 45");
        waveMonsters.add("360, 180, 45, 22");
        waveMonsters.add("360, 180, 45, 22, 22");
        waveMonsters.add("360, 180, 45, 45");
        waveMonsters.add("360, 180, 90");
        waveMonsters.add("360, 180, 90, 22");
        waveMonsters.add("360, 180, 90, 22, 22");
        waveMonsters.add("360, 180, 90, 45");
        waveMonsters.add("360, 180, 90, 45, 22");
        waveMonsters.add("360, 180, 90, 45, 22, 22");
        waveMonsters.add("360, 180, 90, 45, 45");
        waveMonsters.add("360, 180, 90, 90");
        waveMonsters.add("360, 180, 180");
        waveMonsters.add("360, 360");
        waveMonsters.add("720");

    }

    @Override
    public void poll() {
        jad = ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.name().equals("TzTok-Jad");
            }
        }).nearest().poll();

        switch( checkState() ) {
            case PROTECT_MAGIC:
                if( ctx.game.tab() != Game.Tab.PRAYER )
                    ctx.game.tab(Game.Tab.PRAYER);
                ctx.prayer.prayer(Prayer.Effect.PROTECT_FROM_MAGIC, true);
                break;
            case PROTECT_RANGE:
                if( ctx.game.tab() != Game.Tab.PRAYER )
                    ctx.game.tab(Game.Tab.PRAYER);
                ctx.prayer.prayer(Prayer.Effect.PROTECT_FROM_MISSILES, true);
                break;
        }


        targetNpc = (Npc) ctx.players.local().interacting();
    }

    public State checkState() {
        if( jad.valid() ) {
            if( jad.animation() == magicAnimation ) {
                return State.PROTECT_MAGIC;
            }

            if( jad.animation() == rangeAnimation ) {
                return State.PROTECT_RANGE;
            }

        }

        return State.WAIT;
    }

    public enum State {
        PROTECT_MAGIC, PROTECT_RANGE, WAIT
    }

    @Override
    public void repaint(Graphics g) {

        /*Draw Background*/
        g.setColor(GuiHelper.getBaseColorDark());
        g.fillRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getDialogX(), GuiHelper.getDialogY(), GuiHelper.getDialogWidth(), GuiHelper.getDialogHeight(), 4, 4);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        /*Draw Data*/
        g.setColor(Color.WHITE);
        g.drawString(targetNpc.name() + " | " + targetNpc.healthPercent() + "% | " + targetNpc.animation(), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(1));
        g.drawString(activePrayer, GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(2));

        g.setColor(Color.MAGENTA);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        g.drawString("RNG: " + ctx.skills.level(Constants.SKILLS_RANGE) + "/" + ctx.skills.realLevel(Constants.SKILLS_RANGE), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(3));
        g.drawString("DEF: " + ctx.skills.level(Constants.SKILLS_DEFENSE) + "/" + ctx.skills.realLevel(Constants.SKILLS_DEFENSE), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(4));
        g.drawString("PRY: " + ctx.skills.level(Constants.SKILLS_PRAYER) + "/" + ctx.skills.realLevel(Constants.SKILLS_PRAYER), GuiHelper.getDialogMiddleX(), GuiHelper.getDialogStartY(5));

        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        g.setColor(Color.WHITE);
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(1));
        g.drawString("Wave   : " + currentWave, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(2));

        g.setColor(Color.CYAN);
        g.drawString("Prayer : " + roundPrayerPriority, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(4));
        g.drawString("Enemies: " + currentWaveMonsters, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(5));

        g.setColor(Color.GREEN);
        g.drawString("Next Prayer : " + nextRoundPrayer, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(7));
        g.drawString("Next Enemies: " + nextWaveMonsters, GuiHelper.getDialogStartX(), GuiHelper.getDialogStartY(8));
    }

    @Override
    public void messaged(MessageEvent event) {
        String msg = event.text().replace("<col=ef1020>", "").replace("</col>", "");
        System.out.println("~ " + msg);
        if( msg.contains("Wave") && currentWave < 63 ) {
            currentWave = Integer.parseInt(msg.split(": ")[1]);
            updateEnemies();
        }
    }

    private void updateEnemies() {
        currentWaveMonsters = waveMonsters.get(currentWave - 1);
        nextWaveMonsters = waveMonsters.get(currentWave);

        roundPrayerPriority = getRoundPrayer(currentWaveMonsters);
        nextRoundPrayer = getRoundPrayer(nextWaveMonsters);
    }

    private String getRoundPrayer(String monsterString) {
        if( monsterString.contains("360") ) {
            return "Magic";
        } else if( monsterString.contains("90") ) {
            return "Range";
        } else {
            return "None";
        }
    }
}
