package com.wynnventory.gui;

import net.minecraft.resources.Identifier;

public enum Sprite {
    RELOAD_BUTTON("gui/rewardscreenrewardscreenreload.png", 64, 32),
    SETTINGS_BUTTON("gui/rewardscreensettings.png", 40, 20),
    MYTHIC_ICON("gui/rewardscreenbox_mythic.png", 16, 16),
    FABLED_ICON("gui/rewardscreenbox_fabled.png", 16, 16),
    LEGENDARY_ICON("gui/rewardscreenbox_legendary.png", 16, 16),
    RARE_ICON("gui/rewardscreenbox_rare.png", 16, 16),
    UNIQUE_ICON("gui/rewardscreenbox_unique.png", 16, 16),
    COMMON_ICON("gui/rewardscreenbox_normal.png", 16, 16),
    SET_ICON("gui/rewardscreenbox_set.png", 16, 16),
    CHEST_SLOT("gui/rewardscreenchest_slot.png", 18, 18),
    LOOTRUN_POOL_TOP_SECTION("gui/rewardscreenlootrun_pool_top_section.png", 208, 69),
    RAID_POOL_TOP_SECTION("gui/rewardscreenraid_pool_top_section.png", 208, 69),
    POOL_MIDDLE_SECTION_HEADER("gui/rewardscreenpool_middle_section_header.png", 176, 41),
    POOL_MIDDLE_SECTION("gui/rewardscreenpool_middle_section.png", 176, 22),
    POOL_BOTTOM_SECTION("gui/rewardscreenpool_bottom_section.png", 176, 13),
    FILTER_SECTION("gui/rewardscreenfilter.png", 105, 58),
    ARROW_LEFT("gui/rewardscreenarrow_left.png", 64, 32),
    ARROW_RIGHT("gui/rewardscreenarrow_right.png", 64, 32),
    MYTHIC_ASPECT_DISPALY("gui/raidlobby/mythic_aspect_display.png", 69, 102);

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
