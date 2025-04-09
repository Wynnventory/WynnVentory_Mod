package com.wynnventory.ui;

import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.aspect.GuideAspectItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.powder.GuidePowderItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.MathUtils;
import com.wynntils.utils.render.FontRenderer;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.enums.PoolType;
import com.wynnventory.input.KeyBindingManager;
import com.wynnventory.model.item.GroupedLootpool;
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
import org.apache.commons.lang3.function.BooleanConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class LootpoolScreen extends Screen {

    private static final int ITEM_SIZE = 16;
    private static final int ITEMS_PER_ROW = 5;
    private static final int ITEM_PADDING = 8;
    private static final int COL_WIDTH = (ITEM_SIZE * ITEMS_PER_ROW) + (ITEM_PADDING * ITEMS_PER_ROW);
    private static final int PANEL_PADDING = 20;
    private static final int GAP_TITLE_TO_ITEMS = 12;

    private final Map<String, List<GuideItemStack>> stacksByName = new HashMap<>();
    private final List<WynnventoryButton<GuideItemStack>> elementButtons = new ArrayList<>();
    private final List<Button> filterToggles = new ArrayList<>();

    private Button lootrunButton;
    private Button raidButton;
    private Button reloadButton;
    private EditBox searchBar;

    private int lastTitlesY;

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
        int width = 80;
        int height = 20;
        int spacing = 10;
        int totalWidth = 2 * width + spacing;
        int x = (this.width - totalWidth) / 2;

        lootrunButton = Button.builder(Component.literal("Lootruns"), b -> switchTo(PoolType.LOOTRUN))
                .bounds(x, 10, width, height)
                .build();

        raidButton = Button.builder(Component.literal("Raids"), b -> switchTo(PoolType.RAID))
                .bounds(x + width + spacing, 10, width, height)
                .build();

        addRenderableWidget(lootrunButton);
        addRenderableWidget(raidButton);
    }

    private void initSearchBar() {
        int width = 175;
        int height = 20;
        int x = this.width - width - 50;
        int y = 40;

        searchBar = new EditBox(this.font, x, y, width, height, Component.literal(""));
        searchBar.setMaxLength(50);
        searchBar.setResponder(text -> updateScreen());
        addRenderableWidget(searchBar);
    }

    private void initReloadButton() {
        int gap = 5;
        int x = searchBar.getX() + searchBar.getWidth() + gap;
        int y = searchBar.getY();

        reloadButton = Button.builder(Component.literal("↻"), b -> {
            LootpoolManager.reloadAllPools();
            updateScreen();
        }).bounds(x, y, 20, 20).build();

        addRenderableWidget(reloadButton);
    }

    private void initFilters() {
        filterToggles.clear();
        int width = 80;
        int height = 20;
        int spacing = 5;
        int startX = reloadButton.getX() + reloadButton.getWidth() - width;
        int startY = reloadButton.getY() + reloadButton.getHeight() + 6;

        List<FilterToggle> filters = getFilterToggles();

        for (int i = 0; i < filters.size(); i++) {
            int y = startY + i * (height + spacing);
            FilterToggle toggle = filters.get(i);
            Button button = createToggleButton(toggle.label, toggle.getter, toggle.setter, startX, y, width, height);
            filterToggles.add(button);
            addRenderableWidget(button);
        }
    }

    private static @NotNull List<FilterToggle> getFilterToggles() {
        var cfg = ConfigManager.getInstance().getRarityConfig();

        return List.of(
                new FilterToggle("Mythics", cfg::getShowMythic, cfg::setShowMythic),
                new FilterToggle("Fableds", cfg::getShowFabled, cfg::setShowFabled),
                new FilterToggle("Legendaries", cfg::getShowLegendary, cfg::setShowLegendary),
                new FilterToggle("Uniques", cfg::getShowUnique, cfg::setShowUnique),
                new FilterToggle("Rares", cfg::getShowRare, cfg::setShowRare),
                new FilterToggle("Common", cfg::getShowCommon, cfg::setShowCommon),
                new FilterToggle("Set", cfg::getShowSet, cfg::setShowSet)
        );
    }

    private Button createToggleButton(String label, BooleanSupplier getter, BooleanConsumer setter,
                                      int x, int y, int width, int height) {
        return Button.builder(
                Component.literal(label + ": " + (getter.getAsBoolean() ? "On" : "Off")),
                b -> {
                    boolean newValue = !getter.getAsBoolean();
                    setter.accept(newValue);
                    b.setMessage(Component.literal(label + ": " + (newValue ? "On" : "Off")));
                    AutoConfig.getConfigHolder(ConfigManager.class).save();
                    updateScreen();
                }).bounds(x, y, width, height).build();
    }

    private void switchTo(PoolType type) {
        currentPool = type;
        updateScreen();
    }

    protected void updateScreen() {
        clearWidgets();
        elementButtons.clear();

        // Add fixed UI widgets.
        addRenderableWidget(searchBar);
        addRenderableWidget(reloadButton);
        addRenderableWidget(lootrunButton);
        addRenderableWidget(raidButton);
        filterToggles.forEach(this::addRenderableWidget);

        updateTabButtonStyles();

        List<GroupedLootpool> pools = getCurrentPools();
        String query = searchBar.getValue().trim().toLowerCase();

        // --- Horizontal scaling ---
        // Left boundary is fixed; right boundary is based on the filter buttons (if available) or fallback to search bar.
        int leftBoundary = 20;
        int rightBoundary = filterToggles.isEmpty() ? searchBar.getX() - 10 : filterToggles.get(0).getX() - 10;
        int availableColumnsWidth = rightBoundary - leftBoundary;
        // Calculate the desired total width of the columns.
        int totalWidth = pools.size() * COL_WIDTH + (pools.size() - 1) * PANEL_PADDING;
        float horizontalScale = 1.0f;
        if (totalWidth > availableColumnsWidth) {
            horizontalScale = availableColumnsWidth / (float) totalWidth;
        }

        // --- Vertical scaling ---
        // Determine where the columns start.
        lastTitlesY = searchBar.getY() + searchBar.getHeight() + 10;
        // We'll assume a fixed bottom margin.
        int bottomMargin = 10;
        int availableVertical = this.height - lastTitlesY - bottomMargin;

        // For each column, count the items that match the query and filtering, then compute the required (unscaled) height.
        int maxColumnHeightUnscaled = 0;
        for (GroupedLootpool pool : pools) {
            int rendered = 0;
            for (LootpoolGroup group : pool.getGroupItems()) {
                for (LootpoolItem item : group.getLootItems()) {
                    String name = item.getName();
                    if (!name.toLowerCase().contains(query)) continue;
                    if (!matchesRarityFilters(item, ConfigManager.getInstance())) continue;
                    // For each matching item, assume it contributes one button.
                    // (If there are multiple stacks per item, buildColumn() will add them one by one.)
                    rendered++;
                }
            }
            if (rendered > 0) {
                // Compute number of rows for this column.
                int rows = (rendered + ITEMS_PER_ROW - 1) / ITEMS_PER_ROW;
                // Unscaled column height: for rows, each row is ITEM_SIZE, plus (rows-1) gaps of ITEM_PADDING.
                int colHeight = rows * ITEM_SIZE + (rows - 1) * ITEM_PADDING;
                maxColumnHeightUnscaled = Math.max(maxColumnHeightUnscaled, colHeight);
            }
        }
        float verticalScale = 1.0f;
        if (maxColumnHeightUnscaled > availableVertical && availableVertical > 0) {
            verticalScale = availableVertical / (float) maxColumnHeightUnscaled;
        }

        // Use the smaller of the two scales.
        float overallScale = Math.min(horizontalScale, verticalScale);

        int scaledTotalWidth = Math.round(totalWidth * overallScale);
        // Center the columns within the horizontal available space.
        int startX = leftBoundary + (availableColumnsWidth - scaledTotalWidth) / 2;

        // Determine starting Y for the columns.
        int lastStartY = lastTitlesY + this.font.lineHeight + GAP_TITLE_TO_ITEMS;

        // Build each column with positions scaled by overallScale.
        for (int i = 0; i < pools.size(); i++) {
            int colX = startX + Math.round(i * (COL_WIDTH + PANEL_PADDING) * overallScale);
            // Pass overallScale to buildColumn so that button positions and sizes are scaled.
            buildColumn(pools.get(i), colX, lastStartY, query, overallScale);
        }
    }

    private void updateTabButtonStyles() {
        lootrunButton.active = currentPool != PoolType.LOOTRUN;
        raidButton.active = currentPool != PoolType.RAID;
    }

    private void buildColumn(GroupedLootpool pool, int startX, int startY, String query, float scale) {
        var config = ConfigManager.getInstance();
        int rendered = 0;

        for (LootpoolGroup group : pool.getGroupItems()) {
            for (LootpoolItem item : group.getLootItems()) {
                String name = item.getName();
                if (!name.toLowerCase().contains(query)) continue;
                if (!matchesRarityFilters(item, config)) continue;

                // Scale positions using the overall scale factor.
                int x = startX + Math.round((rendered % ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING) * scale);
                int y = startY + Math.round((rendered / ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING) * scale);
                List<GuideItemStack> stacks = stacksByName.get(name);
                if (stacks == null || stacks.isEmpty()) continue;

                for (GuideItemStack stack : stacks) {
                    int buttonSize = Math.round(ITEM_SIZE * scale);
                    WynnventoryButton<GuideItemStack> button = new WynnventoryButton<>(x, y, buttonSize, buttonSize, stack, this, item.isShiny());
                    elementButtons.add(button);
                    addRenderableWidget(button);
                    rendered++;
                }
            }
        }
    }

    private boolean matchesRarityFilters(LootpoolItem item, ConfigManager config) {
        String rarity = item.getRarity().toLowerCase();
        var filter = config.getRarityConfig();

        return switch (rarity) {
            case "mythic" -> filter.getShowMythic();
            case "fabled" -> filter.getShowFabled();
            case "legendary" -> filter.getShowLegendary();
            case "unique" -> filter.getShowUnique();
            case "rare" -> filter.getShowRare();
            case "common" -> filter.getShowCommon();
            case "set" -> filter.getShowSet();
            default -> true;
        };
    }

    private List<GroupedLootpool> getCurrentPools() {
        return currentPool == PoolType.LOOTRUN
                ? LootpoolManager.getLootrunPools()
                : LootpoolManager.getRaidPools();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        List<GroupedLootpool> pools = getCurrentPools();
        // Use the same boundaries as in updateScreen().
        int leftBoundary = 20;
        int rightBoundary = filterToggles.isEmpty()
                ? searchBar.getX() - 10
                : filterToggles.get(0).getX() - 10;
        int availableColumnsWidth = rightBoundary - leftBoundary;
        int totalWidth = pools.size() * COL_WIDTH + (pools.size() - 1) * PANEL_PADDING;
        float horizontalScale = 1.0f;
        if (totalWidth > availableColumnsWidth) {
            horizontalScale = availableColumnsWidth / (float) totalWidth;
        }

        int bottomMargin = 20;
        int lastTitlesYLocal = searchBar.getY() + searchBar.getHeight() + 10;
        int availableVertical = this.height - lastTitlesYLocal - bottomMargin;

        int maxColumnHeightUnscaled = 0;
        String query = searchBar.getValue().trim().toLowerCase();
        for (GroupedLootpool pool : pools) {
            int rendered = 0;
            for (LootpoolGroup group : pool.getGroupItems()) {
                for (LootpoolItem item : group.getLootItems()) {
                    String name = item.getName();
                    if (!name.toLowerCase().contains(query)) continue;
                    if (!matchesRarityFilters(item, ConfigManager.getInstance())) continue;
                    rendered++;
                }
            }
            if (rendered > 0) {
                int rows = (rendered + ITEMS_PER_ROW - 1) / ITEMS_PER_ROW;
                int colHeight = rows * ITEM_SIZE + (rows - 1) * ITEM_PADDING;
                maxColumnHeightUnscaled = Math.max(maxColumnHeightUnscaled, colHeight);
            }
        }
        float verticalScale = 1.0f;
        if (maxColumnHeightUnscaled > availableVertical && availableVertical > 0) {
            verticalScale = availableVertical / (float) maxColumnHeightUnscaled;
        }

        float overallScale = Math.min(horizontalScale, verticalScale);
        int scaledTotalWidth = Math.round(totalWidth * overallScale);
        int startX = leftBoundary + (availableColumnsWidth - scaledTotalWidth) / 2;

        // Draw region (panel) titles using the same overallScale.
        for (int i = 0; i < pools.size(); i++) {
            int columnX = startX + Math.round(i * (COL_WIDTH + PANEL_PADDING) * overallScale);
            String regionName = pools.get(i).getRegion();
            int textX = columnX + Math.round(((COL_WIDTH - ITEM_PADDING) / 2f) * overallScale);
            g.drawCenteredString(this.font, regionName, textX, lastTitlesYLocal, 0xFFFFFFFF);
        }

        // Render tooltips for hovered element buttons.
        for (WynnventoryButton<GuideItemStack> button : elementButtons) {
            if (button.isHovered()) {
                g.renderTooltip(FontRenderer.getInstance().getFont(), button.getItemStack(), mouseX, mouseY);
                PriceTooltipHelper.renderPriceInfoTooltip(g, mouseX, mouseY, button.getItemStack(),
                        ItemStackUtils.getTooltips(button.getItemStack()), ConfigManager.getInstance().isAnchorTooltips());
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

    private void loadAllItems() {
        addStacks(Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList(), s -> s.getGearInfo().name());
        addStacks(Models.Rewards.getAllTomeInfos().map(GuideTomeItemStack::new).toList(), s -> s.getTomeInfo().name());
        addStacks(Models.Element.getAllPowderTierInfo().stream().map(GuidePowderItemStack::new).toList(),
                s -> s.getElement().getName() + " Powder " + MathUtils.toRoman(s.getTier()));
        addStacks(Models.Aspect.getAllAspectInfos().map(info -> new GuideAspectItemStack(info, 1)).toList(), s -> s.getAspectInfo().name());
    }

    private <T extends GuideItemStack> void addStacks(List<T> items, Function<T, String> nameMapper) {
        for (T item : items) {
            stacksByName.computeIfAbsent(nameMapper.apply(item), k -> new ArrayList<>()).add(item);
        }
    }

    private record FilterToggle(String label, BooleanSupplier getter, BooleanConsumer setter) {
    }
}
