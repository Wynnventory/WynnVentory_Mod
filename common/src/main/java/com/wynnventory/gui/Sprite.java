package com.wynnventory.gui;

import net.minecraft.resources.Identifier;

public enum Sprite {
    RELOAD_BUTTON("gui/reload.png", 64, 32),
    SETTINGS_BUTTON("gui/settings.png", 40, 20),
    MYTHIC_ICON("gui/box_mythic.png", 16, 16),
    FABLED_ICON("gui/box_fabled.png", 16, 16),
    LEGENDARY_ICON("gui/box_legendary.png", 16, 16),
    RARE_ICON("gui/box_rare.png", 16, 16),
    UNIQUE_ICON("gui/box_unique.png", 16, 16),
    COMMON_ICON("gui/box_normal.png", 16, 16),
    SET_ICON("gui/box_set.png", 16, 16),
    CHEST_SLOT("gui/chest_slot.png", 18, 18);

    private final Identifier resource;
    private final int width;
    private final int height;


    Sprite(String name, int width, int height) {
        this.resource = Identifier.fromNamespaceAndPath("wynnventory", "textures/" + name);
        this.width = width;
        this.height = height;
    }

    public Identifier resource() {
        return resource;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}