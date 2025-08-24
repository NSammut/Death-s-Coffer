package com.nicholassammut;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("deathscoffer")
public interface DeathsCofferConfig extends Config
{
    @ConfigItem(
            keyName = "apiUrl",
            name = "API URL",
            description = "The URL of the API to call on logon."
    )
    default String apiUrl()
    {
        return "http://osrsdeathscoffer.ddns.net";
    }
}
