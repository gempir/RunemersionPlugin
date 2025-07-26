package com.gempir;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Runemersion"
)
public class RunemersionPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private DialogueOverlay dialogueOverlay;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(dialogueOverlay);
		log.info("Runemersion started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(dialogueOverlay);
		log.info("Runemersion stopped!");
	}

	@Provides
	RunemersionConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RunemersionConfig.class);
	}
}
