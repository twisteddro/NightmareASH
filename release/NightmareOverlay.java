package net.runelite.client.plugins.nightmare;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.Map;

public class NightmareOverlay extends Overlay {

    private final Client client;
    private final NightmarePlugin plugin;
    private final NightmareConfig config;

    private static final int NIGHTMARE_SHADOW = 1767;   // graphics object

    @Inject
    private NightmareOverlay(Client client, NightmarePlugin plugin, NightmareConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGHEST);
        setLayer(OverlayLayer.UNDER_WIDGETS);



    }

    private void drawBox(Graphics2D graphics, int startX, int startY ){

        graphics.setColor(Color.GREEN);

        graphics.drawLine(startX, startY, startX+33, startY);
        graphics.drawLine(startX+33, startY, startX+33, startY+33);
        graphics.drawLine(startX+33, startY+33, startX, startY+33);
        graphics.drawLine(startX, startY+33, startX, startY);

    }
    protected void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (point.distanceTo(playerLocation) >= 32)
            return;

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null)
            return;

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null)
            return;

        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(poly);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
        graphics.fill(poly);
    }

    private void drawPoisonArea(Graphics2D graphics, Map<LocalPoint, GameObject> spores)
    {
        if (spores.size() < 1)
        {
            return;
        }

        for(LocalPoint point : spores.keySet()){
            //WorldPoint p = WorldPoint.fromLocal(client, point);
            Polygon poly = Perspective.getCanvasTileAreaPoly(client, point, 3);
            graphics.setColor(config.poisonBorderCol());
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(poly);
            //drawTile(graphics, p, config.poisonBorderCol(), 1, 255, 0);
        }

        /**
        Area poisonTiles = new Area();

        for (Map.Entry<LocalPoint, GameObject> entry : spores.entrySet())
        {
            LocalPoint point = entry.getKey();
            Polygon poly = Perspective.getCanvasTileAreaPoly(client, point, 3);

            if (poly != null)
            {
                poisonTiles.add(new Area(poly));
            }
        }


        graphics.setPaintMode();
        graphics.setColor(config.poisonBorderCol());
        graphics.draw(poisonTiles);
        graphics.setColor(new Color(config.poisonBorderCol().getRed(), config.poisonBorderCol().getGreen(), config.poisonBorderCol().getBlue(), 0));
        graphics.fill(poisonTiles);**/
    }

    private void renderNpcOverlay(Graphics2D graphics, NPC actor, Color color)
    {
        final Shape objectClickbox = actor.getConvexHull();
        graphics.setColor(color);
        graphics.draw(objectClickbox);
    }

    @Override
    public Dimension render(Graphics2D graphics) {


        if(plugin.bossLoc != null){
            /**
            if(config.tankTile()){
                WorldPoint x = new WorldPoint(plugin.bossLoc.getX()-1, plugin.bossLoc.getY()+4, client.getPlane());
                WorldPoint y = new WorldPoint(plugin.bossLoc.getX()+4, plugin.bossLoc.getY()-1, client.getPlane());
                drawTile(graphics, x, config.tankTileColor(), 2, 255, 15);
                drawTile(graphics, y, config.tankTileColor(), 2, 255, 15);
            }**/
            if (config.highlightShadows())
            {
                for (GraphicsObject graphicsObject : client.getGraphicsObjects())
                {
                    Color color;

                    if (graphicsObject.getId() == NIGHTMARE_SHADOW)
                    {
                        color = config.shadowBorderCol();
                    }
                    else
                    {
                        continue;
                    }

                    LocalPoint lp = graphicsObject.getLocation();
                    Polygon poly = Perspective.getCanvasTilePoly(client, lp);

                    if (poly != null)
                    {
                        OverlayUtil.renderPolygon(graphics, poly, color);
                    }
                }
            }
            if (config.highlightTotems())
            {
                for (MemorizedTotem totem : plugin.getTotems().values())
                {
                    if (totem.getCurrentPhase().isActive())
                    {
                        renderNpcOverlay(graphics, totem.getNpc(), totem.getCurrentPhase().getColor());
                    }
                }
            }
            if (config.highlightSpores())
            {

                drawPoisonArea(graphics, plugin.getSpores());
            }
        }
        return null;
    }
}
