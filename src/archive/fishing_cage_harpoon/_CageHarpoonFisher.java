package scripts.fishing_cage_harpoon;

import shared.constants.GameObjects;
import shared.constants.Npcs;
import shared.actions.ToggleLevelUp;
import shared.models.BaseAction;
import shared.tools.CommonAreas;
import shared.tools.CommonActions;
import shared.tools.GuiHelper;
import scripts.fishing_cage_harpoon.corsair.WalkBankToFishing;
import scripts.fishing_cage_harpoon.corsair.WalkFishingToBank;
import scripts.fishing_cage_harpoon.karamja.*;
import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.powerbot.script.Condition.sleep;

@Script.Manifest(name = "FISH - Lobs & Swordies", description = "Harpoon fishing with banking in Port Sarim", properties = "client=4; topic=051515; author=Bowman")
public class _CageHarpoonFisher extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    // Task List
    private List<BaseAction> taskList = new ArrayList<>();

    // Tracking
    private int catchCount = 0;
    private long lastCameraMove = 0;
    private double avgCatchExp = 90;
    private int catchesToLvl = 0;
    private int startExp = 0;


    // Data
    private String catchName = "Fish";
    private String status = "";


    @Override
    public void start() {
        status = "Loading";
        startExp = ctx.skills.experience(Constants.SKILLS_FISHING);

        lastCameraMove = getRuntime();
        ctx.properties.setProperty("randomevents.disable", "false"); //Ignore random events

        // Prompt for fishing type todo: reconfigure fishing rules based on location/catch combo. Configure for barb tail harpoon.
        catchName = CommonActions.promptForSelection("Fishing Style", "Desired Catch", new String[] {"Lobster", "Swordfish/Tuna", "Shark"});

        if( !catchName.equalsIgnoreCase("lobster") ) {
            // prompt for barb tail harpoon
            String dragonHarpoonResponse = CommonActions.promptForSelection("Harpoon Type", "Using Dragon Harpoon?", "Yes", "No");
            if( dragonHarpoonResponse.equals("Yes") ) {
                taskList.add(new HarpoonSpecial(ctx));
            }

        }

        // Prompt for Location
        String location = CommonActions.promptForSelection("Location", "Where are you fishing?", new String[] {"Corsair Cove", "Fishing Guild", "Karamja"});
        status = location;
        if( location.equals("Karamja") ) {
            status = "Task Config";
            taskList.add(new WalkBankToShip(ctx, CommonAreas.getPortSarimDock(), Npcs.SEAMAN_LORRIS_3645));
            taskList.add(new BoardShipToKaramja(ctx, Npcs.SEAMAN_LORRIS_3645));
            taskList.add(new ExitShipToKaramja(ctx, GameObjects.GANGPLANK_2082));
            taskList.add(new WalkShipToFishing(ctx, GameObjects.GANGPLANK_2082, CommonAreas.getKaramjaFishingDock(), CommonAreas.getPortSarimDock()));
            taskList.add(new FishAtDocks(ctx, Npcs.HARPOON_CAGE_FISHING_SPOT_KARAMJA, (!catchName.equalsIgnoreCase("lobster")), location));

            taskList.add(new BoardShipToPortSarim(ctx, Npcs.CUSTOMS_OFFICER_3648));
            taskList.add(new ExitShipToPortSarim(ctx, GameObjects.GANGPLANK_2084));
            taskList.add(new WalkShipToBank(ctx, CommonAreas.getPortSarimDepositBox(), CommonAreas.getPortSarimDock()));
            taskList.add(new BankDeposit(ctx, GameObjects.DEPOSIT_BOX_26254, false, 0) );
        }

        if( location.equals("Corsair Cove") ) {
            taskList.addAll(Arrays.asList(new WalkBankToFishing(ctx, Npcs.HARPOON_CAGE_FISHING_SPOT_CORSAIR), new FishAtDocks(ctx, Npcs.HARPOON_CAGE_FISHING_SPOT_CORSAIR, (!catchName.equalsIgnoreCase("lobster")), location), new WalkFishingToBank(ctx), new BankDeposit(ctx, GameObjects.DEPOSIT_BOX_31726, false, 0)));
        }

        if( location.equals("Fishing Guild") ) {
/*
            taskList.add(new ngc.fishing_cage_harpoon.fishing_guild.GuildWalkBankToFishing(ctx, 1510));
*/
            if( !catchName.equalsIgnoreCase("shark") ) {
                taskList.add(new FishAtDocks(ctx, Npcs.HARPOON_CAGE_FISHING_SPOT_FISHING_GUILD, !catchName.equalsIgnoreCase("Lobster"), location));
                taskList.add(new BankDeposit(ctx, GameObjects.DEPOSIT_BOX_6948, false, Npcs.HARPOON_CAGE_FISHING_SPOT_FISHING_GUILD));

            } else {
                taskList.add(new FishAtDocks(ctx, Npcs.HARPOON_NET_FISHING_SPOT_FISHING_GUILD, true, location));
                taskList.add(new BankDeposit(ctx, GameObjects.DEPOSIT_BOX_6948, false, Npcs.HARPOON_NET_FISHING_SPOT_FISHING_GUILD));

            }
          //  taskList.add(new HoverFishingSpots(ctx));
            taskList.add(new scripts.fishing_cage_harpoon.fishing_guild.GuildWalkFishingToBank(ctx));
        }

        taskList.add(new ToggleLevelUp(ctx));

        sleep(500);
        status = "Starting";
    }

    @Override
    public void poll() {
        if( getRuntime() - lastCameraMove > (Random.nextInt(50, 120) * 1000) ) {
            int pitchOffset = ctx.camera.pitch() <= 75 ? (75 - ctx.camera.pitch() + Random.nextInt(0, 10)) : ctx.camera.pitch() == 99 ? Random.nextInt(-10, 0) : Random.nextInt(-10, 10);
            ctx.camera.pitch(ctx.camera.pitch() + pitchOffset);
            lastCameraMove = getRuntime();
        }

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
        if( msg.contains("you catch a") && !msg.contains("attempt") ) {
            status = "Fishing";
            catchCount++;
            avgCatchExp = (ctx.skills.experience(Constants.SKILLS_FISHING) - startExp) / catchCount;
            catchesToLvl = (int) ((ctx.skills.experienceAt(ctx.skills.realLevel(Constants.SKILLS_FISHING) + 1) - (ctx.skills.experience(Constants.SKILLS_FISHING))) / avgCatchExp);
        }

    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(GuiHelper.getBaseColor());
        g.fillRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(GuiHelper.getBaseX(), GuiHelper.getBaseY(), GuiHelper.getWidthX(), GuiHelper.getWidthY(), 4, 4);

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        g.drawString("Status : " + status, GuiHelper.getStartX(), GuiHelper.getStartY(1));
        g.drawString("Time   : " + GuiHelper.getReadableRuntime(getRuntime()), GuiHelper.getStartX(), GuiHelper.getStartY(2));

        g.drawString("Catches : " + catchCount, GuiHelper.getStartX(), GuiHelper.getStartY(3));
        g.drawString("Avg Catch XP : " + avgCatchExp, GuiHelper.getStartX(), GuiHelper.getStartY(4));
        g.drawString("Lvl Up       : " + catchesToLvl + " to " + (ctx.skills.realLevel(Constants.SKILLS_FISHING) + 1), GuiHelper.getStartX(), GuiHelper.getStartY(5));
    }
}
