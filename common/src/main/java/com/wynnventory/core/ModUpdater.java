package com.wynnventory.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import com.wynntils.utils.FileUtils;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.model.github.Asset;
import com.wynnventory.model.github.Release;
import com.wynnventory.util.HttpUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;


public class ModUpdater {
    private static final String LATEST_RELEASE_URL = "https://api.github.com/repos/Wynnventory/Wynnventory_Mod/releases/latest";
    private static boolean alreadyChecked = false;

    private ModUpdater() {}

    public static void checkForUpdates() {
        if (alreadyChecked) {
            return;
        }

        alreadyChecked = true;

        if (WynnventoryMod.isDev()) {
            WynnventoryMod.logInfo("This is a dev build. Skipping auto update...");
        } else {
            String currentVersion = WynnventoryMod.getVersion();
            initiateUpdateCheck(currentVersion);
        }
    }

    private static void initiateUpdateCheck(String currentVersion) {
        new Thread(() -> {
            try {
                Release latestRelease = fetchLatestRelease().join();
                String latestVersion = sanitizeVersion(latestRelease.getTagName());

                if (!isUpToDate(currentVersion, latestVersion)) {
                    handleNewVersionFound(latestRelease, latestVersion);
                }
            } catch (Exception e) {
                WynnventoryMod.logError("Failed to check for updates.", e);
            }
        }).start();
    }

    private static CompletableFuture<Release> fetchLatestRelease() throws Exception {
        URI uri = new URI(LATEST_RELEASE_URL);
        ObjectMapper mapper = new ObjectMapper();

        return HttpUtils.sendGetRequest(uri)
                .thenApply(resp -> {
                    int code = resp.statusCode();
                    if (code < 200 || code >= 300) {
                        WynnventoryMod.logError("Failed to GET latest Wynnventory release. Code '{}', Reason '{}'", code, resp.body());
                    }
                    try {
                        return mapper.readValue(resp.body(), Release.class);
                    } catch (JsonProcessingException e) {
                        throw new JsonParseException("Failed to parse release JSON", e);
                    }
                });
    }

    private static String sanitizeVersion(String versionTag) {
        return versionTag.toLowerCase().replace("v", "");
    }

    private static boolean isUpToDate(String currentVersion, String latestVersion) {
        return currentVersion.equals(latestVersion);
    }

    private static void handleNewVersionFound(Release latestRelease, String latestVersion) {
        notifyUserOfUpdate(latestVersion);

        latestRelease.getAssets().stream()
                .filter(asset -> asset.getName().toLowerCase().startsWith("wynnventory") && asset.getName().endsWith(".jar"))
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

                File oldFile = WynnventoryMod.getModFile();
                scheduleFileReplacementOnShutdown(oldFile, newFilePath.toFile());
            } catch (Exception e) {
                WynnventoryMod.logError("Failed to download Wynnventory update", e);
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

    private static void scheduleFileReplacementOnShutdown(File oldJar, File newJar) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                replaceOldFileWithNew(oldJar, newJar);
            } catch (IOException e) {
                WynnventoryMod.logError("Cannot apply update!", e);
            }
        }));
    }

    private static void replaceOldFileWithNew(File oldJar, File newJar) throws IOException {
        if (!isValidJarFile(oldJar)) {
            WynnventoryMod.logWarn("Mod jar file not found or incorrect.");
            return;
        }

        FileUtils.copyFile(newJar, oldJar);
        if (newJar.delete()) {
            WynnventoryMod.logInfo("Successfully applied update!");
        } else {
            WynnventoryMod.logWarn("Failed to delete the new JAR file after copying.");
        }
    }

    private static boolean isValidJarFile(File jarFile) {
        return jarFile != null && jarFile.exists() && !jarFile.isDirectory();
    }
}
