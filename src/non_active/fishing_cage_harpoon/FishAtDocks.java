package disabled.fishing_cage_harpoon;


import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import shared.templates.AbstractAction;
import shared.tools.AntibanTools;
import shared.tools.GaussianTools;

import java.awt.*;
import java.util.concurrent.Callable;

public class FishAtDocks extends AbstractAction<ClientContext> {
    private int FISHING_SPOT;
    private boolean usingHarpoon;
    private boolean karamja;
    private boolean fishingGuild;
    private boolean corsair;

    public FishAtDocks(ClientContext ctx, int fishingSpotId, boolean usingHarpoon, String location) {
        super(ctx, "Fishing");
        this.FISHING_SPOT = fishingSpotId;
        this.usingHarpoon = usingHarpoon;
        this.karamja = location.equalsIgnoreCase("Karamja");
        this.fishingGuild = location.equalsIgnoreCase("Fishing Guild");
        this.corsair = location.equalsIgnoreCase("Corsair Cove");
    }

    @Override
    public boolean activate() {
        boolean invNotFull = !ctx.inventory.isFull();
        boolean playerNotInteracting = !ctx.players.local().interacting().valid();
        boolean playerNotRunning = !ctx.players.local().inMotion();
        Npc fishingSpot = ctx.npcs.select().id(FISHING_SPOT).nearest().peek();
        boolean inFishingArea = fishingSpot.valid() && fishingSpot.inViewport();

        return invNotFull && playerNotInteracting && playerNotRunning && inFishingArea;
    }

    @Override
    public void execute() {
        // Check Camera
        if( karamja ) {
            if( ctx.camera.yaw() < 160 || ctx.camera.yaw() > 200 ) {
                ctx.camera.angle(AntibanTools.getRandomInRange(159, 201));
            }
        }

        if(corsair){
            if( ctx.camera.yaw() < 250 || ctx.camera.yaw() > 290 ) {
                ctx.camera.angle(AntibanTools.getRandomInRange(249, 291));
            }
        }

        if(fishingGuild){
            if( ctx.camera.yaw() < 250 || ctx.camera.yaw() > 290 ) {
                ctx.camera.angle(AntibanTools.getRandomInRange(249, 291));
            }
        }

        Npc fishingSpot = ctx.npcs.nearest().poll();

        if(fishingGuild){
            if(!fishingSpot.inViewport()){
                int tileX = AntibanTools.getRandomInRange(fishingSpot.tile().x() - 2, fishingSpot.tile().x() + 2);
                int tileY = AntibanTools.getRandomInRange(fishingSpot.tile().y() - 2, fishingSpot.tile().y() + 2);
                ctx.movement.step(new Tile(tileX, tileY));
            }
        }

        // Add AFK pause
        AntibanTools.sleepDelay(AntibanTools.getRandomInRange(3, 10));

        if( usingHarpoon ) {
            fishingSpot.interact("Harpoon");
        } else {
            fishingSpot.interact("Cage");
        }
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().animation() != -1;
            }
        }, 250, 4);

        if( GaussianTools.takeActionLikely() ) {
            int x = -10;
            int y = AntibanTools.getRandomInRange(0, 450);

            ctx.input.move(new Point(x, y));
        }
    }
}

