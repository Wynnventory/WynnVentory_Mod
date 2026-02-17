package com.wynnventory.gui.screen;

import com.wynntils.core.components.Models;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.aspect.GuideAspectItemStack;
import com.wynntils.screens.guides.augment.AmplifierItemStack;
import com.wynntils.screens.guides.augment.InsulatorItemStack;
import com.wynntils.screens.guides.augment.SimulatorItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.powder.GuidePowderItemStack;
import com.wynntils.screens.guides.rune.RuneItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.MathUtils;
import com.wynntils.utils.render.FontRenderer;
import com.wynnventory.api.service.RewardService;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.RewardScreenSettings;
import com.wynnventory.gui.Sprite;
import com.wynnventory.gui.widget.*;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleItemType;
import com.wynnventory.model.item.simple.SimpleTierItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class RewardScreen extends Screen {
    private final Screen parent;
    private static RewardType activeType = RewardType.LOOTRUN;
    private static final Map<String, GuideItemStack> wynnItemsByName = new HashMap<>();

    private final List<ItemButton<GuideItemStack>> itemWidgets = new ArrayList<>();

    private int scrollIndex = 0;
    private final int lootrunColumns = ModConfig.getInstance().getRewardScreenSettings().getLootrunColumns();
    private final int raidColumns = ModConfig.getInstance().getRewardScreenSettings().getRaidColumns();

    // Screen layout
    private static final int MARGIN_Y = 40;
    private static final int MARGIN_X = 55;

    // Tab buttons (Lootrun / Raid)
    private static final int TAB_BUTTON_WIDTH = 100;
    private static final int TAB_BUTTON_HEIGHT = 20;
    private static final int TAB_BUTTON_SPACING = 10;

    // Settings & Reload buttons
    private static final int IMAGE_BUTTON_WIDTH = 20;
    private static final int IMAGE_BUTTON_HEIGHT = 20;
    private static final int IMAGE_BUTTON_PADDING_X = 15;

    // Carousel buttons
    private static final int NAV_BUTTON_WIDTH = 20;
    private static final int NAV_BUTTON_HEIGHT = 20;
    private static final int NAV_BUTTON_Y = 40;
    private static final int NAV_BUTTON_MARGIN = 20;

    //Sidebar
    private static final int SIDEBAR_WIDTH = 100;
    private static final int SIDEBAR_Y = NAV_BUTTON_Y;

    public RewardScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new RewardScreen(Component.empty(), mc.screen));
    }

    @Override
    protected void init() {
        if (wynnItemsByName.isEmpty()) {
            loadGuideItems();
        }

        int startX = (this.width - (TAB_BUTTON_WIDTH * 2 + TAB_BUTTON_SPACING)) / 2;
        int startY = 10;

        // Tab button (Lootrun)
        Button lootrunButton = Button.builder(Component.translatable("gui.wynnventory.reward.lootrun"), button -> {
            activeType = RewardType.LOOTRUN;
            this.scrollIndex = 0;
            this.rebuildWidgets();
        }).bounds(startX, startY, TAB_BUTTON_WIDTH, TAB_BUTTON_HEIGHT).build();
        lootrunButton.active = (activeType != RewardType.LOOTRUN);
        this.addRenderableWidget(lootrunButton);

        // Tab button (Raid)
        Button raidButton = Button.builder(Component.translatable("gui.wynnventory.reward.raid"), button -> {
            activeType = RewardType.RAID;
            this.scrollIndex = 0;
            this.rebuildWidgets();
        }).bounds(startX + TAB_BUTTON_WIDTH + TAB_BUTTON_SPACING, startY, TAB_BUTTON_WIDTH, TAB_BUTTON_HEIGHT).build();
        raidButton.active = (activeType != RewardType.RAID);
        this.addRenderableWidget(raidButton);

        // Settings Button
        this.addRenderableWidget(new ImageButton(this.width - IMAGE_BUTTON_WIDTH - 10, startY, IMAGE_BUTTON_WIDTH, IMAGE_BUTTON_HEIGHT, Sprite.SETTINGS_BUTTON, b -> SettingsScreen.open(), Component.translatable("gui.wynnventory.reward.button.config")));

        // Reload Button
        this.addRenderableWidget(new ImageButton(this.width - (IMAGE_BUTTON_WIDTH * 2) - IMAGE_BUTTON_PADDING_X, startY, IMAGE_BUTTON_WIDTH, IMAGE_BUTTON_HEIGHT, Sprite.RELOAD_BUTTON, b -> RewardService.INSTANCE.reloadAllPools().thenRun(() -> this.minecraft.execute(this::rebuildWidgets)), Component.translatable("gui.wynnventory.reward.button.reload")));

        // Carousel buttons
        List<RewardPool> activePools = getActivePools();
        int currentColumns = getCurrentColumns();
        Button prevButton = Button.builder(Component.literal("<"), button -> {
            scrollIndex--;
            if (scrollIndex < 0) {
                scrollIndex = activePools.size() - 1;
            }
            this.rebuildWidgets();
        }).bounds(NAV_BUTTON_MARGIN, NAV_BUTTON_Y, NAV_BUTTON_WIDTH, NAV_BUTTON_HEIGHT).build();
        prevButton.active = activePools.size() > currentColumns;
        this.addRenderableWidget(prevButton);

        Button nextButton = Button.builder(Component.literal(">"), button -> {
            scrollIndex++;
            if (scrollIndex >= activePools.size()) {
                scrollIndex = 0;
            }
            this.rebuildWidgets();
        }).bounds(this.width - 130 - NAV_BUTTON_MARGIN, NAV_BUTTON_Y, NAV_BUTTON_WIDTH, NAV_BUTTON_HEIGHT).build();
        nextButton.active = activePools.size() > currentColumns;
        this.addRenderableWidget(nextButton);

        // === SIDEBAR ===
        int sidebarX = this.width - SIDEBAR_WIDTH - 10;
        this.addRenderableWidget(new RectWidget(sidebarX, MARGIN_Y, SIDEBAR_WIDTH, this.height - 10 - MARGIN_Y, 0x22FFFFFF));

        // Filters
        RewardScreenSettings s = ModConfig.getInstance().getRewardScreenSettings();
        int filterY = SIDEBAR_Y + 12;
        this.addRenderableWidget(new TextWidget(sidebarX + 5, SIDEBAR_Y, Component.literal("Filters")));
        addFilterButton("Mythic", Sprite.MYTHIC_ICON, s::isShowMythic, s::setShowMythic, sidebarX + 5, filterY, 16);
        addFilterButton("Fabled", Sprite.FABLED_ICON, s::isShowFabled, s::setShowFabled, sidebarX + 23, filterY, 16);
        addFilterButton("Legendary", Sprite.LEGENDARY_ICON, s::isShowLegendary, s::setShowLegendary, sidebarX + 41, filterY, 16);
        addFilterButton("Rare", Sprite.RARE_ICON, s::isShowRare, s::setShowRare, sidebarX + 59, filterY, 16);
        addFilterButton("Unique", Sprite.UNIQUE_ICON, s::isShowUnique, s::setShowUnique, sidebarX + 77, filterY, 16);
        addFilterButton("Common", Sprite.COMMON_ICON, s::isShowCommon, s::setShowCommon, sidebarX + 5, filterY + 18, 16);
        addFilterButton("Set", Sprite.SET_ICON, s::isShowSet, s::setShowSet, sidebarX + 23, filterY + 18, 16);

        populateItemWidgets();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        for (ItemButton<GuideItemStack> widget : itemWidgets) {
            if (widget.isHovered()) {
                graphics.setTooltipForNextFrame(this.font, widget.getItemStack(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void loadGuideItems() {
        addStacks(Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList(), s -> s.getGearInfo().name());
        addStacks(Models.Rewards.getAllTomeInfos().map(GuideTomeItemStack::new).toList(), s -> s.getTomeInfo().name());
        addStacks(Models.Aspect.getAllAspectInfos().map(info -> new GuideAspectItemStack(info, 1)).toList(), s -> s.getAspectInfo().name());
        addStacks(Models.Element.getAllPowderTierInfo().stream().map(GuidePowderItemStack::new).toList(), s -> s.getElement().getName() + " Powder " + s.getTier());
        addStacks((Models.Rewards.getAllAmplifierInfo().stream().map(AmplifierItemStack::new).toList()), s -> s.getHoverName().getString());
        addStacks((Models.Rewards.getAllRuneInfo().stream().map(RuneItemStack::new).toList()), s -> s.getHoverName().getString());

        InsulatorItemStack insulatorItemStack = new InsulatorItemStack();
        wynnItemsByName.put(insulatorItemStack.getHoverName().getString(), insulatorItemStack);

        SimulatorItemStack simulatorItemStack = new SimulatorItemStack();
        wynnItemsByName.put(simulatorItemStack.getHoverName().getString(), simulatorItemStack);
    }

    private <T extends GuideItemStack> void addStacks(List<T> items, Function<T, String> nameMapper) {
        for (T item : items) {
            wynnItemsByName.computeIfAbsent(nameMapper.apply(item), k -> item);
        }
    }

    private void populateItemWidgets() {
        int contentWidth = getContentWidth();
        List<RewardPool> allActivePools = getActivePools();
        int currentColumns = getCurrentColumns();

        if (allActivePools.isEmpty()) return;

        int displayCount = Math.min(currentColumns, allActivePools.size());
        int sectionWidth = contentWidth / currentColumns;

        for (int i = 0; i < displayCount; i++) {
            int poolIndex = (scrollIndex + i) % allActivePools.size();
            RewardPool pool = allActivePools.get(poolIndex);
            int currentX = MARGIN_X + (i * sectionWidth);

            // Pool Background (Bookshelf Container)
            int poolHeight = this.height - 10 - MARGIN_Y;
            this.addRenderableWidget(new RectWidget(currentX, MARGIN_Y, sectionWidth - 2, poolHeight, 0xFF7D5D44)); // Lighter non-transparent wood color

            // Pool Borders
            int borderColor = 0xFF3E2820; // Dark wood border
            this.addRenderableWidget(new RectWidget(currentX, MARGIN_Y, sectionWidth - 2, 1, borderColor)); // Top
            this.addRenderableWidget(new RectWidget(currentX, MARGIN_Y + poolHeight - 1, sectionWidth - 2, 1, borderColor)); // Bottom
            this.addRenderableWidget(new RectWidget(currentX, MARGIN_Y, 1, poolHeight, borderColor)); // Left
            this.addRenderableWidget(new RectWidget(currentX + sectionWidth - 3, MARGIN_Y, 1, poolHeight, borderColor)); // Right

            // Header Background
            this.addRenderableWidget(new ImageWidget(currentX + (sectionWidth - 70) / 2, MARGIN_Y - 5, 70, 20, Sprite.BANNER_NAME));

            // Pool Name (TextWidget)
            int nameWidth = this.font.width(pool.getShortName());
            int labelX = currentX + (sectionWidth - nameWidth) / 2;
            this.addRenderableWidget(new TextWidget(labelX, MARGIN_Y + 1, Component.literal(pool.getShortName()), 0xFFEEDDBB));

            // Pool Separator (Vertical Line) - Now part of the border logic above

            createItemButtons(currentX, MARGIN_Y + 15, pool, sectionWidth);
        }
    }

    private void createItemButtons(int startX, int startY, RewardPool pool, int totalWidth) {
        RewardService.INSTANCE.getItems(pool).thenAccept(items -> Minecraft.getInstance().execute(() -> {
            if (getActivePools().stream().noneMatch(p -> p == pool)) return;

            List<SimpleItem> filteredItems = items.stream()
                    .filter(this::matchesFilters)
                    .filter(item -> {
                        GuideItemStack stack = getGuideItemStack(item);
                        return stack != null && !stack.isEmpty();
                    })
                    .toList();

            if (activeType == RewardType.RAID) {
                renderRaidSections(startX, startY, filteredItems, totalWidth);
            } else {
                renderLootrunSections(startX, startY, filteredItems, totalWidth);
            }
        }));
    }

    private void renderRaidSections(int startX, int startY, List<SimpleItem> items, int totalWidth) {
        List<SimpleItem> aspects = new ArrayList<>();
        List<SimpleItem> tomes = new ArrayList<>();
        List<SimpleItem> gear = new ArrayList<>();
        List<SimpleItem> misc = new ArrayList<>();

        for (SimpleItem item : items) {
            SimpleItemType type = item.getItemTypeEnum();
            if (type == SimpleItemType.ASPECT) aspects.add(item);
            else if (type == SimpleItemType.TOME) tomes.add(item);
            else if (type == SimpleItemType.GEAR) gear.add(item);
            else misc.add(item);
        }

        int currentY = startY;
        currentY = renderSection(startX, currentY, "Aspects", aspects, totalWidth);
        currentY = renderSection(startX, currentY, "Tomes", tomes, totalWidth);
        currentY = renderSection(startX, currentY, "Gear", gear, totalWidth);
        renderSection(startX, currentY, "Misc", misc, totalWidth);
    }

    private void renderLootrunSections(int startX, int startY, List<SimpleItem> items, int totalWidth) {
        Map<GearTier, List<SimpleItem>> groupedByRarity = new HashMap<>();
        for (SimpleItem item : items) {
            groupedByRarity.computeIfAbsent(item.getRarityEnum(), k -> new ArrayList<>()).add(item);
        }

        GearTier[] tiers = {GearTier.MYTHIC, GearTier.FABLED, GearTier.LEGENDARY, GearTier.RARE, GearTier.UNIQUE, GearTier.SET, GearTier.NORMAL};
        List<GearTier> activeTiers = Stream.of(tiers).filter(groupedByRarity::containsKey).toList();

        if (activeTiers.isEmpty()) return;

        int currentY = startY;
        for (GearTier tier : activeTiers) {
            currentY = renderSection(startX, currentY, tier.getName(), groupedByRarity.get(tier), totalWidth);
        }
    }

    private int renderSection(int startX, int startY, String title, List<SimpleItem> items, int sectionWidth) {
        if (items.isEmpty()) return startY;

        // Render header
        this.addRenderableWidget(new TextWidget(startX + 2, startY, Component.literal(title)));

        int itemsStartY = startY + 10;

        int itemsPerRow = (sectionWidth - 4) / 20;
        if (itemsPerRow <= 0) itemsPerRow = 1;

        int rows = (int) Math.ceil(items.size() / (double) itemsPerRow);

        for (int i = 0; i < items.size(); i++) {
            SimpleItem item = items.get(i);
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;

            int x = startX + 2 + col * 20;
            int y = itemsStartY + row * 20;

            GuideItemStack stack = getGuideItemStack(item);
            ItemButton<GuideItemStack> button = new ItemButton<>(x, y, 18, 18, stack, item);
            this.addRenderableWidget(button);
            itemWidgets.add(button);
        }

        int nextY = itemsStartY + rows * 20 + 8;
        // Shelf Board Divider
        this.addRenderableWidget(new RectWidget(startX + 1, nextY - 6, sectionWidth - 4, 3, 0xFF3E2820)); // Board
        this.addRenderableWidget(new RectWidget(startX + 1, nextY - 3, sectionWidth - 4, 1, 0xFF2A1B16)); // Board shadow

        return nextY;
    }

    private void addFilterButton(String label, Sprite icon, BooleanSupplier getter, Consumer<Boolean> setter, int x, int y, int w) {
        this.addRenderableWidget(new FilterButton(x, y, w, 16, label, icon, getter, setter, () -> {
            try {
                ModConfig.getInstance().save();
            } catch (IOException ignored) {
            }
            this.rebuildWidgets();
        }));
    }

    private boolean matchesFilters(SimpleItem item) {
        RewardScreenSettings s = ModConfig.getInstance().getRewardScreenSettings();

        return switch (item.getRarityEnum()) {
            case GearTier.MYTHIC -> s.isShowMythic();
            case GearTier.FABLED -> s.isShowFabled();
            case GearTier.LEGENDARY -> s.isShowLegendary();
            case GearTier.RARE -> s.isShowRare();
            case GearTier.UNIQUE -> s.isShowUnique();
            case GearTier.SET -> s.isShowSet();
            default -> s.isShowCommon();
        };
    }

    private List<RewardPool> getActivePools() {
        return Stream.of(RewardPool.values())
                .filter(pool -> pool.getType() == activeType)
                .toList();
    }

    private int getContentWidth() {
        return this.width - SIDEBAR_WIDTH - 2 * NAV_BUTTON_WIDTH - IMAGE_BUTTON_PADDING_X - MARGIN_X;
    }

    private int getCurrentColumns() {
        return activeType == RewardType.LOOTRUN ? lootrunColumns : raidColumns;
    }

    private GuideItemStack getGuideItemStack(SimpleItem item) {
        if (item instanceof SimpleTierItem s) {
            if (s.getItemTypeEnum() == SimpleItemType.POWDER) {
                return wynnItemsByName.get(s.getName() + " " + s.getTier());
            } else if (s.getItemTypeEnum() == SimpleItemType.AMPLIFIER) {
                return wynnItemsByName.get(s.getName() + " " + MathUtils.toRoman(s.getTier()));
            }
        }
        return wynnItemsByName.get(item.getName());
    }
}