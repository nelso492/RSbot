package ngc.magic_high_alch;

import ngc._resources.Items;
import ngc._resources.actions.HighAlch;
import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.GuiHelper;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name = "Alchmate", description = "Prayer Switches against JAD", properties = "client=4")
public class _HighAlch extends PollingScript<ClientContext> implements PaintListener {

    // Task List
    private List<BaseAction> taskList = new ArrayList<>();

    // GUI Tracking
    private String status;

    @Override
    public void start() {

        int[] alchables = new int[ctx.inventory.size()];
        int index = 0;
        for( Item i : ctx.inventory.select() ) {
            if(i.id() != Items.NATURE_RUNE_561 && i.id() != Items.COINS_995){
                alchables[index] = i.id();
                index++;
            }
        }

        taskList.add(new HighAlch(ctx, "Alch", alchables, true,false));
    }

    @Override
    public void poll() {
        for( BaseAction action : taskList ) {
            if( action.activate() ) {
                status = action.getStatus();
                action.execute();
            }
        }

        if( ctx.combat.health() == 0 ) {
            ctx.controller.stop(); // quit on death
        }

        status = "Waiting";
    }

    @Override
    public void repaint(Graphics g) {

        /*Draw Background*/
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        /*Draw Data*/
        g.drawString("Status : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));
    }
}
