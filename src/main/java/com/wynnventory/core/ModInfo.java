package com.wynnventory.core;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ModInfo {

    public static final Logger LOGGER = LoggerFactory.getLogger("wynnventory");

    public static final Optional<ModContainer> INSTANCE = FabricLoader.getInstance().getModContainer("wynnventory");
    public static String NAME;
    public static String VERSION;
    public static boolean IS_DEV;

    public static boolean init() {

        if (INSTANCE.isEmpty()) {
            logError("Failed to find mod container.");
            return false;
        }

        NAME = INSTANCE.get().getMetadata().getName();
        VERSION = INSTANCE.get().getMetadata().getVersion().getFriendlyString();
        IS_DEV = VERSION.contains("dev");

        return true;
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
}