package com.wynnventory.ui;

import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.powder.GuidePowderItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.MathUtils;
import com.wynntils.utils.render.FontRenderer;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.api.WynncraftAPI;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.model.item.info.AspectInfo;
import com.wynnventory.model.screen.GuideAspectItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
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
    private EditBox searchBar;

    private List<Lootpool> raidpools = new ArrayList<>();
    private List<Lootpool> lootrunpools = new ArrayList<>();

    // Constants for layout spacing
    private static final int PADDING = 50;
    private static final int ITEM_SIZE = 16; // Standard item size (16x16 pixels)
    private static final int ITEMS_PER_ROW = 5; // Number of items per row
    private static final int ITEM_PADDING = 8; // Padding between items
    private static final int COL_WIDTH = (ITEM_SIZE * ITEMS_PER_ROW) + (ITEM_PADDING * ITEMS_PER_ROW);
    private static final int GAP_BETWEEN_BUTTONS_AND_SEARCH = 10;
    private static final int GAP_BETWEEN_SEARCH_AND_TITLES = 30; 
    private static final int GAP_BELOW_TITLES = 10;

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

        // Create the search bar with initial position and size
        searchBar = new EditBox(this.font, 0, 0, 100, 20, Component.literal(""));
        searchBar.setMaxLength(50);
        searchBar.setValue("");
//        searchBar.setMessage(Component.literal("Search"));
//        searchBar.setFocused(false);
        searchBar.setResponder(text -> updateScreen()); // Update screen when text changes
        this.addRenderableWidget(searchBar);

        // Build the initial screen based on the current pool
        updateScreen();
    }

    private void updateScreen() {
        // Clear existing buttons and elements except for the tab buttons and search bar
        this.clearWidgets();
        this.addRenderableWidget(lootrunButton);
        this.addRenderableWidget(raidButton);
        this.addRenderableWidget(searchBar);
        elementButtons.clear();

        // Update tab button styles
        updateTabButtonStyles();

        // Get the pools based on the current pool type
        List<Lootpool> pools = currentPool == PoolType.LOOTRUN ? lootrunpools : raidpools;

        // Get the search query
        String query = searchBar.getValue().trim().toLowerCase();

        // Calculate total width to center the columns
        int totalColumns = pools.size();
        int totalWidth = totalColumns * COL_WIDTH + (totalColumns - 1) * PADDING;
        int startX = (this.width - totalWidth) / 2;

        // Update the search bar position and size
        int searchBarY = lootrunButton.getY() + lootrunButton.getHeight() + GAP_BETWEEN_BUTTONS_AND_SEARCH; // Position below the tab buttons with spacing

        searchBar.setX(startX);
        searchBar.setY(searchBarY);
        searchBar.setWidth(totalWidth);

        // Calculate starting Y position for the columns, ensuring spacing
        int titlesY = searchBar.getY() + searchBar.getHeight() + GAP_BETWEEN_SEARCH_AND_TITLES; // Increased spacing for clear gap
        int startY = titlesY + this.font.lineHeight + GAP_BELOW_TITLES; // Position items below titles

        // CREATE COLS
        for (int i = 0; i < pools.size(); i++) {
            int gridX = startX + i * (COL_WIDTH + PADDING); // Starting X position
            buildColumn(pools.get(i), gridX, startY, query);
        }
    }

    private void buildColumn(Lootpool pool, int startX, int startY, String query) {
        int renderedItems = 0;

        List<LootpoolItem> items = new ArrayList<>(pool.getItems());
        for (LootpoolItem item : items) {
            String itemName = item.getName();

            // Filter items based on the search query
            if (!itemName.toLowerCase().contains(query)) {
                continue; // Skip items that don't match the query
            }

            int x = startX + (renderedItems % ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING);
            int y = startY + (renderedItems / ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING);

            List<GuideItemStack> matchingStacks = stacksByName.get(itemName);

            if (matchingStacks != null && !matchingStacks.isEmpty()) {
                for (GuideItemStack stack : matchingStacks) {
                    WynnventoryButton button = new WynnventoryButton(x, y, ITEM_SIZE, ITEM_SIZE, stack, this);
                    elementButtons.add(button);
                    this.addRenderableWidget(button);

                    renderedItems++;
                }
            }
        }
    }

    private void updateTabButtonStyles() {
        lootrunButton.active = currentPool != PoolType.LOOTRUN;
        raidButton.active = currentPool != PoolType.RAID;
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
        int totalWidth = totalColumns * COL_WIDTH + (totalColumns - 1) * PADDING;
        int startX = (this.width - totalWidth) / 2;

        // Calculate positions for titles and items
        int titlesY = searchBar.getY() + searchBar.getHeight() + GAP_BETWEEN_SEARCH_AND_TITLES; // Ensure consistent positioning
        int startY = titlesY + this.font.lineHeight + GAP_BELOW_TITLES; // Position items below titles

        // Render titles above each column
        for (int i = 0; i < pools.size(); i++) {
            int gridX = startX + i * (COL_WIDTH + PADDING); // Starting X position

            String title = pools.get(i).getRegion();
            guiGraphics.drawCenteredString(this.font, title, gridX + ((COL_WIDTH - ITEM_PADDING) / 2), titlesY, 0xFFFFFFFF);
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
        // Allow the search bar to receive keyboard input
        if (searchBar.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        // Close the screen when the keybind is pressed, but not if the search bar is focused
        if (WynnventoryMod.KEY_OPEN_CONFIG.matches(keyCode, scanCode) && !searchBar.isFocused()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        // Allow the search bar to receive character input
        if (searchBar.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    private void loadAllItems() {
        WynncraftAPI api = new WynncraftAPI();

        List<GuideGearItemStack> gear = Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList();
        List<GuideTomeItemStack> tomes = Models.Rewards.getAllTomeInfos().map(GuideTomeItemStack::new).toList();
        List<GuidePowderItemStack> powders = Models.Element.getAllPowderTierInfo().stream().map(GuidePowderItemStack::new).toList();
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

        for (GuidePowderItemStack stack : powders) {
            String element = stack.getElement().getName();
            String tier = MathUtils.toRoman(stack.getTier());
            String name = element + " Powder " + tier;
            stacksByName.computeIfAbsent(name, k -> new ArrayList<>()).add(stack);
        }
    }
}