package com.wynnventory.core.feature.updater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.util.ChatUtils;
import com.wynnventory.util.HttpUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ModUpdater {
    private static final String MODRINTH_UPDATE_API = "https://api.modrinth.com/v2/version_file/%s/update";
    private static final String HASH_ALGORITHM = "SHA-1";

    private ModUpdater() {}

    public static void checkForUpdates() {
        if (WynnventoryMod.isBeta()) {
            ChatUtils.info(Component.translatable("feature.wynnventory.update.betaNotification"));
            return;
        }

        File currentFile = WynnventoryMod.getModFile();
        if (currentFile == null || !currentFile.exists()) {
            WynnventoryMod.logError("Failed to find current mod file");
            return;
        }

        try {
            String url = String.format(MODRINTH_UPDATE_API, getFileHash(WynnventoryMod.getModFile()));

            HttpUtils.sendPostRequest(new URI(url), new UpdateRequest()).thenAccept(resp -> {
                if (resp.statusCode() != 200) {
                    return;
                }

                try {
                    UpdateResponse updateResp = new ObjectMapper().readValue(resp.body(), UpdateResponse.class);

                    String replacedVersion = updateResp.versionNumber.replace("v", "");
                    if (!WynnventoryMod.getVersion().equalsIgnoreCase(replacedVersion)) {
                        ChatUtils.info(Component.translatable(
                                "feature.wynnventory.update.notifyUserOfUpdate", replacedVersion));
                        downloadArtifact(updateResp.files.getFirst());
                        scheduleFileReplacementOnShutdown(WynnventoryMod.getModFile());
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

    private static void downloadArtifact(Artifact artifact) throws Exception {
        URI uri = new URI(artifact.url);
        Path newFilePath = getModFilePath(artifact.filename);
        Files.copy(uri.toURL().openStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static Path getModFilePath(String fileName) {
        return new File(Minecraft.getInstance().gameDirectory, "mods/" + fileName).toPath();
    }

    private static void scheduleFileReplacementOnShutdown(File oldJar) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.deleteIfExists(oldJar.toPath());
            } catch (IOException e) {
                WynnventoryMod.logError("Failed to delete old mod file", e);
                throw new RuntimeException(e);
            }
        }));
    }
}
