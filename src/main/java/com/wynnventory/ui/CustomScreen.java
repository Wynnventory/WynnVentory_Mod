package com.wynnventory.ui;

import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.render.FontRenderer;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.api.WynncraftAPI;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.model.item.info.AspectInfo;
import com.wynnventory.model.item.info.AspectTierInfo;
import com.wynnventory.model.screen.GuideAspectItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class CustomScreen extends Screen {
    Map<String, List<GuideItemStack>> stacksByName = new HashMap<>();

    private final List<WynnventoryButton> elementButtons = new ArrayList<>();
    private Button lootrunButton;
    private Button raidButton;

    private List<Lootpool> raidpools = new ArrayList<>();
    private List<Lootpool> lootrunpools = new ArrayList<>();

    private int padding = 50;
    private int itemSize = 16; // Item size (standard 16x16 pixels)
    private int itemsPerRow = 5; // Number of items per row
    private int itemPadding = 8; // Padding between items
    private int colWidth = (itemSize * itemsPerRow) + (itemPadding * itemsPerRow);

    private enum PoolType {
        LOOTRUN,
        RAID
    }

    private PoolType currentPool = PoolType.LOOTRUN;

    public CustomScreen(Component title) {
        super(title);

        WynnventoryAPI api = new WynnventoryAPI();
        raidpools = api.getLootpools("raidpool");
        lootrunpools = api.getLootpools("lootrun");

        loadAllItems();
    }

    @Override
    protected void init() {
        super.init();

        // Create tab buttons
        int tabButtonWidth = 80;
        int tabButtonHeight = 20;
        int tabButtonY = 10;
        int tabButtonSpacing = 10;

        int totalTabWidth = 2 * tabButtonWidth + tabButtonSpacing;
        int tabStartX = (this.width - totalTabWidth) / 2;

        // Create the Lootrun button
        lootrunButton = Button.builder(Component.literal("Lootruns"), button -> {
            currentPool = PoolType.LOOTRUN;
            updateScreen();
        }).bounds(tabStartX, tabButtonY, tabButtonWidth, tabButtonHeight).build();

        // Create the Raid button
        raidButton = Button.builder(Component.literal("Raids"), button -> {
            currentPool = PoolType.RAID;
            updateScreen();
        }).bounds(tabStartX + tabButtonWidth + tabButtonSpacing, tabButtonY, tabButtonWidth, tabButtonHeight).build();

        this.addRenderableWidget(lootrunButton);
        this.addRenderableWidget(raidButton);

        // Build the initial screen based on the current pool
        updateScreen();
    }

    private void updateScreen() {
        // Clear existing buttons and elements except for the tab buttons
        this.clearWidgets();
        this.addRenderableWidget(lootrunButton);
        this.addRenderableWidget(raidButton);
        elementButtons.clear();

        // Update tab button styles
        updateTabButtonStyles();

        // Get the pools based on the current pool type
        List<Lootpool> pools = currentPool == PoolType.LOOTRUN ? lootrunpools : raidpools;

        // Calculate total width to center the columns
        int totalColumns = pools.size();
        int totalWidth = totalColumns * colWidth + (totalColumns - 1) * padding;
        int startX = (this.width - totalWidth) / 2;

        // CREATE COLS
        for (int i = 0; i < pools.size(); i++) {
            int gridX = startX + i * (colWidth + padding); // Starting X position
            int gridY = 80; // Starting Y position

            int renderedItems = 0;

            List<LootpoolItem> items = new ArrayList<>(pools.get(i).getItems());
            for (LootpoolItem item : items) {
                int x = gridX + (renderedItems % itemsPerRow) * (itemSize + itemPadding);
                int y = gridY + (renderedItems / itemsPerRow) * (itemSize + itemPadding);

                String itemName = item.getName();
                List<GuideItemStack> matchingStacks = stacksByName.get(itemName);

                if (matchingStacks != null && !matchingStacks.isEmpty()) {
                    for (GuideItemStack stack : matchingStacks) {
                        WynnventoryButton button = new WynnventoryButton(x, y, itemSize, itemSize, stack, this);
                        elementButtons.add(button);
                        this.addRenderableWidget(button);

                        renderedItems++;
                    }
                }
            }
        }
    }

    private void updateTabButtonStyles() {
        if (currentPool == PoolType.LOOTRUN) {
            lootrunButton.active = false;
            raidButton.active = true;
        } else {
            lootrunButton.active = true;
            raidButton.active = false;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render the custom background
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Render tab buttons and other widgets
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Get the pools based on the current pool type
        List<Lootpool> pools = currentPool == PoolType.LOOTRUN ? lootrunpools : raidpools;

        // Calculate total width to center the columns
        int totalColumns = pools.size();
        int totalWidth = totalColumns * colWidth + (totalColumns - 1) * padding;
        int startX = (this.width - totalWidth) / 2;

        int gridY = 80; // Starting Y position
        for (int i = 0; i < pools.size(); i++) {
            int gridX = startX + i * (colWidth + padding); // Starting X position

            String title = pools.get(i).getRegion();
            guiGraphics.drawCenteredString(this.font, title, gridX + ((colWidth - itemPadding) / 2), gridY - this.font.lineHeight - 10, 0xFFFFFFFF);
        }

        // Render tooltips if necessary
        for (WynnventoryButton button : elementButtons) {
            if (button.isHovered()) {
                guiGraphics.renderTooltip(FontRenderer.getInstance().getFont(), button.getItemStack(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xFF202020, 0xFF000000);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (WynnventoryMod.KEY_OPEN_CONFIG.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void loadAllItems() {
        WynncraftAPI api = new WynncraftAPI();

        List<GuideGearItemStack> gear = Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList();
        List<GuideTomeItemStack> tomes = Models.Rewards.getAllTomeInfos().map(GuideTomeItemStack::new).toList();
        Map<String, AspectInfo> aspectInfos = api.fetchAllAspects();

        // Example: Iterate over tiers
        for (Map.Entry<String, AspectInfo> entry : aspectInfos.entrySet()) {
            stacksByName.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(new GuideAspectItemStack(entry.getValue()));
        }

        for (GuideGearItemStack stack : gear) {
            String name = stack.getGearInfo().name();
            stacksByName.computeIfAbsent(name, k -> new ArrayList<>()).add(stack);
        }

        for (GuideTomeItemStack stack : tomes) {
            String name = stack.getTomeInfo().name();
            stacksByName.computeIfAbsent(name, k -> new ArrayList<>()).add(stack);
        }
    }
}
