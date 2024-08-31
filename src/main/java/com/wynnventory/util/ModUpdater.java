package com.wynnventory.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wynntils.utils.FileUtils;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.model.github.Asset;
import com.wynnventory.model.github.Release;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ModUpdater {
    private static final String LATEST_RELEASE_URL = "https://api.github.com/repos/Aruloci/Wynnventory/releases/latest";
    private static boolean alreadyChecked = false;

    private ModUpdater() { }

    public static void checkForUpdates() {
        if (alreadyChecked) {
            return;
        }

        alreadyChecked = true;

        if (WynnventoryMod.WYNNVENTORY_INSTANCE.isEmpty()) {
            WynnventoryMod.error("Could not find Wynnventory in Fabric Loader!");
            return;
        }

        if (WynnventoryMod.isDev()) {
            WynnventoryMod.info("This is a dev build. Skipping auto update...");
        } else {
            String currentVersion = WynnventoryMod.WYNNVENTORY_VERSION;
            initiateUpdateCheck(currentVersion);
        }
    }

    private static void initiateUpdateCheck(String currentVersion) {
        new Thread(() -> {
            try {
                Release latestRelease = fetchLatestRelease();
                String latestVersion = sanitizeVersion(latestRelease.getTagName());

                if (!isUpToDate(currentVersion, latestVersion)) {
                    handleNewVersionFound(latestRelease, latestVersion);
                }
            } catch (Exception e) {
                WynnventoryMod.error("Failed to check for updates.", e);
            }
        }).start();
    }

    private static Release fetchLatestRelease() throws Exception {
        URI uri = new URI(LATEST_RELEASE_URL);
        HttpResponse<String> response = HttpUtil.sendHttpGetRequest(uri);
        return new ObjectMapper().readValue(response.body(), Release.class);
    }

    private static String sanitizeVersion(String versionTag) {
        return versionTag.toLowerCase().replace("v", "");
    }

    private static boolean isUpToDate(String currentVersion, String latestVersion) {
        return currentVersion.equals(latestVersion);
    }

    private static void handleNewVersionFound(Release latestRelease, String latestVersion) {
        notifyUserOfUpdate(latestVersion);

        String modName = WynnventoryMod.WYNNVENTORY_MOD_NAME;
        latestRelease.getAssets().stream()
                .filter(asset -> asset.getName().startsWith(modName) && asset.getName().endsWith(".jar"))
                .forEach(ModUpdater::downloadAndApplyUpdate);
    }

    private static void notifyUserOfUpdate(String latestVersion) {
        Component message = Component.literal("[Wynnventory] New version available: " + latestVersion + ". Attempting to auto-update...")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
        McUtils.sendMessageToClient(message);
    }

    private static void downloadAndApplyUpdate(Asset asset) {
        new Thread(() -> {
            try {
                Path newFilePath = downloadAsset(asset);
                notifyUserOfDownloadCompletion();

                File oldFile = getExistingModFile();
                scheduleFileReplacementOnShutdown(oldFile, newFilePath.toFile());
            } catch (Exception e) {
                WynnventoryMod.error("Failed to download Wynnventory update", e);
            }
        }).start();
    }

    private static Path downloadAsset(Asset asset) throws Exception {
        URI uri = new URI(asset.getBrowserDownloadUrl());
        Path newFilePath = getModFilePath(asset.getName());
        Files.copy(uri.toURL().openStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
        return newFilePath;
    }

    private static Path getModFilePath(String fileName) {
        return new File(Minecraft.getInstance().gameDirectory, "mods/" + fileName).toPath();
    }

    private static void notifyUserOfDownloadCompletion() {
        Component message = Component.literal("[Wynnventory] Download completed! Restart Minecraft to apply the update.")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
        McUtils.sendMessageToClient(message);
    }

    private static File getExistingModFile() {
        return new File(WynnventoryMod.WYNNVENTORY_INSTANCE.get().getOrigin().getPaths().getFirst().toUri());
    }

    private static void scheduleFileReplacementOnShutdown(File oldJar, File newJar) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                replaceOldFileWithNew(oldJar, newJar);
            } catch (IOException e) {
                WynnventoryMod.error("Cannot apply update!", e);
            }
        }));
    }

    private static void replaceOldFileWithNew(File oldJar, File newJar) throws IOException {
        if (!isValidJarFile(oldJar)) {
            WynnventoryMod.warn("Mod jar file not found or incorrect.");
            return;
        }

        FileUtils.copyFile(newJar, oldJar);
        if (newJar.delete()) {
            WynnventoryMod.info("Successfully applied update!");
        } else {
            WynnventoryMod.warn("Failed to delete the new JAR file after copying.");
        }
    }

    private static boolean isValidJarFile(File jarFile) {
        return jarFile != null && jarFile.exists() && !jarFile.isDirectory();
    }
}