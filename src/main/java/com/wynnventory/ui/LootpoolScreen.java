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
import com.wynnventory.util.LootpoolManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

@Environment(EnvType.CLIENT)
public class LootpoolScreen extends Screen {
    private static final int ITEM_SIZE = 16;
    private static final int ITEMS_PER_ROW = 5;
    private static final int ITEM_PADDING = 8;
    private static final int COL_WIDTH = (ITEM_SIZE * ITEMS_PER_ROW) + (ITEM_PADDING * ITEMS_PER_ROW);
    private static final int PADDING = 20;
    private static final int GAP_TITLE_TO_ITEMS = 12;
    private static final int FILTER_COUNT = 5;
    private static int LAST_TITLES_Y = 0;
    private static int LAST_START_Y = 0;


    private final Map<String, List<GuideItemStack>> stacksByName = new HashMap<>();
    private final List<WynnventoryButton> elementButtons = new ArrayList<>();

    private Button lootrunButton;
    private Button raidButton;
    private Button reloadButton;
    private EditBox searchBar;
    private final List<Button> filterToggles = new ArrayList<>();

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
        int buttonWidth = 80, buttonHeight = 20, y = 10, spacing = 10;
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

    private void initFilters() {
        filterToggles.clear();
        int toggleWidth = 80;
        int toggleHeight = 20;
        int spacing = 5;

        // Align filters to right edge of reload button
        int startX = reloadButton.getX() + reloadButton.getWidth() - toggleWidth;
        int startY = reloadButton.getY() + reloadButton.getHeight() + 6; // slight vertical gap below reload

        for (int i = 0; i < FILTER_COUNT; i++) {
            final int index = i;
            boolean initial = ConfigManager.getInstance().getFilterState(index);

            int toggleX = startX;
            int toggleY = startY + i * (toggleHeight + spacing); // stacked vertically

            Button toggle = Button.builder(
                    Component.literal(getFilterLabel(index, initial)),
                    b -> {
                        boolean newState = !ConfigManager.getInstance().getFilterState(index);
                        ConfigManager.getInstance().setFilterState(index, newState);
                        updateScreen();
                    }
            ).bounds(toggleX, toggleY, toggleWidth, toggleHeight).build();

            filterToggles.add(toggle);
            addRenderableWidget(toggle);
        }
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
        int reloadWidth = 20;
        int reloadHeight = 20;
        int gap = 5;

        int x = searchBar.getX() + searchBar.getWidth() + gap;
        int y = searchBar.getY();

        reloadButton = Button.builder(Component.literal("↻"), b -> {
            LootpoolManager.reloadAllPools();
            updateScreen();
        }).bounds(x, y, reloadWidth, reloadHeight).build();

        addRenderableWidget(reloadButton);
    }

    private void switchTo(PoolType type) {
        currentPool = type;
        updateScreen();
    }

    private void updateScreen() {
        this.clearWidgets();

        // Re-add fixed widgets
        addRenderableWidget(lootrunButton);
        addRenderableWidget(raidButton);
        addRenderableWidget(searchBar);
        addRenderableWidget(reloadButton);
        filterToggles.forEach(this::addRenderableWidget);
        elementButtons.clear();

        updateTabButtonStyles();

        List<GroupedLootpool> pools = getCurrentPools();
        String query = searchBar.getValue().trim().toLowerCase();

        // Center layout calculations
        int totalColumns = pools.size();
        int totalWidth = totalColumns * COL_WIDTH + (totalColumns - 1) * PADDING;
        int startX = (this.width - totalWidth) / 2;

        int filterPanelHeight = 20 * FILTER_COUNT + (FILTER_COUNT - 1) * 5;
        int topY = 40 + filterPanelHeight + 20;

        LAST_TITLES_Y = searchBar.getY() + searchBar.getHeight() + 10;
        LAST_START_Y = LAST_TITLES_Y + this.font.lineHeight + GAP_TITLE_TO_ITEMS;

        for (int i = 0; i < pools.size(); i++) {
            int gridX = startX + i * (COL_WIDTH + PADDING);
            buildColumn(pools.get(i), gridX, LAST_START_Y, query);
        }
    }

    private List<GroupedLootpool> getCurrentPools() {
        return currentPool == PoolType.LOOTRUN ? LootpoolManager.getLootrunPools() : LootpoolManager.getRaidPools();
    }

    private void updateTabButtonStyles() {
        lootrunButton.active = currentPool != PoolType.LOOTRUN;
        raidButton.active = currentPool != PoolType.RAID;
    }

    private void buildColumn(GroupedLootpool pool, int startX, int startY, String query) {
        int rendered = 0;

        for(LootpoolGroup group : pool.getGroupItems()) {
            for (LootpoolItem item : group.getLootItems()) {
                if (!item.getName().toLowerCase().contains(query)) continue;

                int x = startX + (rendered % ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING);
                int y = startY + (rendered / ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING);

                List<GuideItemStack> stacks = stacksByName.get(item.getName());
                if (stacks == null || stacks.isEmpty()) continue;

                for (GuideItemStack stack : stacks) {
                    WynnventoryButton button = new WynnventoryButton(x, y, ITEM_SIZE, ITEM_SIZE, stack, this);
                    elementButtons.add(button);
                    addRenderableWidget(button);
                    rendered++;
                }
            }
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        List<GroupedLootpool> pools = getCurrentPools();
        int totalWidth = pools.size() * COL_WIDTH + (pools.size() - 1) * PADDING;
        int startX = (this.width - totalWidth) / 2;

        for (int i = 0; i < pools.size(); i++) {
            int columnX = startX + i * (COL_WIDTH + PADDING);
            String regionName = pools.get(i).getRegion();
            int textWidth = this.font.width(regionName);
            int textX = columnX + ((COL_WIDTH - ITEM_PADDING) / 2);

            guiGraphics.drawCenteredString(this.font, regionName, textX, LAST_TITLES_Y, 0xFFFFFFFF);

            if (mouseX >= textX - textWidth / 2 && mouseX <= textX + textWidth / 2 &&
                    mouseY >= LAST_TITLES_Y && mouseY <= LAST_TITLES_Y + this.font.lineHeight) {
                guiGraphics.renderTooltip(this.font, Component.literal("LONG DESCRIPTION"), mouseX, mouseY);
            }
        }

        for (WynnventoryButton button : elementButtons) {
            if (button.isHovered()) {
                guiGraphics.renderTooltip(FontRenderer.getInstance().getFont(), button.getItemStack(), mouseX, mouseY);
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

    private String getFilterLabel(int index, boolean enabled) {
        return "Filter" + (index + 1) + ": " + (enabled ? "ON" : "OFF");
    }

    private void loadAllItems() {
        addStacks(Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList(), s -> s.getGearInfo().name());
        addStacks(Models.Rewards.getAllTomeInfos().map(GuideTomeItemStack::new).toList(), s -> s.getTomeInfo().name());
        addStacks(Models.Element.getAllPowderTierInfo().stream().map(GuidePowderItemStack::new).toList(),
                s -> s.getElement().getName() + " Powder " + MathUtils.toRoman(s.getTier()));
    }

    private <T extends GuideItemStack> void addStacks(List<T> items, java.util.function.Function<T, String> nameMapper) {
        for (T item : items) {
            stacksByName.computeIfAbsent(nameMapper.apply(item), k -> new ArrayList<>()).add(item);
        }
    }
}