package scripts.combat_lumby;

import resources.models.BaseAction;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Script.Manifest(name = "CMB - Lumby Cow Fighter", description = "Fights da cows", properties = "client=4; topic=051515; author=Bowman")
public class _LumbyCowFighter extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private int atkLevelsGained = 0;
    private int strLevelsGained = 0;
    private int defLevelsGained = 0;
    private int hpLevelsGained = 0;

    private int atkLevel = 0;
    private int defLevel = 0;
    private int strLevel = 0;
    private int hpLevel = 0;


    @Override
    public void start() {
        log.info("Loading Tasks");
        taskList.addAll(Arrays.asList(new CombatAction(ctx), new HealAction(ctx), new LootAction(ctx)));
        log.info("Tasks Loaded");

        atkLevel = ctx.skills.level(Constants.SKILLS_ATTACK);
        defLevel = ctx.skills.level(Constants.SKILLS_DEFENSE);
        strLevel = ctx.skills.level(Constants.SKILLS_STRENGTH);
        hpLevel = ctx.skills.level(Constants.SKILLS_HITPOINTS);
    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {
            if( t.activate() ) {
                t.execute();
            }
        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();
        if( msg.contains("attack level.") ) {
            atkLevelsGained += 1;
        }
        if( msg.contains("strength level.") ) {
            strLevelsGained += 1;
        }
        if( msg.contains("defence level.") ) {
            defLevelsGained += 1;
        }
        if( msg.contains("hitpoint level.") ) {
            hpLevelsGained += 1;
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 16));
        g.drawString("ATK : " + atkLevel + " (" + atkLevelsGained + ")", 50, 50);
        g.drawString("STR : " + strLevel + " (" + strLevelsGained + ")", 50, 75);
        g.drawString("DEF : " + defLevel + " (" + defLevelsGained + ")", 50, 100);
        g.drawString("HP  : " + hpLevel + " (" + hpLevelsGained + ")", 50, 125);
    }
}
