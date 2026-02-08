package com.wynnventory.bootstrap;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixins;

/**
 * Pre-launch entrypoint for Wynnventory that handles conditional mixin configuration loading
 */
public class WynnventoryPreLaunch implements PreLaunchEntrypoint {

    // Use a dedicated bootstrap logger since ModInfo may not be safe to use this early (in case)
    private static final Logger LOGGER = LoggerFactory.getLogger("wynnventory/bootstrap");

    private static final String STANDARD_MIXIN_CONFIG = "wynnventory.mixins.json";
    private static final String LUNAR_MIXIN_CONFIG = "wynnventory.lunar.mixins.json";

    private static volatile String loadedConfig = null;

    @Override
    public void onPreLaunch() {
        try {
            loadMixinConfiguration();
        } catch (Exception e) {
            LOGGER.error("[Wynnventory] Critical error during mixin configuration loading", e);
            // Attempt fallback to standard config
            attemptFallbackLoad();
        }
    }

    /**
     * Loads the appropriate mixin configuration based on detected platform
     */
    private void loadMixinConfiguration() {
        boolean isLunar = LunarPlatformDetector.isLunarIchor();
        String selectedConfig = isLunar ? LUNAR_MIXIN_CONFIG : STANDARD_MIXIN_CONFIG;

        LOGGER.info("[Wynnventory] Platform detected: {}", isLunar ? "Lunar Client (Ichor/Genesis)" : "Standard Fabric");
        LOGGER.info("[Wynnventory] Selected mixin configuration: {}", selectedConfig);

        // here we register the mixin configuration
        Mixins.addConfiguration(selectedConfig);
        loadedConfig = selectedConfig;

        LOGGER.info("[Wynnventory] Successfully registered mixin configuration: {}", selectedConfig);
    }

    /**
     * Attempts to load the standard config as a fallback if detection fails
     */
    private void attemptFallbackLoad() {
        LOGGER.warn("[Wynnventory] Attempting fallback to standard mixin configuration");
        try {
            Mixins.addConfiguration(STANDARD_MIXIN_CONFIG);
            loadedConfig = STANDARD_MIXIN_CONFIG + " (fallback)";
            LOGGER.info("[Wynnventory] Fallback mixin configuration loaded successfully");
        } catch (Exception e) {
            LOGGER.error("[Wynnventory] Fallback mixin loading also failed - mod may not function correctly", e);
        }
    }

    /**
     * Returns the name of the loaded mixin configuration
     *
     * @return config name, or null if not yet loaded
     */
    public static String getLoadedConfigName() {
        return loadedConfig;
    }

    /**
     * Nested static class for Lunar/Ichor detection
     */
    private static final class LunarPlatformDetector {

        // there is the known Lunar Client / Ichor class signatures
        private static final String[] LUNAR_CLASS_SIGNATURES = {
                "com.moonsworth.lunar.ichor.Ichor",
                "com.moonsworth.lunar.genesis.Genesis", 
                "com.moonsworth.lunar.client.Lunar",
                "com.moonsworth.lunar.common.LunarCommon"
        };

        private static Boolean cachedResult = null;

        /**
         * Detects if running on Lunar Client with Ichor/Genesis
         *
         * @return true if Lunar Client environment is detected
         */
        static boolean isLunarIchor() {
            if (cachedResult != null) {
                return cachedResult;
            }

            cachedResult = detectLunarEnvironment();
            return cachedResult;
        }

        private static boolean detectLunarEnvironment() {
            ClassLoader classLoader = LunarPlatformDetector.class.getClassLoader();
            
            for (String className : LUNAR_CLASS_SIGNATURES) {
                if (classExists(className, classLoader)) {
                    LOGGER.debug("[Wynnventory] Detected Lunar class: {}", className);
                    return true;
                }
            }

            return false;
        }

        /**
         * Checks if a class exists without initializing it
         *
         * @param className   fully qualified class name
         * @param classLoader the class loader to use
         * @return true if the class can be found
         */
        private static boolean classExists(String className, ClassLoader classLoader) {
            try {
                Class.forName(className, false, classLoader);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            } catch (LinkageError e) {
                // still indicates Lunar environment
                LOGGER.debug("[Wynnventory] LinkageError for {}: {} (treating as present)", className, e.getMessage());
                return true;
            } catch (Exception e) {
                LOGGER.debug("[Wynnventory] Unexpected error checking for {}: {}", className, e.getMessage());
                return false;
            }
        }
    }
}
