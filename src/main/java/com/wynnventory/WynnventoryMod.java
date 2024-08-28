package com.wynnventory;

import com.mojang.blaze3d.platform.InputConstants;
import com.sun.tools.javac.Main;
import com.wynnventory.api.WynnventoryScheduler;
import com.wynnventory.util.KeyMappingUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class WynnventoryMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("wynnventory");
	public static final Optional<ModContainer> WYNNVENTORY_INSTANCE = FabricLoader.getInstance().getModContainer("wynnventory");
	public static String WYNNVENTORY_VERSION;
	public static String WYNNVENTORY_MOD_NAME;

	public static boolean SHOW_TOOLTIP = true;
	private boolean keyPressed = false;

	@Override
	public void onInitialize() {
		if (WYNNVENTORY_INSTANCE.isEmpty()) {
			error("Could not find Wynnventory in Fabric Loader!");
			return;
		}
		WYNNVENTORY_VERSION = WYNNVENTORY_INSTANCE.get().getMetadata().getVersion().getFriendlyString();
		WYNNVENTORY_MOD_NAME = WYNNVENTORY_INSTANCE.get().getMetadata().getName();

		// Start WynnventoryScheduler
		WynnventoryScheduler.startScheduledTask();

		KeyMapping priceTooltipKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.wynnventory.toggle_tooltip",
				GLFW.GLFW_KEY_PERIOD,
				"category.wynnventory.keybinding"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.screen != null && client.player != null && KeyMappingUtil.getBoundKey(priceTooltipKey) != null) {
				long windowHandle = Minecraft.getInstance().getWindow().getWindow();
				int keyCode = Objects.requireNonNull(KeyMappingUtil.getBoundKey(priceTooltipKey)).getValue();

				if (InputConstants.isKeyDown(windowHandle, keyCode)) {
					System.out.println("CLICKED");
					if (!keyPressed) {
						SHOW_TOOLTIP = !SHOW_TOOLTIP;
					}
					keyPressed = true;
				} else {
					keyPressed = false;
				}
			}
		});

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