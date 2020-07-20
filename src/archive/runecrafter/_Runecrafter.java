package scripts.runecrafter;

import shared.action_config.RunecraftConfig;
import shared.templates.AbstractAction;
import shared.tools.CommonAreas;
import shared.tools.GuiHelper;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "Runecrafter", description = "Rune crafting with banking", properties = "client=4; topic=051515; author=Bowman")
public class _Runecrafter extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Helpers
    private GuiHelper helper = new GuiHelper();
    private CommonAreas areas = new CommonAreas();

    // Tasks
    private List<AbstractAction> taskList = new ArrayList<>();

    // Tracking
    private String status = "";
    private int lvlsGained;
    private int xpToLevel;
    private String elementName;
    private int xpGained;
    private int xpStart;
    private int trips;
    private int essId = 1436;
    private RunecraftConfig rcConfig;

    // Earth
    private Tile[] pathToEarthAltar = {new Tile(3253, 3422, 0), new Tile(3254, 3426, 0), new Tile(3258, 3428, 0), new Tile(3262, 3428, 0), new Tile(3266, 3428, 0), new Tile(3270, 3428, 0), new Tile(3274, 3428, 0), new Tile(3278, 3428, 0), new Tile(3282, 3429, 0), new Tile(3284, 3433, 0), new Tile(3284, 3437, 0), new Tile(3284, 3441, 0), new Tile(3284, 3445, 0), new Tile(3284, 3449, 0), new Tile(3285, 3453, 0), new Tile(3285, 3457, 0), new Tile(3288, 3460, 0), new Tile(3291, 3464, 0), new Tile(3295, 3465, 0), new Tile(3299, 3468, 0), new Tile(3303, 3470, 0)};
    private Tile[] pathFromEarthAltar = {new Tile(3302, 3477, 0), new Tile(3298, 3476, 0), new Tile(3297, 3472, 0), new Tile(3297, 3468, 0), new Tile(3296, 3464, 0), new Tile(3292, 3462, 0), new Tile(3289, 3459, 0), new Tile(3289, 3455, 0), new Tile(3289, 3451, 0), new Tile(3286, 3448, 0), new Tile(3283, 3445, 0), new Tile(3280, 3442, 0), new Tile(3281, 3438, 0), new Tile(3277, 3439, 0), new Tile(3275, 3435, 0), new Tile(3275, 3431, 0), new Tile(3271, 3430, 0), new Tile(3267, 3430, 0), new Tile(3263, 3428, 0), new Tile(3259, 3428, 0), new Tile(3255, 3427, 0), new Tile(3254, 3423, 0)};
    private RunecraftConfig earthConfig = new RunecraftConfig(pathToEarthAltar, pathFromEarthAltar, 14405, 14900, 4000, areas.getVarrockBankEast());

    // Body
    private Tile[] pathToBodyRuins = {new Tile(3092, 3491, 0), new Tile(3088, 3490, 0), new Tile(3085, 3487, 0), new Tile(3082, 3484, 0), new Tile(3081, 3480, 0), new Tile(3080, 3476, 0), new Tile(3080, 3472, 0), new Tile(3080, 3468, 0), new Tile(3084, 3467, 0), new Tile(3086, 3463, 0), new Tile(3082, 3462, 0), new Tile(3079, 3459, 0), new Tile(3075, 3457, 0), new Tile(3072, 3454, 0), new Tile(3071, 3450, 0), new Tile(3071, 3446, 0), new Tile(3068, 3442, 0), new Tile(3064, 3439, 0), new Tile(3060, 3438, 0), new Tile(3061, 3442, 0), new Tile(3061, 3438, 0), new Tile(3057, 3440, 0)};
    private Tile[] pathFromBodyRuins = {new Tile(3050, 3442, 0), new Tile(3054, 3443, 0), new Tile(3055, 3447, 0), new Tile(3058, 3450, 0), new Tile(3062, 3450, 0), new Tile(3066, 3448, 0), new Tile(3069, 3445, 0), new Tile(3072, 3449, 0), new Tile(3075, 3452, 0), new Tile(3076, 3456, 0), new Tile(3079, 3460, 0), new Tile(3083, 3461, 0), new Tile(3086, 3464, 0), new Tile(3082, 3465, 0), new Tile(3080, 3469, 0), new Tile(3080, 3473, 0), new Tile(3080, 3477, 0), new Tile(3080, 3481, 0), new Tile(3084, 3484, 0), new Tile(3087, 3487, 0), new Tile(3090, 3490, 0)};
    private RunecraftConfig bodyConfig = new RunecraftConfig(pathToBodyRuins, pathFromBodyRuins, 14409, 14902, 4000, areas.edgevilleBankSouth());


    @Override
    public void start() {
        // Init
        status = "Equipment Check";
        lvlsGained = 0;
        trips=0;
        elementName = "";

        // Initial XP Calculation
        xpStart = ctx.skills.experience(Constants.SKILLS_RUNECRAFTING);
        xpToLevel = ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_RUNECRAFTING) + 1) - ctx.skills.experience(Constants.SKILLS_RUNECRAFTING);

        // Open Equipment
        ctx.game.tab(Game.Tab.EQUIPMENT);

        // Check for tiara
        Item tiara = ctx.equipment.itemAt(Equipment.Slot.HEAD);

        if( tiara.valid() && tiara.name().contains("tiara") ) {
            elementName = tiara.name().split(" ")[0];
        } else {
            ctx.controller.stop();
        }

        switch( elementName.toLowerCase() ) {
            case "earth":
                rcConfig = earthConfig;
                break;
            case "body":
                rcConfig = bodyConfig;
                break;
            default:
                ctx.controller.stop();
        }

        // Open Inventory
        sleep(1000);
        ctx.game.tab(Game.Tab.INVENTORY);

        // Walk to Ruins
        WalkToRuins walkToRuins = new WalkToRuins(ctx, rcConfig.getPathToAltar(), rcConfig.getRuinsId(), rcConfig.getRuinsCoord());

        // Enter Ruins
        EnterRuins enterRuins = new EnterRuins(ctx, rcConfig.getRuinsId(), rcConfig.getRuinsCoord());

        // Craft Runes
        CraftRunes craftRunes = new CraftRunes(ctx, rcConfig.getAltarId(), rcConfig.getRuinsCoord());

        // Exit Ruins
        ExitRuins exitRuins = new ExitRuins(ctx, rcConfig.getRuinsCoord());

        // Walk to Bank
        WalkFromRuins walkFromRuins = new WalkFromRuins(ctx, rcConfig.getPathFromAltar(), rcConfig.getBankArea(), rcConfig.getRuinsCoord());

        // Bank
        BankRunes bankRunes = new BankRunes(ctx, essId);

        // Add Tasks
        taskList.addAll(Arrays.asList(walkToRuins, enterRuins, craftRunes, exitRuins, walkFromRuins, bankRunes));

        // Extra Configs
        ctx.properties.setProperty("randomevents.disable", "false"); //Ignore random events

        status = "Start";

    }

    @Override
    public void poll() {
        for( AbstractAction t : taskList ) {
            if( t.activate() ) {
                status = t.getStatus();
                t.execute();
            }
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(helper.getBaseColor());
        g.fillRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(helper.getBaseX(), helper.getBaseY(), helper.getWidthX(), helper.getWidthY(), 4, 4);

        g.setFont(new Font("Arial", Font.BOLD, 14));

        g.drawString("Status : " + (status), helper.getStartX(), helper.getStartY(1));
        g.drawString("Runtime: " + helper.getReadableRuntime(getRuntime()), helper.getStartX(), helper.getStartY(2));
        g.drawString("Element: " + elementName, helper.getStartX(), helper.getStartY(3));
        g.drawString("RC Lvl : " + ctx.skills.level(Constants.SKILLS_RUNECRAFTING), helper.getStartX(), helper.getStartY(5));
        g.drawString("Lvl Up : " + lvlsGained, helper.getStartX(), helper.getStartY(6));
        g.drawString("XP UP  : " + xpGained, helper.getStartX(), helper.getStartY(8));
        g.drawString("Nxt Lvl: " + xpToLevel, helper.getStartX(), helper.getStartY(9));
        g.drawString("Trips: " + trips, helper.getStartX(), helper.getStartY(10));

    }

    @Override
    public void messaged(MessageEvent e) {
        String msg = e.text().toLowerCase();

        if( msg.contains("bind the temple's power") ) {
            trips++;
            xpGained = ctx.skills.experience(Constants.SKILLS_RUNECRAFTING) - xpStart;
            xpToLevel = ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_RUNECRAFTING) + 1) - ctx.skills.experience(Constants.SKILLS_RUNECRAFTING);
        }

        if( msg.contains("runecraft level") ) {
            lvlsGained++;
        }
    }
}

