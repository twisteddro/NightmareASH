package net.runelite.client.plugins.nightmare;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.sound.sampled.Line;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SanfewOverlay extends Overlay {

    private final Client client;
    private final NightmarePlugin plugin;
    private final NightmareConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    private int opacity = 5;
    @Inject
    private ItemManager itemManager;

    @Inject
    private SanfewOverlay(Client client, NightmarePlugin plugin, NightmareConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (config.bug()) {
            panelComponent.getChildren().clear();
            if (plugin.parasiteSpawned) {
                if (plugin.impregnated) {
                    if (opacity >= 80) {
                        opacity = 5;
                    } else {
                        opacity += 3;
                    }
                    panelComponent.setBackgroundColor(new Color(255, 0, 0, opacity));
                } else {
                    opacity = 80;
                    panelComponent.setBackgroundColor(new Color(0, 255, 0, opacity));
                }

                //panelComponent.getChildren().add( TitleComponent.builder().text("BUG").build());
                //panelComponent.getChildren().add(LineComponent.builder().left(String.valueOf(plugin.parasiteSpawn)).build());

                if(plugin.parasiteSpawn < 10){
                    panelComponent.getChildren().add(LineComponent.builder().left(" " + plugin.parasiteSpawn).build());
                } else {
                    panelComponent.getChildren().add(LineComponent.builder().left(String.valueOf(plugin.parasiteSpawn)).build());
                }

                    panelComponent.setPreferredSize(new Dimension(24, 0));

                return panelComponent.render(graphics);
            } else {
                opacity = 5;
            }
            //System.out.println("If it's here, parasitespawned = false");
            return null;
        }
        return null;
    }
}


        /**
        if(plugin.impregnated){
            panelComponent.getChildren().clear();
            //BufferedImage sanfew = spriteManager.getSprite(10925, 0);

            BufferedImage sanfew = itemManager.getImage(10925);
            InfoBoxComponent impregnated = new InfoBoxComponent();

            impregnated.setBackgroundColor(new Color(255, 0, 0, opacity));

            impregnated.setImage(sanfew);
            impregnated.setPreferredSize(new Dimension(sanfew.getWidth(), sanfew.getHeight()));
            panelComponent.getChildren().add(impregnated);
            panelComponent.setPreferredSize(new Dimension(sanfew.getWidth(), sanfew.getHeight()));
            panelComponent.setBorder(new Rectangle(0, 0, 0, 0));


            if(opacity >= 150){
                opacity = 5;
            } else {
                opacity += 2;
            }
            if(config.bug()){
                return panelComponent.render(graphics);
            } else {
                return null;
            }

        } else {
            opacity = 5;
            return null;
        }

    }
}**/
