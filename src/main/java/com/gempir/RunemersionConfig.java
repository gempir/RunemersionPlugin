package com.gempir;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("runemersion")
public interface RunemersionConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}

	@ConfigItem(
		keyName = "showDialogueNumbers",
		name = "Show Dialogue Numbers",
		description = "Show numbers next to dialogue options"
	)
	default boolean showDialogueNumbers()
	{
		return true;
	}
}
