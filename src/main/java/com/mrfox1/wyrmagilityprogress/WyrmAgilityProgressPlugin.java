/*
 * Copyright (c) 2026, mrfox-1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES ARE DISCLAIMED.
 */
package com.mrfox1.wyrmagilityprogress;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Wyrm Agility AFK Progress",
	description = "Shows AFK-friendly progress bars for both Colossal Wyrm agility routes",
	tags = {"agility", "wyrm", "progress", "timer", "afk"}
)
public class WyrmAgilityProgressPlugin extends Plugin
{
	private static final int GAME_TICK_MS = 600;
	private static final int COLOSSAL_WYRM_REGION = 6445;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Notifier notifier;

	@Inject
	private WyrmAgilityProgressConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WyrmAgilityProgressOverlay overlay;

	@Getter
	private boolean tracking;

	@Getter
	private String obstacleName = "Wyrm obstacle";

	@Getter
	private long startedAtMs;

	@Getter
	private long expectedEndMs;

	private boolean completionNotificationSent;
	private WorldPoint lastLocation;
	private WorldPoint timerStartLocation;
	private boolean tentativeTimer;
	private String lastOverheadText;
	private int currentObstacleTicks;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		reset();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		Player local = client.getLocalPlayer();
		if (local == null || local.getWorldLocation().getRegionID() != COLOSSAL_WYRM_REGION)
		{
			return;
		}

		Integer ticks = getWyrmTicks(event.getId());
		if (ticks == null)
		{
			return;
		}

		if (tracking && !canReplaceTimer(local))
		{
			return;
		}

		long now = System.currentTimeMillis();
		boolean replacingBeforeReady = tracking && now < expectedEndMs;
		if (tracking && now >= expectedEndMs)
		{
			sendCompletionNotification();
		}

		obstacleName = cleanName(event.getMenuTarget(), event.getMenuOption());
		startedAtMs = now;
		expectedEndMs = startedAtMs + (long) ticks * GAME_TICK_MS;
		currentObstacleTicks = ticks;
		completionNotificationSent = false;
		lastLocation = local.getWorldLocation();
		timerStartLocation = lastLocation;
		tentativeTimer = replacingBeforeReady;
		tracking = true;
		updateOverheadCountdown(local);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!tracking)
		{
			return;
		}

		Player local = client.getLocalPlayer();
		if (local == null)
		{
			reset();
			return;
		}
		long now = System.currentTimeMillis();
		if (tentativeTimer && timerStartLocation != null
			&& !local.getWorldLocation().equals(timerStartLocation))
		{
			tentativeTimer = false;
		}
		updateOverheadCountdown(local);
		if (now >= expectedEndMs)
		{
			sendCompletionNotification();
		}
		if (now >= expectedEndMs + GAME_TICK_MS)
		{
			reset();
			return;
		}

		lastLocation = local.getWorldLocation();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			reset();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (WyrmAgilityProgressConfig.GROUP.equals(event.getGroup())
			&& "completionSoundId".equals(event.getKey()))
		{
			int soundId = config.completionSoundId();
			clientThread.invokeLater(() ->
			{
				if (client.getGameState() == GameState.LOGGED_IN)
				{
					client.playSoundEffect(soundId);
				}
			});
		}
	}

	private boolean canReplaceTimer(Player local)
	{
		if (tentativeTimer && timerStartLocation != null
			&& local.getWorldLocation().equals(timerStartLocation))
		{
			return true;
		}

		long remaining = expectedEndMs - System.currentTimeMillis();
		if (remaining <= GAME_TICK_MS)
		{
			return true;
		}

		return remaining <= 2L * GAME_TICK_MS
			&& lastLocation != null
			&& local.getWorldLocation().equals(lastLocation);
	}

	private void sendCompletionNotification()
	{
		if (completionNotificationSent)
		{
			return;
		}

		completionNotificationSent = true;
		if ((long) currentObstacleTicks * GAME_TICK_MS <= (long) config.minimumSoundSeconds() * 1000L)
		{
			return;
		}

		if (config.completionSound())
		{
			client.playSoundEffect(config.completionSoundId());
		}
		if (config.nativeNotification())
		{
			notifier.notify(obstacleName + " complete");
		}
	}

	private void updateOverheadCountdown(Player local)
	{
		if (!config.overheadCountdown())
		{
			clearOverheadCountdown();
			return;
		}

		String current = local.getOverheadText();
		if (current != null && !current.equals(lastOverheadText))
		{
			lastOverheadText = null;
			return;
		}

		long remaining = Math.max(0, expectedEndMs - System.currentTimeMillis());
		String text = Long.toString((remaining + 999) / 1000);
		local.setOverheadText(text);
		local.setOverheadCycle(100);
		lastOverheadText = text;
	}

	private void clearOverheadCountdown()
	{
		Player local = client.getLocalPlayer();
		if (local != null && lastOverheadText != null && lastOverheadText.equals(local.getOverheadText()))
		{
			local.setOverheadText(null);
			local.setOverheadCycle(0);
		}
		lastOverheadText = null;
	}

	private void reset()
	{
		clearOverheadCountdown();
		tracking = false;
		completionNotificationSent = false;
		lastLocation = null;
		timerStartLocation = null;
		tentativeTimer = false;
		currentObstacleTicks = 0;
	}

	private static Integer getWyrmTicks(int id)
	{
		switch (id)
		{
			case ObjectID.VARLAMORE_WYRM_AGILITY_START_LADDER_TRIGGER:
				return 6;
			case ObjectID.VARLAMORE_WYRM_AGILITY_BALANCE_1_TRIGGER:
				return 45;
			case ObjectID.VARLAMORE_WYRM_AGILITY_END_ZIPLINE_TRIGGER:
				return 15;
			case ObjectID.VARLAMORE_WYRM_AGILITY_BASIC_BALANCE_1_TRIGGER:
				return 18;
			case ObjectID.VARLAMORE_WYRM_AGILITY_BASIC_MONKEYBARS_1_TRIGGER:
				return 30;
			case ObjectID.VARLAMORE_WYRM_AGILITY_BASIC_LADDER_1_TRIGGER:
				return 3;
			case ObjectID.VARLAMORE_WYRM_AGILITY_ADVANCED_LADDER_1_TRIGGER:
				return 3;
			case ObjectID.VARLAMORE_WYRM_AGILITY_ADVANCED_JUMP_1_TRIGGER:
				return 10;
			case ObjectID.VARLAMORE_WYRM_AGILITY_ADVANCED_BALANCE_1_TRIGGER:
				return 59;
			default:
				return null;
		}
	}

	private static String cleanName(String target, String option)
	{
		String cleanTarget = Text.removeTags(target).trim();
		return cleanTarget.isEmpty() ? option + " obstacle" : cleanTarget;
	}

	@Provides
	WyrmAgilityProgressConfig provideConfig(ConfigManager manager)
	{
		return manager.getConfig(WyrmAgilityProgressConfig.class);
	}
}
