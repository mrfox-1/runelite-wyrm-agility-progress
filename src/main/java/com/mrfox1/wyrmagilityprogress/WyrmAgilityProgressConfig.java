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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(WyrmAgilityProgressConfig.GROUP)
public interface WyrmAgilityProgressConfig extends Config
{
	String GROUP = "wyrm-agility-progress";

	@ConfigItem(
		keyName = "overheadCountdown",
		name = "Overhead countdown",
		description = "Show a local-only yellow countdown above your character",
		position = 1
	)
	default boolean overheadCountdown()
	{
		return false;
	}

	@ConfigItem(
		keyName = "nativeNotification",
		name = "RuneLite notification",
		description = "Use RuneLite notifications (tray notification, request focus, taskbar flash, etc.) when the next obstacle should be clickable",
		position = 2
	)
	default boolean nativeNotification()
	{
		return false;
	}

	@ConfigItem(
		keyName = "completionSound",
		name = "Plugin-specific sound only",
		description = "Play the plugin's configured sound when the next obstacle should be clickable",
		position = 3
	)
	default boolean completionSound()
	{
		return false;
	}

	@Range(min = 0, max = 60)
	@ConfigItem(
		keyName = "minimumSoundSeconds",
		name = "Minimum notification length",
		description = "Only notify for obstacles longer than this many seconds (0 notifies for all)",
		position = 4
	)
	default int minimumSoundSeconds()
	{
		return 0;
	}

	@Range(min = 0, max = 65535)
	@ConfigItem(
		keyName = "completionSoundId",
		name = "Sound ID",
		description = "RuneLite sound effect ID played on completion",
		position = 5
	)
	default int completionSoundId()
	{
		return 3813;
	}
}
