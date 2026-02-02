package com.wynnventory.util;

import com.wynnventory.core.ModInfo;

/**
 * Detects the runtime platform to determine environment-specific behavior
 */
public final class PlatformDetector {

    // Known Lunar Client / Ichor class signatures
    private static final String[] LUNAR_CLASS_SIGNATURES = {
            "com.moonsworth.lunar.ichor.Ichor",
            "com.moonsworth.lunar.genesis.Genesis",
            "com.moonsworth.lunar.client.Lunar",
            "com.moonsworth.lunar.common.LunarCommon"
    };

    private static Boolean isLunarClient = null;

    private PlatformDetector() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Detects if the mod is running on Lunar Client with Ichor/Genesis
     *
     * @return true if Lunar Client/Ichor is detected, false otherwise
     */
    public static boolean isLunarClient() {
        if (isLunarClient != null) {
            return isLunarClient;
        }

        isLunarClient = detectLunarClient();

        if (isLunarClient) {
            ModInfo.logInfo("Lunar Client/Ichor detected");
        } else {
            ModInfo.logDebug("Standard Fabric environment detected");
        }

        return isLunarClient;
    }

    /**
     * Alias for {@link #isLunarClient()} with more specific naming.
     *
     * @return true if Lunar/Ichor/Genesis environment is detected
     */
    public static boolean isLunarIchor() {
        return isLunarClient();
    }

    /**
     * Attempts to detect Lunar Client by checking for known Lunar/Ichor/Genesis classes
     *
     * @return true if any Lunar-specific class is found
     */
    private static boolean detectLunarClient() {
        ClassLoader classLoader = PlatformDetector.class.getClassLoader();

        for (String className : LUNAR_CLASS_SIGNATURES) {
            if (classExists(className, classLoader)) {
                ModInfo.logDebug("Detected Lunar class: " + className);
                return true;
            }
        }

        return false;
    }

    /**
     * Safely checks if a class exists without causing initialization or linkage errors
     *
     * @param className   fully qualified class name
     * @param classLoader the class loader to use
     * @return true if the class exists and can be loaded
     */
    private static boolean classExists(String className, ClassLoader classLoader) {
        try {
            Class.forName(className, false, classLoader);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (LinkageError e) {
            // still indicates Lunar environment
            ModInfo.logDebug("LinkageError for " + className + ": " + e.getMessage() + " (treating as present)");
            return true;
        } catch (Exception e) {
            ModInfo.logWarn("Unexpected error while checking for class " + className + ": " + e.getMessage());
            return false;
        }
    }
}
