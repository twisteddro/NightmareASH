package net.runelite.client.plugins.nightmare;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("Nightmare")

public interface NightmareConfig extends Config {
    @ConfigItem(
            keyName = "prayer",
            name = "Easy Pray",
            description = "Highlights Correct Prayer."

    )
    default boolean easyPrayer() {
        return true;
    }

    @ConfigItem(
            keyName = "hidePrayers",
            name = "Hide Prayers",
            description = "Removes all unnecessary prayers for nightmare."

    )
    default boolean hidePrayer() {
        return false;
    }

    @ConfigItem(
            keyName = "bug",
            name = "Parasite Spawn Timer",
            description = "Notifies you when a bug has crawled up ur ass so u dont 4get to drink sanfew and fuckin die. Also spawn timer for parasite."


    )
    default boolean bug() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightTotems",
            name = "Highlight Totems",
            description = "Highlights Totems based on their status",
            position = 2
    )
    default boolean highlightTotems() {
        return false;
    }

    @ConfigItem(
            keyName = "highlightShadows",
            name = "Highlight Shadows",
            description = "Highlights the Shadow Attacks",
            position = 3
    )
    default boolean highlightShadows() {
        return false;
    }

    @ConfigItem(
            keyName = "highlightSpores",
            name = "Highlight Spores",
            description = "Highlights spores that will make you yawn",
            position = 4
    )
    default boolean highlightSpores() {
        return false;
    }

    @ConfigItem(
            keyName = "poisonBorderCol",
            name = "Poison border colour",
            description = "Colour the edges of the area highlighted by poison special will be",
            position = 5
    )

    default Color poisonBorderCol() {
        return new Color(255, 0, 0, 100);
    }

    @ConfigItem(
            keyName = "shadowBorderCol",
            name = "Shadow border colour",
            description = "Colour the edges of the area highlighted by Shadow will be",
            position = 5
    )

    default Color shadowBorderCol() {
        return new Color(255, 0, 0, 100);
    }

}
