package com.mrfox1.wyrmagilityprogress;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class WyrmAgilityProgressPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(WyrmAgilityProgressPlugin.class);
		RuneLite.main(args);
	}
}
