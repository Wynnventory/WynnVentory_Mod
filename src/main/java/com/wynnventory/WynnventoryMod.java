package com.wynnventory;

import com.sun.tools.javac.Main;
import com.wynnventory.api.WynnventoryScheduler;
import com.wynnventory.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WynnventoryMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("wynnventory");
	public static final Optional<ModContainer> WYNNVENTORY_INSTANCE = FabricLoader.getInstance().getModContainer("wynnventory");
	public static final String WYNNVENTORY_VERSION = WYNNVENTORY_INSTANCE.get().getMetadata().getVersion().getFriendlyString();
	public static final String WYNNVENTORY_MOD_NAME = WYNNVENTORY_INSTANCE.get().getMetadata().getName();

	@Override
	public void onInitialize() {
		if (WYNNVENTORY_INSTANCE.isEmpty()) {
			error("Could not find Wynnventory in Fabric Loader!");
			return;
		}

		// Load configs
		ConfigManager.loadConfig();

		// Start WynnventoryScheduler
		WynnventoryScheduler.startScheduledTask();


		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		if (isDev()) warn("WynnVentory is running in dev environment. Mod will behave differently in non-dev environment.");
		LOGGER.info("Initialized WynnVentoryMod with version {}", WYNNVENTORY_VERSION);
	}

	public static void info(String msg) {
		LOGGER.info(msg);
	}

	public static void warn(String msg) {
		LOGGER.warn(msg);
	}

	public static void warn(String msg, Throwable t) {
		LOGGER.warn(msg, t);
	}

	public static void error(String msg) {
		LOGGER.error(msg);
	}

	public static void error(String msg, Throwable t) {
		LOGGER.error(msg, t);
	}

	public static boolean isDev() {
		boolean isDev = false;
		try {
			isDev = Main.class.getClassLoader().loadClass("com.intellij.rt.execution.application.AppMainV2") != null;
		} catch (NoClassDefFoundError | Exception e) {
		}
		return isDev;
	}
}