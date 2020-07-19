package scripts.combat_alkharid_warriors;

import shared.models.BaseAction;
import shared.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Script.Manifest(name = "CMB - Al-Kharid Warrior Slayer LITE", description = "Fights warriors in the palace", properties = "client=4; topic=051515; author=Bowman")
public class _AlKharidWarriorSlayer extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private String status = "Starting";

    private int atkLevelsGained = 0;
    private int strLevelsGained = 0;
    private int defLevelsGained = 0;
    private int rngLevelsGained = 0;
    private GuiHelper h = new GuiHelper();

    @Override
    public void start() {
        log.info("Loading Task List");
        taskList.addAll(Arrays.asList(new AttackAction(ctx), new HealAction(ctx), new LootArrowsAction(ctx)));
    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {
            if( t.activate() ) {
                t.execute();
                status = t.getStatus() != null ? t.getStatus() : status;
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
        if( msg.contains("ranged level.") ) {
            rngLevelsGained += 1;
        }
    }

    @Override
    public void repaint(Graphics g) {
        int baseY = 305;
        int baseX = 225;

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 284, 520, 50);

        // Headers
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // App Specific Stats
        g.drawString("Status", 0, baseY);
        g.drawString("Time", 100, baseY);

        g.drawString("ATK", baseX, baseY);
        g.drawString("STR", baseX + 75, baseY);
        g.drawString("DEF", baseX + 150, baseY);
        g.drawString("RNG", baseX + 225, baseY);

        // App Data
        baseY += 20;

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(status, 0, baseY);
        g.drawString(h.getReadableRuntime(getRuntime()), 100, baseY);

        g.drawString(ctx.skills.level(Constants.SKILLS_ATTACK) + " +" + atkLevelsGained, baseX, baseY);
        g.drawString(ctx.skills.level(Constants.SKILLS_STRENGTH) + " +" + strLevelsGained, baseX + 75, baseY);
        g.drawString(ctx.skills.realLevel(Constants.SKILLS_DEFENSE) + " +" + defLevelsGained, baseX + 150, baseY);
        g.drawString(ctx.skills.realLevel(Constants.SKILLS_RANGE) + " +" + rngLevelsGained, baseX + 225, baseY);
    }
}
