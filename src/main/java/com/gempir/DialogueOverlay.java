package com.gempir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class DialogueOverlay extends Overlay
{
	private final Client client;
	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	private DialogueOverlay(Client client)
	{
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Widget npcDialogue = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);
		Widget playerDialogue = client.getWidget(WidgetInfo.DIALOG_PLAYER_TEXT);

		String dialogue = null;
		if (npcDialogue != null && !npcDialogue.isHidden())
		{
			dialogue = npcDialogue.getText().replace("<br>", " ");
		}
		else if (playerDialogue != null && !playerDialogue.isHidden())
		{
			dialogue = playerDialogue.getText().replace("<br>", " ");
		}

		if (dialogue == null)
		{
			return null;
		}

		panelComponent.getChildren().clear();
		panelComponent.getChildren().add(LineComponent.builder().left(dialogue).build());
		panelComponent.setBackgroundColor(new Color(0, 0, 0, 150));

		// Center the panel
		Dimension panelSize = panelComponent.getPreferredSize();
		int x = (client.getCanvasWidth() - panelSize.width) / 2;
		int y = (client.getCanvasHeight() - panelSize.height) / 2;
		panelComponent.setPreferredLocation(new Point(x, y));

		return panelComponent.render(graphics);
	}
}
