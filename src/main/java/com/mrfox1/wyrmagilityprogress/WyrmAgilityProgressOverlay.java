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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class WyrmAgilityProgressOverlay extends OverlayPanel
{
	private final WyrmAgilityProgressPlugin plugin;

	@Inject
	private WyrmAgilityProgressOverlay(WyrmAgilityProgressPlugin plugin)
	{
		super(plugin);
		this.plugin = plugin;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isTracking())
		{
			return null;
		}

		long now = System.currentTimeMillis();
		long duration = Math.max(1, plugin.getExpectedEndMs() - plugin.getStartedAtMs());
		long elapsed = Math.max(0, now - plugin.getStartedAtMs());
		long remaining = Math.max(0, plugin.getExpectedEndMs() - now);

		panelComponent.getChildren().add(TitleComponent.builder()
			.text(plugin.getObstacleName())
			.color(Color.ORANGE)
			.build());

		ProgressBarComponent bar = new ProgressBarComponent();
		bar.setMinimum(0);
		bar.setMaximum(duration);
		bar.setValue(Math.min(elapsed, duration));
		bar.setForegroundColor(new Color(82, 161, 82));
		bar.setBackgroundColor(new Color(61, 56, 49));
		bar.setLabelDisplayMode(ProgressBarComponent.LabelDisplayMode.TEXT_ONLY);
		bar.setCenterLabel(remaining == 0 ? "Finishing..." : formatSeconds(remaining));
		panelComponent.getChildren().add(bar);

		return super.render(graphics);
	}

	private static String formatSeconds(long milliseconds)
	{
		return String.format("%.1fs", milliseconds / 1000.0);
	}
}
