package com.nicholassammut;

import com.google.gson.Gson;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "Death's Coffer",
	description = "Use !dc or !deathscoffer to check your coffer's value.",
	tags = {"death", "coffer", "pvm", "value", "chat", "command"}
)
public class DeathsCofferPlugin extends Plugin
{
	private static final int DEATHS_COFFER_VARP = 261;
	private static final String DC_COMMAND = "!dc";
    private static final String DEATHSCOFFER_COMMAND = "!deathscoffer";
    private static final String COFFER_COMMAND = "!coffer";
	private static final Pattern DEATH_CHAT_PATTERN = Pattern.compile("Death charges you [0-9,]+ x Coins\\. You have ([0-9,]+) x Coins left in Death's Coffer\\.");
    private long cofferValue = -1;

	@Inject
	private Client client;

	@Inject
	private ChatCommandManager chatCommandManager;

	@Inject
    private ChatMessageManager chatMessageManager;

	@Inject
	private ClientToolbar clientToolbar;

	private DeathsCofferPanel panel;
	private NavigationButton navButton;

    @Inject
    private DeathsCofferService dcService;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Death's Coffer startUp()!");
		panel = new DeathsCofferPanel(dcService);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Death's Coffer")
			.icon(icon)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);
		
		// Register the chat commands when the plugin starts.
		chatCommandManager.registerCommandAsync(DC_COMMAND, this::updateChat);
        chatCommandManager.registerCommandAsync(DEATHSCOFFER_COMMAND, this::updateChat);
        chatCommandManager.registerCommandAsync(COFFER_COMMAND, this::updateChat);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);

		// Unregister the chat commands when the plugin stops.
		chatCommandManager.unregisterCommand(DC_COMMAND);
		chatCommandManager.unregisterCommand(DEATHSCOFFER_COMMAND);
        chatCommandManager.unregisterCommand(COFFER_COMMAND);
		log.info("Death's Coffer shutDown()!");
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
		int groupId = widgetLoaded.getGroupId();
		log.debug("Widget Group ID: {}", groupId);
		switch (groupId) {
			case 669:
			//case 670:
			case 671:
				// This code runs when the player opens the sacrifice chest.
				log.debug("Death's Coffer interface opened.");
                long cofferValue = client.getVarpValue(DEATHS_COFFER_VARP);
                this.cofferValue = cofferValue;
                Player loggedInPlayer = client.getLocalPlayer();
                if(loggedInPlayer != null && loggedInPlayer.getName() != null) {
                    String playerName = loggedInPlayer.getName();
                    dcService.updateCofferValue(playerName, cofferValue);
                }
                panel.setCofferValue(cofferValue);
				break;
		}
	}

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            log.debug("Player is now logged in!");
            Player loggedInPlayer = client.getLocalPlayer();
            if(loggedInPlayer != null && loggedInPlayer.getName() != null) {
                dcService.getCofferValue(loggedInPlayer.getName()).whenComplete((cofferValue, ex) -> {
                    if (ex != null) {
                        log.error("Error fetching coffer value: " + ex.getMessage());
                        return;
                    } else {
                        this.cofferValue = cofferValue;
                        panel.setCofferValue(cofferValue);
                    }
                });
            }
        } else if (event.getGameState() == GameState.LOGIN_SCREEN) {
            log.debug("The game state has changed to LOGIN_SCREEN.");
            this.cofferValue = -1;
            panel.setCofferValue(-1);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        // Only look at GAME messages
        if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            String message = event.getMessage();
            Matcher matcher = DEATH_CHAT_PATTERN.matcher(message);
            if (matcher.find()) {
                String cofferString = matcher.group(1);
                long cofferValue = Long.parseLong(cofferString.replace(",", ""));
                this.cofferValue = cofferValue;
                dcService.updateCofferValue(client.getLocalPlayer().getName(), cofferValue);
                panel.setCofferValue(cofferValue);
            }
        }
    }

	private void updateChat(ChatMessage chatMessage, String message) {
        final MessageNode messageNode = chatMessage.getMessageNode();

        String loadingMessage = new ChatMessageBuilder()
                .append("Retrieving coffer value, please wait...")
                .build();

        messageNode.setRuneLiteFormatMessage(loadingMessage);
        client.refreshChat();

        dcService.getCofferValue(chatMessage.getName()).thenAccept(result -> {
            NumberFormat defaultFormatter = NumberFormat.getInstance();
            String formattedResult = defaultFormatter.format(result);
            String newMessage = new ChatMessageBuilder()
                    .append(Color.YELLOW, "Coffer Value")
                    .append(ChatColorType.HIGHLIGHT)
                    .append(" - ")
                    .append(formattedResult)
                    .append(" coins.")
                    .build();

            messageNode.setRuneLiteFormatMessage(newMessage);
            client.refreshChat();
        });
    }

	@Provides
	DeathsCofferConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DeathsCofferConfig.class);
	}
}