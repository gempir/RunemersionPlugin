package com.gempir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class DialogueOverlay extends Overlay
{
	private final Client client;
	private final RunemersionConfig config;
	private final PanelComponent mainDialoguePanel = new PanelComponent();
	private final List<PanelComponent> optionPanels = new ArrayList<>();

	@Inject
	private DialogueOverlay(Client client, RunemersionConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// Clear previous panels
		optionPanels.clear();
		
		// Try different widget IDs for dialogue components
		Widget npcDialogue = null;
		Widget playerDialogue = null;
		Widget continueWidget = null;
		Widget[] dialogueOptions = null;

		// Check for NPC dialogue (chat head dialogue)
		Widget npcChatHead = client.getWidget(231, 0); // NPC dialogue interface
		if (npcChatHead != null && !npcChatHead.isHidden())
		{
			// Look for text widget in NPC dialogue
			Widget npcText = client.getWidget(231, 5); // NPC text
			if (npcText != null && !npcText.isHidden() && npcText.getText() != null && !npcText.getText().isEmpty())
			{
				npcDialogue = npcText;
			}
		}

		// Check for player dialogue
		Widget playerChatHead = client.getWidget(217, 0); // Player dialogue interface  
		if (playerChatHead != null && !playerChatHead.isHidden())
		{
			Widget playerText = client.getWidget(217, 5); // Player text
			if (playerText != null && !playerText.isHidden() && playerText.getText() != null && !playerText.getText().isEmpty())
			{
				playerDialogue = playerText;
			}
		}

		// Check for continue dialogue
		Widget continueInterface = client.getWidget(229, 0); // Continue dialogue interface
		if (continueInterface != null && !continueInterface.isHidden())
		{
			Widget continueText = client.getWidget(229, 2); // Continue text
			if (continueText != null && !continueText.isHidden())
			{
				continueWidget = continueText;
			}
		}

		// Check for dialogue options
		Widget optionsInterface = client.getWidget(219, 0); // Options dialogue interface
		if (optionsInterface != null && !optionsInterface.isHidden())
		{
			Widget optionsContainer = client.getWidget(219, 1); // Options container
			if (optionsContainer != null && !optionsContainer.isHidden())
			{
				dialogueOptions = optionsContainer.getChildren();
			}
		}

		Dimension totalSize = new Dimension(0, 0);

		// Render NPC dialogue if present
		if (npcDialogue != null)
		{
			String dialogue = npcDialogue.getText().replace("<br>", " ").trim();
			if (!dialogue.isEmpty())
			{
				mainDialoguePanel.getChildren().clear();
				mainDialoguePanel.getChildren().add(LineComponent.builder().left(dialogue).build());
				mainDialoguePanel.setBackgroundColor(new Color(0, 0, 0, 150));
				mainDialoguePanel.setPreferredSize(new Dimension(400, 0)); // Set width for proper wrapping

				// Position NPC dialogue to the left of center
				Dimension panelSize = mainDialoguePanel.getPreferredSize();
				int centerX = client.getCanvasWidth() / 2;
				int centerY = client.getCanvasHeight() / 2;
				int x = centerX - panelSize.width - 50;
				int y = centerY - panelSize.height / 2;
				
				mainDialoguePanel.setPreferredLocation(new Point(x, y));
				Dimension mainSize = mainDialoguePanel.render(graphics);
				totalSize = new Dimension(Math.max(totalSize.width, mainSize.width), 
										totalSize.height + mainSize.height);
			}
		}

		// Render player dialogue if present
		if (playerDialogue != null)
		{
			String dialogue = playerDialogue.getText().replace("<br>", " ").trim();
			if (!dialogue.isEmpty())
			{
				mainDialoguePanel.getChildren().clear();
				mainDialoguePanel.getChildren().add(LineComponent.builder().left(dialogue).build());
				mainDialoguePanel.setBackgroundColor(new Color(0, 0, 0, 150));
				mainDialoguePanel.setPreferredSize(new Dimension(400, 0)); // Set width for proper wrapping

				// Position player dialogue to the right of center
				Dimension panelSize = mainDialoguePanel.getPreferredSize();
				int centerX = client.getCanvasWidth() / 2;
				int centerY = client.getCanvasHeight() / 2;
				int x = centerX + 50;
				int y = centerY - panelSize.height / 2;
				
				mainDialoguePanel.setPreferredLocation(new Point(x, y));
				Dimension mainSize = mainDialoguePanel.render(graphics);
				totalSize = new Dimension(Math.max(totalSize.width, mainSize.width), 
										totalSize.height + mainSize.height);
			}
		}

		// Render continue prompt on the right
		if (continueWidget != null)
		{
			PanelComponent continuePanel = new PanelComponent();
			continuePanel.getChildren().add(LineComponent.builder().left("<space to continue>").build());
			continuePanel.setBackgroundColor(new Color(50, 50, 0, 150)); // Yellow-ish background
			continuePanel.setPreferredSize(new Dimension(200, 0));

			// Position continue prompt on the right
			int centerX = client.getCanvasWidth() / 2;
			int centerY = client.getCanvasHeight() / 2;
			int x = centerX + 50;
			int y = centerY + 50; // Below center
			
			continuePanel.setPreferredLocation(new Point(x, y));
			Dimension continueSize = continuePanel.render(graphics);
			totalSize = new Dimension(Math.max(totalSize.width, continueSize.width), 
									Math.max(totalSize.height, y + continueSize.height));
		}

		// Render dialogue options if present
		if (dialogueOptions != null && dialogueOptions.length > 0)
		{
			int optionIndex = 1;
			int startY = client.getCanvasHeight() / 2; // Start at center
			int rightX = client.getCanvasWidth() / 2 + 50; // Right side of screen
			
			for (Widget option : dialogueOptions)
			{
				if (option != null && !option.isHidden() && option.getText() != null && !option.getText().trim().isEmpty())
				{
					String optionText = option.getText().replace("<br>", " ").trim();
					
					// Skip empty or invalid options
					if (optionText.isEmpty() || optionText.equals("null") || optionText.equals("Select an option"))
					{
						continue;
					}
					
					PanelComponent optionPanel = new PanelComponent();
					
					// Add number prefix if config enabled
					if (config.showDialogueNumbers())
					{
						optionText = optionIndex + ". " + optionText;
					}
					
					optionPanel.getChildren().add(LineComponent.builder().left(optionText).build());
					optionPanel.setBackgroundColor(new Color(0, 0, 0, 150)); // Blue background for options
					optionPanel.setPreferredSize(new Dimension(350, 40)); // Set width for proper sizing
					
					// Position each option panel with more spacing
					Dimension optionSize = optionPanel.getPreferredSize();
					int y = startY + (optionIndex - 1) * (optionSize.height); // 20px spacing between options
					optionPanel.setPreferredLocation(new Point(rightX, y));
					
					Dimension renderedSize = optionPanel.render(graphics);
					totalSize = new Dimension(Math.max(totalSize.width, rightX + renderedSize.width), 
											Math.max(totalSize.height, y + renderedSize.height));
					
					optionPanels.add(optionPanel);
					optionIndex++;
				}
			}
		}

		return totalSize.width > 0 || totalSize.height > 0 ? totalSize : null;
	}
}
