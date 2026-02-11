package com.wynnventory.gui;

import net.minecraft.resources.Identifier;

public enum Sprite {
    RELOAD_BUTTON("gui/reload.png", 64, 32),
    SETTINGS_BUTTON("gui/settings.png", 40, 20);

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