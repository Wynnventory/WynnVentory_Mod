package com.wynnventory;

import com.wynnventory.handler.TooltipHandler;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WynnventoryMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("wynnventory");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Initialized WynnVentoryMod");
		System.out.println("Initialized Wynnventory via Sout");
		TooltipHandler.registerTooltips();
	}

	public static void info(String msg) {
		LOGGER.info(msg);
	}

	public static void warn(String msg) {
		LOGGER.warn(msg);
	}

	public static void error(String msg) {
		LOGGER.error(msg);
	}
}