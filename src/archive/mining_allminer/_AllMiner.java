package ngc.mining_allminer;

import resources.constants.GameObjects;
import resources.constants.Items;
import resources.models.BaseAction;
import resources.tools.CommonActions;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class _AllMiner extends PollingScript<ClientContext> implements PaintListener, MessageListener {

    public List<BaseAction> tasks = new ArrayList<>();
    public String status;

    // Ore Config
    public int oreId;
    public int rockId;
    public int[] allOreIds;

    // Xp Config
    public double expGained;
    public int currentLevel;
    public int levelsGained;

    // GUI Counters
    public int oreMined;


    @Override
    public void start() {

        // Startup Config
        startup();

        // Location
        loadLocationConfig();

        // Go!
    }

    @Override
    public void poll() {
        for( BaseAction action : tasks ) {
            if( action.activate() ) {
                status = action.getStatus();
                action.execute();
            }
        }

    }

    @Override
    public void messaged(MessageEvent messageEvent) {

    }

    @Override
    public void repaint(Graphics graphics) {

    }

    /*Start Private Methods*/
    public void startup() {
        status = "Startup";

        currentLevel = ctx.skills.realLevel(Constants.SKILLS_MINING);
        expGained = 0;
        levelsGained = 0;

    }

    public void loadLocationConfig() {
        String location = CommonActions.promptForSelection("Location", "Location?", "Barbarian Village", "Mining Guild");

        if( location.equalsIgnoreCase("barbarian village") ) {
            barbVillageConfig(new String[] {"Tin", "Coal"});
        } else {
            miningGuildConfig(new String[] {"Silver", "Gold", "Coal", "Mithril", "Adamant"});
        }
    }


    public void barbVillageConfig(String[] oreNames) {
        orePrompt(oreNames);

        // Load Barb Tasks

    }

    public void miningGuildConfig(String[] oreNames) {
        orePrompt(oreNames);

        // Load Guild Tasks

    }

    public void orePrompt(String[] oreNames) {
        String oreName = CommonActions.promptForSelection("Ore Selection", "Ore Selection", oreNames);

        switch( oreName ) {
            case "Tin":
                oreId = Items.TIN_ORE_438;
                break;
            case "Coal":
                oreId = Items.COAL_453;
                rockId = GameObjects.COALROCK;
                break;
            case "Mithril":
                oreId = Items.MITHRIL_ORE_447;
                break;
            case "Adamantite":
                oreId = Items.ADAMANTITE_ORE_449;
                break;
            default:
                oreId = 0;
                break;
        }
    }

}
