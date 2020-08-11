package net.runelite.client.plugins.nightmare;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class PrayerOverlay extends Overlay {

    private final Client client;
    private final NightmarePlugin plugin;
    private final NightmareConfig config;

    @Inject
    private PrayerOverlay(Client client, NightmarePlugin plugin, NightmareConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGHEST);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);



    }
    private void drawBox(Graphics2D graphics, int startX, int startY ){

        graphics.setColor(Color.GREEN);

        graphics.drawLine(startX, startY, startX+33, startY);
        graphics.drawLine(startX+33, startY, startX+33, startY+33);
        graphics.drawLine(startX+33, startY+33, startX, startY+33);
        graphics.drawLine(startX, startY+33, startX, startY);

    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if(plugin.bossLoc != null){
            if(config.easyPrayer()){
                Widget x = client.getWidget(WidgetInfo.PRAYER_BOX);
                if(x != null && !x.isHidden() && !x.isSelfHidden()){
                //System.out.println(plugin.correctPray + " in overlay class" + plugin.cursePhase);
                    if(plugin.correctPray.equals("melee")){
                        if(plugin.cursePhase){
                            //System.out.println("curse phase MELEE, should highlight MISSILES");
                            net.runelite.api.Point startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES).getCanvasLocation();
                            drawBox(graphics, startLoc.getX(), startLoc.getY());
                        } else {
                            //System.out.println("normal MELEE, should highlight MELEE");
                            net.runelite.api.Point startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE).getCanvasLocation();
                            drawBox(graphics, startLoc.getX(), startLoc.getY());
                        }
                    } else if (plugin.correctPray.equals("missiles")){
                        if(plugin.cursePhase){
                            //System.out.println("curse phase MISSILES, should highlight MAGIC");
                            net.runelite.api.Point startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC).getCanvasLocation();
                            drawBox(graphics, startLoc.getX(), startLoc.getY());
                        } else {
                            //System.out.println("normal MISSILES, should highlight MISSILES");
                            net.runelite.api.Point startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES).getCanvasLocation();
                            drawBox(graphics, startLoc.getX(), startLoc.getY());
                        }
                    } else if (plugin.correctPray.equals("magic")){
                        if(plugin.cursePhase){
                            //System.out.println("curse phase MAGIC, should highlight MELEE");
                            net.runelite.api.Point startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE).getCanvasLocation();
                            drawBox(graphics, startLoc.getX(), startLoc.getY());
                        } else {
                            //System.out.println("normal MAGIC, should highlight MAGIC");
                            Point startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC).getCanvasLocation();
                            drawBox(graphics, startLoc.getX(), startLoc.getY());
                        }
                    }
                }
            }
        }
        return null;
    }
}
