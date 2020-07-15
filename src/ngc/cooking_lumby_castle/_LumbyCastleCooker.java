package ngc.cooking_lumby_castle;

import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "COOK - Lumby Castle", description = "Cooking and Banking in Lumby Castle", properties = "client=4; topic=051515; author=Bowman")
public class _LumbyCastleCooker extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private int cookCount = 0;
    private int cooksToLvl = 0;
    private int lvlsGained = 0;
    private double avgExpPerCook = 0;
    private int startExp;

    private String cookingName;
    private String status = "";


    @Override
    public void start() {
        status = "Searching";

        Item i = ctx.inventory.select().select(new Filter<Item>() {
            @Override
            public boolean accept(Item item) {
                return item.name().toLowerCase().contains("raw");
            }
        }).first().poll();

        if( i.valid() ) {
            status = " found " + i.name();
            cookingName = i.name().substring(4, 5).toUpperCase() + i.name().substring(5);
            taskList.addAll(Arrays.asList(new BankAction(ctx, i.id()), new WalkBankToStairs(ctx, i.id()), new NavigateStairs(ctx, i.id()), new CookFood(ctx, i.id()), new WalkKitchenToStairs(ctx, i.id())));
        } else {
            ctx.controller.stop();
        }

        status = "Calculating";
        startExp = ctx.skills.experience(Constants.SKILLS_COOKING);
    }

    @Override
    public void poll() {
        cooksToLvl = (int) ((ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_COOKING) + 1) - (ctx.skills.experience(Constants.SKILLS_COOKING))) / avgExpPerCook);

        for( BaseAction t : taskList ) {
            if( t.activate() ) {
                if( t.getStatus() != null ) {
                    status = t.getStatus();
                }
                t.execute();
            }
        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();
        if( msg.contains("cook") || msg.contains("roast") ) {
            cookCount++;
            status = "Cooking";
        }
        if( msg.contains("advanced your cooking level.") ) {
            lvlsGained++;
        }

        if( msg.contains("can't reach that") && ctx.game.floor() == 2 ) {
            ctx.movement.step(ctx.bank.nearest());
            sleep(1000);
        }

        // Calculate avg xp per item
        avgExpPerCook = (ctx.skills.experience(Constants.SKILLS_COOKING) - startExp) / (cookCount);
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        g.drawString("Status : " + (status), GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Runtime: " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));

        g.drawString("Item : " + cookingName, GuiHelper.getStartX(), GuiHelper.getStartY(4));
        g.drawString("Level: " + ctx.skills.level(Constants.SKILLS_COOKING) + " [" + lvlsGained + " ]", GuiHelper.getStartX(), GuiHelper.getStartY(5));
        g.drawString("CTL  : " + cooksToLvl, GuiHelper.getStartX(), GuiHelper.getStartY(6));
        g.drawString("Count: " + cookCount, GuiHelper.getStartX(), GuiHelper.getStartY(7));
    }

}
