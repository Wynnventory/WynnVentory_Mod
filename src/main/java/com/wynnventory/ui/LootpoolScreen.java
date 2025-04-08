package com.wynnventory.ui;

import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.powder.GuidePowderItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.MathUtils;
import com.wynntils.utils.render.FontRenderer;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.enums.PoolType;
import com.wynnventory.input.KeyBindingManager;
import com.wynnventory.model.item.GroupedLootpool;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolGroup;
import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.LootpoolManager;
import com.wynnventory.util.PriceTooltipHelper;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class LootpoolScreen extends Screen {
    // Layout constants
    private static final int ITEM_SIZE = 16;
    private static final int ITEMS_PER_ROW = 5;
    private static final int ITEM_PADDING = 8;
    private static final int COL_WIDTH = (ITEM_SIZE * ITEMS_PER_ROW) + (ITEM_PADDING * ITEMS_PER_ROW);
    private static final int PANEL_PADDING = 20;
    private static final int GAP_TITLE_TO_ITEMS = 12;
    private static final int FILTER_COUNT = 5;

    // Instance layout positions
    private int lastTitlesY;
    private int lastStartY;

    // Data & widget collections
    private final Map<String, List<GuideItemStack>> stacksByName = new HashMap<>();
    private final List<WynnventoryButton> elementButtons = new ArrayList<>();
    private final List<Button> filterToggles = new ArrayList<>();

    // Fixed widgets
    private Button lootrunButton;
    private Button raidButton;
    private Button reloadButton;
    private EditBox searchBar;

    // Current state
    private PoolType currentPool = PoolType.LOOTRUN;

    public LootpoolScreen(Component title) {
        super(title);
        loadAllItems();
    }

    @Override
    protected void init() {
        super.init();
        initTabs();
        initSearchBar();
        initReloadButton();
        initFilters();
        updateScreen();
    }

    private void initTabs() {
        final int buttonWidth = 80, buttonHeight = 20, y = 10, spacing = 10;
        int totalWidth = 2 * buttonWidth + spacing;
        int x = (this.width - totalWidth) / 2;

        lootrunButton = Button.builder(Component.literal("Lootruns"), b -> switchTo(PoolType.LOOTRUN))
                .bounds(x, y, buttonWidth, buttonHeight)
                .build();

        raidButton = Button.builder(Component.literal("Raids"), b -> switchTo(PoolType.RAID))
                .bounds(x + buttonWidth + spacing, y, buttonWidth, buttonHeight)
                .build();

        addRenderableWidget(lootrunButton);
        addRenderableWidget(raidButton);
    }

    /**
     * Creates and adds rarity filter buttons using a common helper to reduce duplication.
     */
    private void initFilters() {
        filterToggles.clear();
        final int toggleWidth = 80, toggleHeight = 20, spacing = 5;

        // Align filters to the right edge of the reload button
        int startX = reloadButton.getX() + reloadButton.getWidth() - toggleWidth;
        int startY = reloadButton.getY() + reloadButton.getHeight() + 6; // slight gap below reload

        final ConfigManager configManager = ConfigManager.getInstance();
        final var rarityConfig = configManager.getRarityConfig();

        // List of filters with their labels and corresponding getter/setter lambdas.
        List<FilterToggle> filters = List.of(
                new FilterToggle("Mythics", rarityConfig::getShowMythic, rarityConfig::setShowMythic),
                new FilterToggle("Fableds", rarityConfig::getShowFabled, rarityConfig::setShowFabled),
                new FilterToggle("Legendaries", rarityConfig::getShowLegendary, rarityConfig::setShowLegendary),
                new FilterToggle("Uniques", rarityConfig::getShowUnique, rarityConfig::setShowUnique),
                new FilterToggle("Rares", rarityConfig::getShowRare, rarityConfig::setShowRare),
                new FilterToggle("Common", rarityConfig::getShowCommon, rarityConfig::setShowCommon),
                new FilterToggle("Set", rarityConfig::getShowSet, rarityConfig::setShowSet)
        );

        for (int i = 0; i < filters.size(); i++) {
            int toggleY = startY + i * (toggleHeight + spacing);
            FilterToggle toggle = filters.get(i);
            Button button = createToggleButton(toggle.label, toggle.getter, toggle.setter, startX, toggleY, toggleWidth, toggleHeight);
            addRenderableWidget(button);
            filterToggles.add(button);
        }
    }

    // Helper class to encapsulate the configuration for a filter toggle.
    private static class FilterToggle {
        final String label;
        final Supplier<Boolean> getter;
        final Consumer<Boolean> setter;

        FilterToggle(String label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
            this.label = label;
            this.getter = getter;
            this.setter = setter;
        }
    }

    // Helper method to create a toggle button.
    private Button createToggleButton(String label, Supplier<Boolean> getter, Consumer<Boolean> setter,
                                      int x, int y, int width, int height) {
        return Button.builder(
                        Component.literal(label + ": " + (getter.get() ? "On" : "Off")),
                        button -> {
                            boolean newState = !getter.get();
                            setter.accept(newState);
                            updateScreen();
                            button.setMessage(Component.literal(label + ": " + (newState ? "On" : "Off")));
                            AutoConfig.getConfigHolder(ConfigManager.class).save();
                        }
                ).bounds(x, y, width, height)
                .build();
    }

    private void initSearchBar() {
        final int searchWidth = 175, searchHeight = 20;
        int x = this.width - searchWidth - 50;
        int y = 40;

        searchBar = new EditBox(this.font, x, y, searchWidth, searchHeight, Component.literal(""));
        searchBar.setMaxLength(50);
        searchBar.setResponder(text -> updateScreen());
        addRenderableWidget(searchBar);
    }

    private void initReloadButton() {
        final int reloadWidth = 20, reloadHeight = 20, gap = 5;
        int x = searchBar.getX() + searchBar.getWidth() + gap;
        int y = searchBar.getY();

        reloadButton = Button.builder(Component.literal("↻"), b -> {
                    LootpoolManager.reloadAllPools();
                    updateScreen();
                }).bounds(x, y, reloadWidth, reloadHeight)
                .build();

        addRenderableWidget(reloadButton);
    }

    private void switchTo(PoolType type) {
        currentPool = type;
        updateScreen();
    }

    /**
     * Rebuilds the screen layout and re-adds fixed and dynamic widgets.
     */
    private void updateScreen() {
        // Clear current widgets and reset element buttons list.
        this.clearWidgets();
        elementButtons.clear();

        // Re-add fixed widgets.
        addRenderableWidget(lootrunButton);
        addRenderableWidget(raidButton);
        addRenderableWidget(searchBar);
        addRenderableWidget(reloadButton);
        filterToggles.forEach(this::addRenderableWidget);

        updateTabButtonStyles();

        List<GroupedLootpool> pools = getCurrentPools();
        String query = searchBar.getValue().trim().toLowerCase();

        // Center layout: calculate overall width and starting position.
        int totalColumns = pools.size();
        int totalWidth = totalColumns * COL_WIDTH + (totalColumns - 1) * PANEL_PADDING;
        int startX = (this.width - totalWidth) / 2;

        // Calculate vertical positions based on the search bar and filter panel.
        lastTitlesY = searchBar.getY() + searchBar.getHeight() + 10;
        lastStartY = lastTitlesY + this.font.lineHeight + GAP_TITLE_TO_ITEMS;

        // Build each column for the pools.
        for (int i = 0; i < pools.size(); i++) {
            int gridX = startX + i * (COL_WIDTH + PANEL_PADDING);
            buildColumn(pools.get(i), gridX, lastStartY, query);
        }
    }

    private List<GroupedLootpool> getCurrentPools() {
        return currentPool == PoolType.LOOTRUN
                ? LootpoolManager.getLootrunPools()
                : LootpoolManager.getRaidPools();
    }

    private void updateTabButtonStyles() {
        lootrunButton.active = currentPool != PoolType.LOOTRUN;
        raidButton.active = currentPool != PoolType.RAID;
    }

    /**
     * Builds a single column of lootpool items. For each group within the pool, applies the search query and
     * rarity filters before placing an item on the grid.
     */
    private void buildColumn(GroupedLootpool pool, int startX, int startY, String query) {
        final ConfigManager config = ConfigManager.getInstance();
        int renderedItems = 0;

        for (LootpoolGroup group : pool.getGroupItems()) {
            for (LootpoolItem item : group.getLootItems()) {
                String itemNameLower = item.getName().toLowerCase();
                if (!itemNameLower.contains(query)) continue;
                if (!config.getRarityConfig().getShowMythic() && "mythic".equalsIgnoreCase(item.getRarity())) continue;
                if (!config.getRarityConfig().getShowFabled() && "fabled".equalsIgnoreCase(item.getRarity())) continue;
                if (!config.getRarityConfig().getShowLegendary() && "legendary".equalsIgnoreCase(item.getRarity())) continue;
                if (!config.getRarityConfig().getShowUnique() && "unique".equalsIgnoreCase(item.getRarity())) continue;
                if (!config.getRarityConfig().getShowRare() && "rare".equalsIgnoreCase(item.getRarity())) continue;
                if (!config.getRarityConfig().getShowCommon() && "common".equalsIgnoreCase(item.getRarity())) continue;
                if (!config.getRarityConfig().getShowSet() && "set".equalsIgnoreCase(item.getRarity())) continue;

                // Calculate grid position.
                int x = startX + (renderedItems % ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING);
                int y = startY + (renderedItems / ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING);

                List<GuideItemStack> stacks = stacksByName.get(item.getName());
                if (stacks == null || stacks.isEmpty()) continue;

                for (GuideItemStack stack : stacks) {
                    WynnventoryButton button = new WynnventoryButton(x, y, ITEM_SIZE, ITEM_SIZE, stack, this, item.isShiny());
                    elementButtons.add(button);
                    addRenderableWidget(button);
                    renderedItems++;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        List<GroupedLootpool> pools = getCurrentPools();
        int totalWidth = pools.size() * COL_WIDTH + (pools.size() - 1) * PANEL_PADDING;
        int startX = (this.width - totalWidth) / 2;

        // Render pool region titles and show a tooltip on hover.
        for (int i = 0; i < pools.size(); i++) {
            int columnX = startX + i * (COL_WIDTH + PANEL_PADDING);
            String regionName = pools.get(i).getRegion();
            int textWidth = this.font.width(regionName);
            int textX = columnX + ((COL_WIDTH - ITEM_PADDING) / 2);

            guiGraphics.drawCenteredString(this.font, regionName, textX, lastTitlesY, 0xFFFFFFFF);

            if (mouseX >= textX - textWidth / 2 && mouseX <= textX + textWidth / 2 &&
                    mouseY >= lastTitlesY && mouseY <= lastTitlesY + this.font.lineHeight) {
                guiGraphics.renderTooltip(this.font, Component.literal("LONG DESCRIPTION"), mouseX, mouseY);
            }
        }

        // Render tooltips for hovered element buttons.
        for (WynnventoryButton button : elementButtons) {
            if (button.isHovered()) {
                guiGraphics.renderTooltip(FontRenderer.getInstance().getFont(), button.getItemStack(), mouseX, mouseY);

                List<Component> tooltips = ItemStackUtils.getTooltips(button.getItemStack());
                PriceTooltipHelper.renderPriceInfoTooltip(guiGraphics, mouseX, mouseY, button.getItemStack(), tooltips, ConfigManager.getInstance().isAnchorTooltips());
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBar.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (KeyBindingManager.OPEN_POOLS.matches(keyCode, scanCode) && !searchBar.isFocused()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return searchBar.charTyped(chr, modifiers) || super.charTyped(chr, modifiers);
    }

    /**
     * Loads all item stacks from various models into the stacksByName map for later retrieval.
     */
    private void loadAllItems() {
        addStacks(
                Models.Gear.getAllGearInfos()
                        .map(GuideGearItemStack::new)
                        .toList(),
                stack -> stack.getGearInfo().name()
        );
        addStacks(
                Models.Rewards.getAllTomeInfos()
                        .map(GuideTomeItemStack::new)
                        .toList(),
                stack -> stack.getTomeInfo().name()
        );
        addStacks(
                Models.Element.getAllPowderTierInfo()
                        .stream()
                        .map(GuidePowderItemStack::new)
                        .toList(),
                stack -> stack.getElement().getName() + " Powder " + MathUtils.toRoman(stack.getTier())
        );
    }

    private <T extends GuideItemStack> void addStacks(List<T> items, Function<T, String> nameMapper) {
        for (T item : items) {
            stacksByName.computeIfAbsent(nameMapper.apply(item), k -> new ArrayList<>())
                    .add(item);
        }
    }
}