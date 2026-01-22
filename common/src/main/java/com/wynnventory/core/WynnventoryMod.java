package com.wynnventory.core;

import com.wynnventory.config.ModConfig;
import com.wynnventory.core.command.WynnventoryCommands;
import com.wynnventory.handler.CommandHandler;
import com.wynnventory.handler.LootRewardHandler;
import com.wynnventory.core.event.EventBusWrapper;
import com.wynnventory.handler.RaidWindowHandler;
import com.wynnventory.handler.TooltipRenderHandler;
import com.wynnventory.queue.QueueScheduler;
import com.wynnventory.util.IconManager;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class WynnventoryMod {
    public static final String  MOD_ID = "wynnventory";

    private static final IEventBus eventBus = EventBusWrapper.createEventBus();
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ModLoader loader;
    private static String version;
    private static boolean isDev;
    private static File modFile;

    private WynnventoryMod() {}

    public static void init(ModLoader loader, String version, File modFile) {
        WynnventoryMod.loader   = loader;
        WynnventoryMod.version  = version;
        WynnventoryMod.isDev    = WynnventoryMod.version.contains("dev");
        WynnventoryMod.modFile  = modFile;

        WynnventoryMod.logInfo("Initializing Wynnventory mod v{} ({}), from file {}", version, loader.name(), modFile.getAbsolutePath());

        ModConfig.init();
        WynnventoryCommands.init();
        IconManager.fetchAll();
        QueueScheduler.startScheduledTask();

        eventBus.register(new LootRewardHandler());
        eventBus.register(new TooltipRenderHandler());
        eventBus.register(new RaidWindowHandler());
        eventBus.register(new CommandHandler());

        WynnventoryMod.logInfo("Wynnventory mod successfully initialized");
    }

    public static <T extends Event> void postEvent(T event) {
        try {
            eventBus.post(event);
        } catch (Exception e) {
            logError("Error while posting event...", e);
        }
    }

    public static void logInfo(String msg) {
        LOGGER.info(msg);
    }

    public static void logInfo(String msg, Object... args) {
        LOGGER.info(msg, args);
    }

    public static void logWarn(String msg) {
        LOGGER.warn(msg);
    }

    public static void logDebug(String msg) { LOGGER.debug(msg); }

    public static void logDebug(String msg, Object... args) { LOGGER.debug(msg, args); }

    public static void logError(String msg) {
        LOGGER.error(msg);
    }

    public static void logError(String msg, Object... args) {
        LOGGER.error(msg, args);
    }

    public static void logError(String msg, Throwable t) {
        LOGGER.error(msg, t);
    }

    public static boolean isDev() {
        return isDev;
    }

    public enum ModLoader {
        FORGE,
        FABRIC
    }

    public static ModLoader getLoader() {
        return loader;
    }

    public static String getVersion() {
        return version;
    }

    public static boolean isIsDev() {
        return isDev;
    }

    public static File getModFile() {
        return modFile;
    }
}
