package com.wynnventory.core;

import com.wynnventory.api.service.IconService;
import com.wynnventory.api.service.RewardService;
import com.wynnventory.feature.crowdsource.QueueScheduler;
import java.io.File;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WynnventoryMod {
    public static final String MOD_ID = "wynnventory";
    private static final IEventBus eventBus = BusBuilder.builder().build();

    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ModLoader loader;
    private static String version;
    private static boolean isBeta;
    private static File modFile;

    private WynnventoryMod() {}

    public static void init(ModLoader loader, String version, File modFile) {
        WynnventoryMod.loader = loader;
        WynnventoryMod.version = version;
        WynnventoryMod.isBeta = WynnventoryMod.version.contains("beta");
        WynnventoryMod.modFile = modFile;

        WynnventoryMod.logInfo(
                "Initializing Wynnventory mod v{} ({}), from file {}",
                version,
                loader.name(),
                modFile.getAbsolutePath());

        IconService.INSTANCE.fetchAll();
        RewardService.INSTANCE.reloadAllPools();
        QueueScheduler.startScheduledTask();
    }

    public static void registerEventListener(Object listener) {
        eventBus.register(listener);
    }

    public static void unregisterEventListener(Object listener) {
        eventBus.unregister(listener);
    }

    public static <T extends Event> void postEvent(T event) {
        try {
            eventBus.post(event);
            if (event instanceof ICancellableEvent cancellableEvent) {
                cancellableEvent.isCanceled();
            }
        } catch (Exception e) {
            logError("Error while posting event...");
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

    public static void logDebug(String msg, Object... args) {
        LOGGER.debug(msg, args);
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

    public static boolean isBeta() {
        return isBeta;
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

    public static File getModFile() {
        return modFile;
    }
}
