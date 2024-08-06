package com.wynnventory;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wynnventory_mod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("wynnventory_mod");
	public static final Wynnventory_MarketContainer MARKET_LISTENER = new Wynnventory_MarketContainer();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello from WynnVentory!");
	}
}