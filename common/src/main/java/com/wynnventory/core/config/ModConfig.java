package com.wynnventory.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wynnventory.config.settings.*;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.core.config.settings.ColorSettings;
import com.wynnventory.core.config.settings.FavouriteNotifierSettings;
import com.wynnventory.core.config.settings.RaritySettings;
import com.wynnventory.core.config.settings.TooltipSettings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfig {
    private static final Path CFG_PATH = Path.of("config", "wynnventory.json");
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).enable(SerializationFeature.INDENT_OUTPUT);

    private static ModConfig instance;

    private TooltipSettings tooltipSettings = new TooltipSettings();
    private ColorSettings colorSettings = new ColorSettings();
    private FavouriteNotifierSettings favouriteNotifierSettings = new FavouriteNotifierSettings();
    private RaritySettings raritySettings = new RaritySettings();

    public static ModConfig get() {
        if (instance == null) {
            throw new IllegalStateException("ModConfig not initialized. Call ModConfig.init() first.");
        }
        return instance;
    }

    public static void init() {
        if (instance != null) return;

        synchronized (ModConfig.class) {
            if (instance == null) {
                instance = loadOrCreate();
            }
        }
    }

    /** Reloads config from disk */
    public static void reload() {
        synchronized (ModConfig.class) {
            instance = loadOrCreate();
        }
    }

    /**
     * Loads config from disk. If missing, returns defaults and writes them to disk.
     */
    private static ModConfig loadOrCreate() {
        try {
            if (Files.notExists(CFG_PATH)) {
                ModConfig cfg = new ModConfig();
                cfg.save();
                return cfg;
            }

            try (InputStream in = Files.newInputStream(CFG_PATH)) {
                return MAPPER.readValue(in, ModConfig.class);
            }
        } catch (Exception ex) {
            WynnventoryMod.logError("Failed to load config file", ex);
            try {
                Files.deleteIfExists(CFG_PATH);
            } catch (IOException i) {
                WynnventoryMod.logError("Failed to delete corrupted config file", i);
            }

            ModConfig cfg = new ModConfig();
            try {
                cfg.save();
            } catch (IOException i) {
                WynnventoryMod.logError("Failed to write default config file", i);
            }

            return cfg;
        }
    }

    public void save() throws IOException {
        Path parent = CFG_PATH.getParent();
        if (parent != null) Files.createDirectories(parent);

        // atomic-ish write: write to tmp then move
        Path tmp = CFG_PATH.resolveSibling(CFG_PATH.getFileName() + ".tmp");
        MAPPER.writeValue(tmp.toFile(), this);
        Files.move(tmp, CFG_PATH,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                java.nio.file.StandardCopyOption.ATOMIC_MOVE);
    }

    public static ModConfig getInstance() {
        return instance;
    }

    public static void setInstance(ModConfig instance) {
        ModConfig.instance = instance;
    }

    public TooltipSettings getTooltipSettings() {
        return tooltipSettings;
    }

    public void setTooltipSettings(TooltipSettings tooltipSettings) {
        this.tooltipSettings = tooltipSettings;
    }

    public ColorSettings getColorSettings() {
        return colorSettings;
    }

    public void setColorSettings(ColorSettings colorSettings) {
        this.colorSettings = colorSettings;
    }

    public FavouriteNotifierSettings getFavouriteNotifierSettings() {
        return favouriteNotifierSettings;
    }

    public void setFavouriteNotifierSettings(FavouriteNotifierSettings favouriteNotifierSettings) {
        this.favouriteNotifierSettings = favouriteNotifierSettings;
    }

    public RaritySettings getRaritySettings() {
        return raritySettings;
    }

    public void setRaritySettings(RaritySettings raritySettings) {
        this.raritySettings = raritySettings;
    }
}
