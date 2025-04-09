package com.wynnventory.enums;

import net.minecraft.resources.ResourceLocation;

public enum Sprite {
    RELOAD_BUTTON("gui/reload.png", 64, 32);

    private final ResourceLocation resource;
    private final int width;
    private final int height;


    Sprite(String name, int width, int height) {
        this.resource = ResourceLocation.fromNamespaceAndPath("wynnventory", "textures/" + name);
        this.width = width;
        this.height = height;
    }

    public ResourceLocation resource() {
        return resource;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}