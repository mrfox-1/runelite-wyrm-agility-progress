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
		description = "Show a local-only yellow countdown above your character"
	)
	default boolean overheadCountdown()
	{
		return false;
	}

	@ConfigItem(
		keyName = "completionSound",
		name = "Completion sound",
		description = "Play a sound when the next obstacle should be clickable"
	)
	default boolean completionSound()
	{
		return false;
	}

	@Range(min = 0, max = 65535)
	@ConfigItem(
		keyName = "completionSoundId",
		name = "Sound ID",
		description = "RuneLite sound effect ID played on completion"
	)
	default int completionSoundId()
	{
		return 3813;
	}
}
