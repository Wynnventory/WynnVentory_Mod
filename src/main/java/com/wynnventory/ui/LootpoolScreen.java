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
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.ui.layout.LayoutHelper;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.LootpoolManager;
import com.wynnventory.util.PriceTooltipHelper;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
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

    private final Map<String, List<GuideItemStack>> stacksByName = new HashMap<>();
    private final List<WynnventoryItemButton<GuideItemStack>> elementButtons = new ArrayList<>();
    private final List<Button> filterToggles = new ArrayList<>();

    private Button lootrunButton;
    private Button raidButton;
    private ReloadButton reloadButton;
    private SettingsButton settingsButton;
    private EditBox searchBar;

    private LayoutHelper layoutHelper;

    private PoolType currentPool = PoolType.LOOTRUN;

    // Cache for current data to avoid recalculation
    private List<Lootpool> currentPools;
    private String currentQuery = "";

    public LootpoolScreen(Component title) {
        super(title);
        loadAllItems();
    }

    @Override
    protected void init() {
        super.init();
        layoutHelper = new LayoutHelper(this.width, this.height);
        initTabs();
        initSettingsButton();
        initReloadButton();
        initSearchBar();
        initFilters();
        updateScreen();
    }

    private void initTabs() {
        int width = 80;
        int height = 20;
        int spacing = 10;

        int[] tabPosition = layoutHelper.calculateTabPosition(width, spacing);
        int x = tabPosition[0];
        int y = tabPosition[1];

        lootrunButton = Button.builder(Component.literal("Lootruns"), b -> switchTo(PoolType.LOOTRUN))
                .bounds(x, y, width, height)
                .build();

        raidButton = Button.builder(Component.literal("Raids"), b -> switchTo(PoolType.RAID))
                .bounds(x + width + spacing, y, width, height)
                .build();

        addRenderableWidget(lootrunButton);
        addRenderableWidget(raidButton);
    }

    private void initSearchBar() {
        int width = 175;
        int height = 20;

        int[] position = layoutHelper.calculateSearchBarPosition(width, height, settingsButton.getWidth(), reloadButton.getWidth(), raidButton.getY());
        int x = position[0];
        int y = position[1];

        searchBar = new EditBox(this.font, x, y, width, height, Component.literal(""));
        searchBar.setMaxLength(50);
        searchBar.setResponder(text -> {
            // When search query changes, we need to update the screen
            // No need to invalidate pools as they don't change, just the filtering
            currentQuery = text.trim().toLowerCase();
            updateScreen();
        });
        addRenderableWidget(searchBar);
    }

    private void initReloadButton() {
        int buttonSize = 16;

        int[] position = layoutHelper.calculateReloadButtonPosition(buttonSize, settingsButton.getWidth(), raidButton.getHeight());
        int x = position[0];
        int y = position[1];

        reloadButton = new ReloadButton(x, y, () -> {
            LootpoolManager.reloadAllPools();
            // Invalidate cached pools when reloading
            currentPools = null;
            updateScreen();
        });

        addRenderableWidget(reloadButton);
    }

    private void initSettingsButton() {
        int buttonSize = 16;

        int[] position = layoutHelper.calculateSettingsButtonPosition(buttonSize, raidButton.getHeight());
        int x = position[0];
        int y = position[1];

        settingsButton = new SettingsButton(x, y, () -> Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(ConfigManager.class, this).get()));

        addRenderableWidget(settingsButton);
    }

    private void initFilters() {
        filterToggles.clear();
        int width = 80;
        int height = 20;
        int spacing = 5;

        int[] position = layoutHelper.calculateFilterTogglePosition(width, height, spacing, reloadButton.getY(), reloadButton.getHeight());
        int startX = position[0];
        int startY = position[1];
        int verticalStep = position[2];

        List<FilterToggle> filters = getFilterToggles();

        for (int i = 0; i < filters.size(); i++) {
            int y = startY + i * verticalStep;
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
                    // Invalidate cached pools when filter is changed
                    currentPools = null;
                    updateScreen();
                }).bounds(x, y, width, height).build();
    }

    private void switchTo(PoolType type) {
        currentPool = type;
        // Invalidate cached pools when switching pool types
        currentPools = null;
        updateScreen();
    }

    protected void updateScreen() {
        clearWidgets();
        elementButtons.clear();

        // Add fixed UI widgets.
        addRenderableWidget(searchBar);
        addRenderableWidget(reloadButton);
        addRenderableWidget(settingsButton);
        addRenderableWidget(lootrunButton);
        addRenderableWidget(raidButton);
        filterToggles.forEach(this::addRenderableWidget);

        updateTabButtonStyles();

        // Get pools and query, updating cached values
        currentPools = getCurrentPools();
        currentQuery = searchBar.getValue().trim().toLowerCase();

        // Calculate layout using the LayoutHelper
        layoutHelper.calculateItemLayout(currentPools, searchBar, filterToggles, currentQuery);

        // Build each column with positions calculated by LayoutHelper
        for (int i = 0; i < currentPools.size(); i++) {
            int[] columnPosition = layoutHelper.calculateColumnPosition(i);
            int colX = columnPosition[0];
            int colY = columnPosition[1];

            // Build column with positions calculated by LayoutHelper
            buildColumn(currentPools.get(i), colX, currentQuery);
        }
    }

    private void updateTabButtonStyles() {
        lootrunButton.active = currentPool != PoolType.LOOTRUN;
        raidButton.active = currentPool != PoolType.RAID;
    }

    private void buildColumn(Lootpool pool, int startX, String query) {
        var config = ConfigManager.getInstance();
        int rendered = 0;
        List<LootpoolItem> items = currentPool == PoolType.LOOTRUN ? pool.getLootrunSortedItems() : pool.getRaidSortedItems();
        for (LootpoolItem item : items) {
            String name = item.getName();
            if (!name.toLowerCase().contains(query)) continue;
            if (layoutHelper.matchesRarityFilters(item, config)) continue;

            List<GuideItemStack> stacks = stacksByName.get(name);
            if (stacks == null || stacks.isEmpty()) continue;

            for (GuideItemStack stack : stacks) {
                // Use LayoutHelper to calculate item position
                int[] itemPosition = layoutHelper.calculateItemPosition(startX, rendered);
                int x = itemPosition[0];
                int y = itemPosition[1];
                int buttonSize = itemPosition[2];

                WynnventoryItemButton<GuideItemStack> button = new WynnventoryItemButton<>(x, y, buttonSize, buttonSize, stack, item.isShiny());
                elementButtons.add(button);
                addRenderableWidget(button);
                rendered++;
            }
        }
    }


    private List<Lootpool> getCurrentPools() {
        // If we already have the pools cached and the pool type hasn't changed, return the cached pools
        if (currentPools != null) {
            return currentPools;
        }

        // Otherwise, get the pools from the manager
        return currentPool == PoolType.LOOTRUN
                ? LootpoolManager.getLootrunPools()
                : LootpoolManager.getRaidPools();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        // Use cached values instead of recalculating
        if (currentPools == null) {
            currentPools = getCurrentPools();
            currentQuery = searchBar.getValue().trim().toLowerCase();
            layoutHelper.calculateItemLayout(currentPools, searchBar, filterToggles, currentQuery);
        }

        // Draw region (panel) titles
        for (int i = 0; i < currentPools.size(); i++) {
            int[] titlePosition = layoutHelper.calculateColumnTitlePosition(i);
            int textX = titlePosition[0];
            int titleY = titlePosition[1];
            String regionName = currentPools.get(i).getRegion();
            g.drawCenteredString(this.font, regionName, textX, titleY, 0xFFFFFFFF);
        }

        // Render tooltips for hovered element buttons.
        for (WynnventoryItemButton<GuideItemStack> button : elementButtons) {
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
