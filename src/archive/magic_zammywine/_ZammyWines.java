package ngc.magic_zammywine;

import ngc._resources.models.BaseAction;
import ngc._resources.enums.ITEM_IDS;
import ngc._resources.tools.GaussianProbability;
import ngc._resources.tools.GuiHelper;
import ngc._resources.tools.RsLookup;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Magic;
import org.powerbot.script.rt4.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "MM - Zammy Wines", description = "Telegrab Wine of Zamorak upstairs and tele to Fally to bank", properties = "client=4; topic=051515; author=Bowman")
public class _ZammyWines extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Constants
    private List<BaseAction> taskList = new ArrayList<>();
    private GuiHelper helper = new GuiHelper();
    private RsLookup lookup = new RsLookup();
    private GaussianProbability probability = new GaussianProbability();


    private String status = "";
    private int startWaterRuneCount, startLawCount;
    private int cycles = 0;
    private int winePrice, lawPrice, waterPrice;
    private int wineId = lookup.getId(ITEM_IDS.WineOfZamorak_245);
    private int waterRuneId = lookup.getId(ITEM_IDS.WaterRune_555);
    private int lawRuneId = lookup.getId(ITEM_IDS.LawRune_563);
    private int profit;
    private int missCount;
    private int tableId = 31581;



    @Override
    public void start() {

        // Init
        status = "Loading";
        startWaterRuneCount = ctx.inventory.select().id(waterRuneId).poll().stackSize();
        startLawCount = ctx.inventory.select().id(lawRuneId).poll().stackSize();
        missCount = 0;

        // Wine Price
        GeItem wine = new org.powerbot.script.rt4.GeItem(wineId);
        winePrice = wine.price;

        // Law Price
        GeItem law = new org.powerbot.script.rt4.GeItem(lawRuneId);
        lawPrice = law.price;

        // Water Price
        GeItem water = new org.powerbot.script.rt4.GeItem(waterRuneId);
        waterPrice = water.price;

        // Walk to Chaos Tower
        WalkBankToChaosTower walkAction = new WalkBankToChaosTower(ctx);

        // Climb Ladder
        ClimbLadder ladderAction = new ClimbLadder(ctx);

        // Telegrab Wines
        TelegrabWines telegrab = new TelegrabWines(ctx);

        // Inv full, teleport to falador
        FallyTeleport teleport = new FallyTeleport(ctx);

        // Walk to Bank
        WalkTeleToBank walkBank = new WalkTeleToBank(ctx);

        // Bank
        BankWines bankAction = new BankWines(ctx);

        taskList.addAll(Arrays.asList(walkAction, ladderAction, telegrab, teleport, walkBank, bankAction));

    }

    @Override
    public void poll() {
        for( BaseAction t : taskList ) {
            if( t.activate() ) {
                if( t.getStatus() != null ) {
                    status = t.getStatus();
                }
                t.execute();
            }

            if( ctx.game.floor() == 1 && !ctx.objects.select().id(wineId).poll().valid() ) {
                status = "Waiting";

                if(!ctx.magic.casting(Magic.Spell.TELEKINETIC_GRAB) && ctx.inventory.select().id(wineId).count() < 26){
                    primeCast();
                }
            }

            cycles = startWaterRuneCount - ctx.inventory.select().id(waterRuneId).poll().stackSize();
            int wineCount = cycles * 26;
            profit = ((wineCount * winePrice) - (((wineCount + cycles + missCount) * lawPrice) + (cycles * waterPrice)));

        }
    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

        if(msg.contains("too late")){
            missCount++;
        }


    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(helper.getBaseColor());
        g.fillRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Status : " + status, helper.getStartX(), helper.getStartY(1));
        g.drawString("Runtime: " + helper.getReadableRuntime(getRuntime()), helper.getStartX(), helper.getStartY(2));
        g.drawString("Trips: " + (cycles), helper.getStartX(), helper.getStartY(4));
        g.drawString("Wines: " + (cycles * 26), helper.getStartX(), helper.getStartY(5));
        g.drawString("Profit: " + (profit), helper.getStartX(), helper.getStartY(6));
    }

    private void primeCast() {
        // Competition raises click accuracy
        int otherPlayerCount = ctx.players.select().select(new Filter<Player>() {
            @Override
            public boolean accept(Player player) {
                return player.tile().distanceTo(ctx.players.local()) < 3;
            }
        }).size() - 1; // subtract to ignore self
        // chill for a sec
        //sleep(Random.nextInt(500, 9500));

        // Prime the spell
        ctx.magic.cast(Magic.Spell.TELEKINETIC_GRAB);

        // Adjust camera if necessary
        if( probability.takeActionRarely() ) {
            int r = Random.nextInt(-15, 15);
            ctx.camera.pitch(ctx.camera.pitch() + r);
            ctx.input.scroll(false);
            //sleep(Random.nextInt(800, 3200));
        }

        // Hover over area
        GameObject table = ctx.objects.select().id(tableId).poll();

        if( table.valid() ) {
            // Chill for another sec
            //sleep(Random.nextInt(500, 5000));
            int randomX = Random.nextInt(-8 + otherPlayerCount, 8 - otherPlayerCount);
            int randomY = Random.nextInt(-8 + otherPlayerCount, 8 - otherPlayerCount);
            Point p = new Point(table.centerPoint().x + randomX, table.centerPoint().y + randomY);

            ctx.input.move(p);
        }

        // Wiggle mouse if needed
        if( probability.takeActionNormal() ) {
            int randomX = Random.nextInt(-10, 10);
            int randomY = Random.nextInt(-10, 10);
            Point p = new Point(ctx.input.getLocation().x + randomX, ctx.input.getLocation().y + randomY);

            ctx.input.move(p);
        }
    }

}
