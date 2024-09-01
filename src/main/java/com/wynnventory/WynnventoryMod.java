package com.wynnventory;

import com.sun.tools.javac.Main;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.api.WynnventoryScheduler;
import com.wynnventory.model.keymapping.StickyKeyMapping;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WynnventoryMod implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("wynnventory");
	public static final Optional<ModContainer> WYNNVENTORY_INSTANCE = FabricLoader.getInstance().getModContainer("wynnventory");
	public static String WYNNVENTORY_VERSION;
	public static String WYNNVENTORY_MOD_NAME;

	private static boolean IS_DEV = false;
	public static boolean SHOW_TOOLTIP = true;

	@Override
	public void onInitializeClient() {
		if (WYNNVENTORY_INSTANCE.isEmpty()) {
			error("Could not find Wynnventory in Fabric Loader!");
			return;
		}
		WYNNVENTORY_VERSION = WYNNVENTORY_INSTANCE.get().getMetadata().getVersion().getFriendlyString();
		WYNNVENTORY_MOD_NAME = WYNNVENTORY_INSTANCE.get().getMetadata().getName();

		// Start WynnventoryScheduler
		WynnventoryScheduler.startScheduledTask();

		StickyKeyMapping priceTooltipKey = (StickyKeyMapping) KeyBindingHelper.registerKeyBinding(new StickyKeyMapping(
				"key.wynnventory.toggle_tooltip",
				GLFW.GLFW_KEY_PERIOD,
				"category.wynnventory.keybinding",
				() -> true
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(priceTooltipKey.hasStateChanged()) {
				Component message;
				if(priceTooltipKey.isDown()) {
					message = Component.literal("[Wynnventory] Trade Market tooltips disabled").withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
				} else {
					message = Component.literal("[Wynnventory] Trade Market tooltips enabled").withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
				}

				SHOW_TOOLTIP = !SHOW_TOOLTIP;
				McUtils.sendMessageToClient(message);
			}
		});

		try {
			IS_DEV = Main.class.getClassLoader().loadClass("com.intellij.rt.execution.application.AppMainV2") != null;
		} catch (NoClassDefFoundError | Exception ignored) {
			IS_DEV = WYNNVENTORY_VERSION.contains("dev");
		}

		if (isDev()) warn("Wynnventory is running in dev environment. Mod will behave differently in non-dev environment.");
		LOGGER.info("Initialized Wynnventory with version {}", WYNNVENTORY_VERSION);
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
		return IS_DEV;
	}
}