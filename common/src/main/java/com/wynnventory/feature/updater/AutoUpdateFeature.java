package com.wynnventory.feature.updater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wynntils.core.mod.event.WynncraftConnectionEvent;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.feature.joinmessage.MessageSeverity;
import com.wynnventory.feature.joinmessage.ServerJoinMessageFeature;
import com.wynnventory.util.HttpUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.io.FileUtils;

public class AutoUpdateFeature {
    private static final String MODRINTH_UPDATE_API = "https://api.modrinth.com/v2/version_file/%s/update";
    private static final String HASH_ALGORITHM = "SHA-1";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldStateChange(WynncraftConnectionEvent.Connected e) {
        checkForUpdates();
    }

    private static void checkForUpdates() {
        if (WynnventoryMod.isBeta()) {
            ServerJoinMessageFeature.queueMessage(MessageSeverity.INFO, "feature.wynnventory.update.betaNotification");
            return;
        }

        File currentFile = WynnventoryMod.getModFile();
        if (currentFile == null || !currentFile.exists()) {
            WynnventoryMod.logError("Failed to find current mod file");
            return;
        }

        try {
            String url = String.format(MODRINTH_UPDATE_API, getFileHash(currentFile));

            HttpUtils.sendPostRequest(new URI(url), new UpdateRequest()).thenAccept(resp -> {
                if (resp.statusCode() != 200) {
                    return;
                }

                try {
                    UpdateResponse updateResp = new ObjectMapper().readValue(resp.body(), UpdateResponse.class);

                    String replacedVersion = updateResp.versionNumber.replace("v", "");
                    if (!WynnventoryMod.getVersion().equalsIgnoreCase(replacedVersion)) {
                        ServerJoinMessageFeature.queueMessage(
                                MessageSeverity.INFO,
                                Component.translatable(
                                        "feature.wynnventory.update.notifyUserOfUpdate", replacedVersion));

                        File updateJar = downloadArtifact(updateResp.files.getFirst());

                        ServerJoinMessageFeature.queueMessage(
                                MessageSeverity.INFO,
                                Component.translatable(
                                        "feature.wynnventory.update.notifyUserOfUpdate.success", replacedVersion));

                        scheduleFileReplacementOnShutdown(currentFile, updateJar);
                    }
                } catch (JsonProcessingException e) {
                    WynnventoryMod.logError("Failed to parse update response", e);
                } catch (Exception e) {
                    WynnventoryMod.logError("Failed to download update", e);
                }
            });

        } catch (Exception e) {
            WynnventoryMod.logError("Failed to evaluate update", e);
        }
    }

    private static String getFileHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }

        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static File downloadArtifact(Artifact artifact) throws IOException, URISyntaxException {
        URI uri = new URI(artifact.url);
        Path newFilePath = getModFilePath(artifact.filename);
        Files.copy(uri.toURL().openStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
        return newFilePath.toFile();
    }

    private static Path getModFilePath(String fileName) {
        return new File(Minecraft.getInstance().gameDirectory, "mods/" + fileName).toPath();
    }

    private static void scheduleFileReplacementOnShutdown(File oldJar, File newJar) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (oldJar == null || !oldJar.exists() || oldJar.isDirectory()) {
                    WynnventoryMod.logError("Mod jar file not found or incorrect.");
                    return;
                }

                FileUtils.copyFile(newJar, oldJar);
                Files.deleteIfExists(newJar.toPath());

                WynnventoryMod.logInfo("Successfully applied update!");
            } catch (IOException e) {
                WynnventoryMod.logError("Failed to delete old mod file", e);
            }
        }));
    }
}
