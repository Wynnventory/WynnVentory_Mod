package com.wynnventory.core;

import com.wynnventory.feature.LootRewardHandler;
import com.wynnventory.core.event.EventBusWrapper;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class WynnventoryMod {
    public static final String  MOD_ID = "wynnventory";

    private static final IEventBus eventBus = EventBusWrapper.createEventBus();
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ModLoader     LOADER;
    private static String        VERSION;
    private static boolean       IS_DEV;
    private static File          MOD_FILE;

    private WynnventoryMod() {}

    public static void init(ModLoader loader, String version, File modFile) {
        LOADER = loader;
        VERSION = version;
        IS_DEV = VERSION.contains("dev");
        MOD_FILE = modFile;

        logInfo("Initializing Wynnventory mod v{} ({}), from file {}", version, loader.name(), modFile.getAbsolutePath());

        eventBus.register(new LootRewardHandler());
    }

    public static <T extends Event> boolean postEvent(T event) {
        try {
            eventBus.post(event);
            return event instanceof ICancellableEvent cancellableEvent && cancellableEvent.isCanceled();
        } catch (Throwable t) {
            return false;
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

    public static void logDebug(String msg) {
        LOGGER.debug(msg);
    }

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
        return IS_DEV;
    }

    public enum ModLoader {
        FORGE,
        FABRIC
    }
}
