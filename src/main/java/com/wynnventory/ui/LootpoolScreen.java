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
        int x = this.width - width - 25;
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

    private void updateScreen() {
        clearWidgets();
        elementButtons.clear();

        addRenderableWidget(searchBar);
        addRenderableWidget(reloadButton);
        addRenderableWidget(lootrunButton);
        addRenderableWidget(raidButton);
        filterToggles.forEach(this::addRenderableWidget);

        updateTabButtonStyles();

        List<GroupedLootpool> pools = getCurrentPools();
        String query = searchBar.getValue().trim().toLowerCase();

        int totalWidth = pools.size() * COL_WIDTH + (pools.size() - 1) * PANEL_PADDING;
        int startX = (this.width - totalWidth) / 2;

        lastTitlesY = searchBar.getY() + searchBar.getHeight() + 10;
        int lastStartY = lastTitlesY + this.font.lineHeight + GAP_TITLE_TO_ITEMS;

        for (int i = 0; i < pools.size(); i++) {
            int x = startX + i * (COL_WIDTH + PANEL_PADDING);
            buildColumn(pools.get(i), x, lastStartY, query);
        }
    }

    private void updateTabButtonStyles() {
        lootrunButton.active = currentPool != PoolType.LOOTRUN;
        raidButton.active = currentPool != PoolType.RAID;
    }

    private void buildColumn(GroupedLootpool pool, int startX, int startY, String query) {
        var config = ConfigManager.getInstance();
        int rendered = 0;

        for (LootpoolGroup group : pool.getGroupItems()) {
            for (LootpoolItem item : group.getLootItems()) {
                String name = item.getName();
                if (!name.toLowerCase().contains(query)) continue;
                if (!matchesRarityFilters(item, config)) continue;

                int x = startX + (rendered % ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING);
                int y = startY + (rendered / ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING);

                List<GuideItemStack> stacks = stacksByName.get(name);
                if (stacks == null || stacks.isEmpty()) continue;

                for (GuideItemStack stack : stacks) {
                    WynnventoryButton<GuideItemStack> button = new WynnventoryButton<>(x, y, ITEM_SIZE, ITEM_SIZE, stack, this, item.isShiny());
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
        int totalWidth = pools.size() * COL_WIDTH + (pools.size() - 1) * PANEL_PADDING;
        int startX = (this.width - totalWidth) / 2;

        for (int i = 0; i < pools.size(); i++) {
            int columnX = startX + i * (COL_WIDTH + PANEL_PADDING);
            String regionName = pools.get(i).getRegion();
            int textX = columnX + ((COL_WIDTH - ITEM_PADDING) / 2);

            g.drawCenteredString(this.font, regionName, textX, lastTitlesY, 0xFFFFFFFF);

            int regionWidth = this.font.width(regionName);
            if (mouseX >= textX - regionWidth / 2 && mouseX <= textX + regionWidth / 2 &&
                    mouseY >= lastTitlesY && mouseY <= lastTitlesY + this.font.lineHeight) {
//                g.renderTooltip(this.font, Component.literal("LONG DESCRIPTION"), mouseX, mouseY);
            }
        }

        for (WynnventoryButton<GuideItemStack> button : elementButtons) {
            if (button.isHovered()) {
                g.renderTooltip(FontRenderer.getInstance().getFont(), button.getItemStack(), mouseX, mouseY);
                PriceTooltipHelper.renderPriceInfoTooltip(g, mouseX, mouseY, button.getItemStack(), ItemStackUtils.getTooltips(button.getItemStack()), ConfigManager.getInstance().isAnchorTooltips());
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
