package com.wynnventory.ui;

import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.powder.GuidePowderItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.MathUtils;
import com.wynntils.utils.render.FontRenderer;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.enums.PoolType;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import com.wynnventory.util.LootpoolManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class LootpoolScreen extends Screen {
    private static final int PADDING = 50;
    private static final int ITEM_SIZE = 16;
    private static final int ITEMS_PER_ROW = 5;
    private static final int ITEM_PADDING = 8;
    private static final int COL_WIDTH = (ITEM_SIZE * ITEMS_PER_ROW) + (ITEM_PADDING * ITEMS_PER_ROW);
    private static final int GAP_TAB_TO_SEARCH = 10;
    private static final int GAP_SEARCH_TO_TITLES = 30;
    private static final int GAP_TITLES_TO_ITEMS = 10;
    private static final int RELOAD_BUTTON_SIZE = 20;
    private static final int RELOAD_BUTTON_PADDING = 5;

    private final Map<String, List<GuideItemStack>> stacksByName = new HashMap<>();
    private final List<WynnventoryButton> elementButtons = new ArrayList<>();

    private Button lootrunButton;
    private Button raidButton;
    private Button reloadButton;
    private EditBox searchBar;

    private PoolType currentPool = PoolType.LOOTRUN;


    public LootpoolScreen(Component title) {
        super(title);
        loadAllItems();
    }

    @Override
    protected void init() {
        super.init();
        initTabButtons();
        initSearchBar();
        initReloadButton();
        updateScreen();
    }

    private void initTabButtons() {
        int width = 80, height = 20, y = 10, spacing = 10;
        int totalWidth = 2 * width + spacing;
        int startX = (this.width - totalWidth) / 2;

        lootrunButton = Button.builder(Component.literal("Lootruns"), b -> switchTo(PoolType.LOOTRUN))
                .bounds(startX, y, width, height)
                .build();

        raidButton = Button.builder(Component.literal("Raids"), b -> switchTo(PoolType.RAID))
                .bounds(startX + width + spacing, y, width, height)
                .build();

        addRenderableWidget(lootrunButton);
        addRenderableWidget(raidButton);
    }

    private void initSearchBar() {
        searchBar = new EditBox(this.font, 0, 0, 100, 20, Component.literal(""));
        searchBar.setMaxLength(50);
        searchBar.setValue("");
        searchBar.setResponder(text -> updateScreen());
        addRenderableWidget(searchBar);
    }

    private void initReloadButton() {
        reloadButton = Button.builder(Component.literal("↻"), b -> reloadPools())
                .bounds(0, 0, RELOAD_BUTTON_SIZE, RELOAD_BUTTON_SIZE)
                .tooltip(Tooltip.create(Component.literal("Reload Lootpools")))
                .build();
        addRenderableWidget(reloadButton);
    }

    private void switchTo(PoolType type) {
        currentPool = type;
        updateScreen();
    }

    private void updateScreen() {
        this.clearWidgets();
        this.addRenderableWidget(lootrunButton);
        this.addRenderableWidget(raidButton);
        this.addRenderableWidget(searchBar);
        this.addRenderableWidget(reloadButton);
        elementButtons.clear();

        updateTabButtonStyles();

        List<Lootpool> pools = getCurrentPools();
        String query = searchBar.getValue().trim().toLowerCase();

        // Layout calculations
        int totalColumns = pools.size();
        int totalWidth = totalColumns * COL_WIDTH + (totalColumns - 1) * PADDING;
        int startX = (this.width - totalWidth) / 2;
        int searchBarY = lootrunButton.getY() + lootrunButton.getHeight() + GAP_TAB_TO_SEARCH;

        searchBar.setX(startX);
        searchBar.setY(searchBarY);
        searchBar.setWidth(totalWidth);

        reloadButton.setX(searchBar.getX() + searchBar.getWidth() + RELOAD_BUTTON_PADDING);
        reloadButton.setY(searchBar.getY());

        int titlesY = searchBarY + searchBar.getHeight() + GAP_SEARCH_TO_TITLES;
        int startY = titlesY + this.font.lineHeight + GAP_TITLES_TO_ITEMS;

        for (int i = 0; i < pools.size(); i++) {
            int gridX = startX + i * (COL_WIDTH + PADDING);
            buildColumn(pools.get(i), gridX, startY, query);
        }
    }

    private List<Lootpool> getCurrentPools() {
        return currentPool == PoolType.LOOTRUN ? LootpoolManager.getLootrunPools() : LootpoolManager.getRaidPools();
    }

    private void updateTabButtonStyles() {
        lootrunButton.active = currentPool != PoolType.LOOTRUN;
        raidButton.active = currentPool != PoolType.RAID;
    }

    private void reloadPools() {
        LootpoolManager.reloadAllPools();
    }

    private void buildColumn(Lootpool pool, int startX, int startY, String query) {
        int rendered = 0;
        for (LootpoolItem item : pool.getItems()) {
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        List<Lootpool> pools = getCurrentPools();
        int totalWidth = pools.size() * COL_WIDTH + (pools.size() - 1) * PADDING;
        int startX = (this.width - totalWidth) / 2;
        int titlesY = searchBar.getY() + searchBar.getHeight() + GAP_SEARCH_TO_TITLES;

        for (int i = 0; i < pools.size(); i++) {
            int x = startX + i * (COL_WIDTH + PADDING);
            guiGraphics.drawCenteredString(this.font, pools.get(i).getRegion(), x + ((COL_WIDTH - ITEM_PADDING) / 2), titlesY, 0xFFFFFFFF);
        }

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
        if (searchBar.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (WynnventoryMod.KEY_OPEN_POOLS.matches(keyCode, scanCode) && !searchBar.isFocused()) {
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
        addStacks(Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList(),
                stack -> stack.getGearInfo().name());
        addStacks(Models.Rewards.getAllTomeInfos().map(GuideTomeItemStack::new).toList(),
                stack -> stack.getTomeInfo().name());
        addStacks(Models.Element.getAllPowderTierInfo().stream().map(GuidePowderItemStack::new).toList(),
                stack -> stack.getElement().getName() + " Powder " + MathUtils.toRoman(stack.getTier()));
    }

    private <T extends GuideItemStack> void addStacks(List<T> items, java.util.function.Function<T, String> nameMapper) {
        for (T item : items) {
            stacksByName.computeIfAbsent(nameMapper.apply(item), k -> new ArrayList<>()).add(item);
        }
    }
}
