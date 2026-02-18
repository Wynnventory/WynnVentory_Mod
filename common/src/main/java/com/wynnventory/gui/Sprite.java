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
    CHEST_SLOT("gui/chest_slot.png", 18, 18),
    LOOTRUN_POOL_TOP_SECTION("gui/lootrun_pool_top_section.png", 208, 69),
    RAID_POOL_TOP_SECTION("gui/raid_pool_top_section.png", 208, 69),
    POOL_MIDDLE_SECTION_HEADER("gui/pool_middle_section_header.png", 176, 41),
    POOL_MIDDLE_SECTION("gui/pool_middle_section.png", 176, 22),
    POOL_BOTTOM_SECTION("gui/pool_bottom_section.png", 176, 13),
    FILTER_SECTION("gui/filter.png", 105, 58),
    ARROW_LEFT("gui/arrow_left.png", 64, 32),
    ARROW_RIGHT("gui/arrow_right.png", 64, 32);

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
