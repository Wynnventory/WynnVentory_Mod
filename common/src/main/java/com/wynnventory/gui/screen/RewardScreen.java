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
import com.wynnventory.api.service.RewardService;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.RewardScreenSettings;
import com.wynnventory.gui.Sprite;
import com.wynnventory.gui.widget.FilterButton;
import com.wynnventory.gui.widget.ImageButton;
import com.wynnventory.gui.widget.ImageWidget;
import com.wynnventory.gui.widget.ItemButton;
import com.wynnventory.gui.widget.TextWidget;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleItemType;
import com.wynnventory.model.item.simple.SimpleTierItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RewardScreen extends Screen {
    private final Screen parent;
    private static RewardType activeType = RewardType.LOOTRUN;
    private static final Map<String, GuideItemStack> wynnItemsByName = new HashMap<>();

    private final List<ItemButton<GuideItemStack>> itemWidgets = new ArrayList<>();

    private int scrollIndex = 0;

    // Global scaling derived from tallest pool to fit vertically
    private double globalPoolScale = 1.0;
    private boolean scaleReady = false;
    private int lastWidth = -1;
    private int lastHeight = -1;

    // Recalc control to avoid repeated heavy work during drag-resize
    private boolean recalculating = false;
    private boolean pendingRecalc = false;
    private boolean suppressInitRecalc = false;
    private long lastResizeTime = 0;
    private static final long RESIZE_DEBOUNCE_MS = 250;

    // Screen layout
    private static final int MARGIN_Y = 40;
    private static final int MARGIN_X = 55;
    private static final int BOTTOM_PADDING = 20;

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

    // Sidebar
    private static final int SIDEBAR_WIDTH = 115;
    private static final int SIDEBAR_Y = NAV_BUTTON_Y;

    // Layout constants for pools
    private static final int ITEMS_PER_ROW = 9;
    private static final int BASE_ITEM_SIZE = 16;
    private static final int BASE_PITCH = 18;
    private static final int INTERIOR_BODY_WIDTH = 176;
    private static final double TOP_AWNING_OVERLAP = 0.5; // Sections start halfway into pool top

    public RewardScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new RewardScreen(Component.empty(), mc.screen));
    }

    private void triggerRecalc() {
        if (this.suppressInitRecalc) return;
        this.scaleReady = false;
        if (!this.recalculating) {
            recalcScaleAsync();
        } else {
            this.pendingRecalc = true;
        }
    }

    @Override
    protected void init() {
        // During live window resizing, skip heavy widget rebuilds entirely.
        // We'll rebuild once after the resize settles via triggerRecalc() in resize().
        if (this.suppressInitRecalc) {
            return;
        }

        if (wynnItemsByName.isEmpty()) {
            loadGuideItems();
        }

        int startX = (this.width - (TAB_BUTTON_WIDTH * 2 + TAB_BUTTON_SPACING)) / 2;
        int startY = 10;

        // Tab button (Lootrun)
        Button lootrunButton = Button.builder(Component.translatable("gui.wynnventory.reward.lootrun"), button -> {
                    activeType = RewardType.LOOTRUN;
                    this.scrollIndex = 0;
                    this.triggerRecalc();
                })
                .bounds(startX, startY, TAB_BUTTON_WIDTH, TAB_BUTTON_HEIGHT)
                .build();
        lootrunButton.active = (activeType != RewardType.LOOTRUN);
        this.addRenderableWidget(lootrunButton);

        // Tab button (Raid)
        Button raidButton = Button.builder(Component.translatable("gui.wynnventory.reward.raid"), button -> {
                    activeType = RewardType.RAID;
                    this.scrollIndex = 0;
                    this.triggerRecalc();
                })
                .bounds(startX + TAB_BUTTON_WIDTH + TAB_BUTTON_SPACING, startY, TAB_BUTTON_WIDTH, TAB_BUTTON_HEIGHT)
                .build();
        raidButton.active = (activeType != RewardType.RAID);
        this.addRenderableWidget(raidButton);

        // Settings Button
        this.addRenderableWidget(new ImageButton(
                this.width - IMAGE_BUTTON_WIDTH - 10,
                startY,
                IMAGE_BUTTON_WIDTH,
                IMAGE_BUTTON_HEIGHT,
                Sprite.SETTINGS_BUTTON,
                b -> SettingsScreen.open(),
                Component.translatable("gui.wynnventory.reward.button.config")));

        // Reload Button
        this.addRenderableWidget(new ImageButton(
                this.width - (IMAGE_BUTTON_WIDTH * 2) - IMAGE_BUTTON_PADDING_X,
                startY,
                IMAGE_BUTTON_WIDTH,
                IMAGE_BUTTON_HEIGHT,
                Sprite.RELOAD_BUTTON,
                b -> RewardService.INSTANCE
                        .reloadAllPools()
                        .thenRun(() -> this.minecraft.execute(this::rebuildWidgets)),
                Component.translatable("gui.wynnventory.reward.button.reload")));

        // Carousel buttons
        List<RewardPool> activePools = getActivePools();
        int contentWidth = getContentWidth();
        int poolWidth = Sprite.LOOTRUN_POOL_TOP_SECTION.width();

        int middleY = (this.height - NAV_BUTTON_HEIGHT) / 2;
        ImageButton prevButton = new ImageButton(
                NAV_BUTTON_MARGIN,
                middleY,
                NAV_BUTTON_WIDTH * 2,
                NAV_BUTTON_HEIGHT * 2,
                Sprite.ARROW_LEFT,
                button -> {
                    scrollIndex--;
                    if (scrollIndex < 0) {
                        scrollIndex = activePools.size() - 1;
                    }
                    this.rebuildWidgets();
                },
                null);
        this.addRenderableWidget(prevButton);

        ImageButton nextButton = new ImageButton(
                this.width - 130 - NAV_BUTTON_MARGIN,
                middleY,
                NAV_BUTTON_WIDTH * 2,
                NAV_BUTTON_HEIGHT * 2,
                Sprite.ARROW_RIGHT,
                button -> {
                    scrollIndex++;
                    if (scrollIndex >= activePools.size()) {
                        scrollIndex = 0;
                    }
                    this.rebuildWidgets();
                },
                null);
        this.addRenderableWidget(nextButton);

        // === SIDEBAR ===
        int sidebarX = this.width - SIDEBAR_WIDTH - 10;

        // Filters
        RewardScreenSettings s = ModConfig.getInstance().getRewardScreenSettings();
        int filterY = SIDEBAR_Y;

        // Filter background texture
        this.addRenderableWidget(new ImageWidget(
                sidebarX + 5,
                filterY,
                Sprite.FILTER_SECTION.width(),
                Sprite.FILTER_SECTION.height(),
                Sprite.FILTER_SECTION));

        Component filterTitle = Component.literal("Filters");
        int textW = this.font.width(filterTitle);
        int textX = (sidebarX + 7) + (Sprite.FILTER_SECTION.width() - textW) / 2;
        int textY = filterY + 3; // Positioned within the plaque area of the new texture
        this.addRenderableWidget(new TextWidget(textX, textY, filterTitle));

        // Grid of 5 buttons per row
        record FilterItem(String label, Sprite icon, BooleanSupplier getter, Consumer<Boolean> setter) {}
        List<FilterItem> filterItems = List.of(
                new FilterItem("Mythic", Sprite.MYTHIC_ICON, s::isShowMythic, s::setShowMythic),
                new FilterItem("Fabled", Sprite.FABLED_ICON, s::isShowFabled, s::setShowFabled),
                new FilterItem("Legendary", Sprite.LEGENDARY_ICON, s::isShowLegendary, s::setShowLegendary),
                new FilterItem("Rare", Sprite.RARE_ICON, s::isShowRare, s::setShowRare),
                new FilterItem("Unique", Sprite.UNIQUE_ICON, s::isShowUnique, s::setShowUnique),
                new FilterItem("Common", Sprite.COMMON_ICON, s::isShowCommon, s::setShowCommon),
                new FilterItem("Set", Sprite.SET_ICON, s::isShowSet, s::setShowSet));

        int filterStartX = sidebarX + 9;
        int filterStartY = filterY + 18;
        int filterSpacingX = 20;
        int filterSpacingY = 20;
        int buttonsPerRow = 5;

        for (int i = 0; i < filterItems.size(); i++) {
            FilterItem item = filterItems.get(i);
            int row = i / buttonsPerRow;
            int col = i % buttonsPerRow;
            addFilterButton(
                    item.label,
                    item.icon,
                    item.getter,
                    item.setter,
                    filterStartX + col * filterSpacingX,
                    filterStartY + row * filterSpacingY,
                    16);
        }

        // Trigger scale calculation on first open; during window resize it's managed in resize()
        if (!this.scaleReady) {
            this.triggerRecalc();
        } else {
            populateItemWidgets();
        }
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
    public void resize(int width, int height) {
        // Use native resize to manage recalculation once per resize cycle.
        // Suppress init-triggered recalc during this call to avoid duplicates.
        this.suppressInitRecalc = true;
        super.resize(width, height);
        this.suppressInitRecalc = false;

        this.lastResizeTime = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lastResizeTime > 0 && System.currentTimeMillis() - this.lastResizeTime > RESIZE_DEBOUNCE_MS) {
            this.lastResizeTime = 0;
            this.triggerRecalc();
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void loadGuideItems() {
        addStacks(Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList(), s -> s.getGearInfo()
                .name());
        addStacks(Models.Rewards.getAllTomeInfos().map(GuideTomeItemStack::new).toList(), s -> s.getTomeInfo()
                .name());
        addStacks(
                Models.Aspect.getAllAspectInfos()
                        .map(info -> new GuideAspectItemStack(info, 1))
                        .toList(),
                s -> s.getAspectInfo().name());
        addStacks(
                Models.Element.getAllPowderTierInfo().stream()
                        .map(GuidePowderItemStack::new)
                        .toList(),
                s -> s.getElement().getName() + " Powder " + s.getTier());
        addStacks(
                (Models.Rewards.getAllAmplifierInfo().stream()
                        .map(AmplifierItemStack::new)
                        .toList()),
                s -> s.getHoverName().getString());
        addStacks(
                (Models.Rewards.getAllRuneInfo().stream()
                        .map(RuneItemStack::new)
                        .toList()),
                s -> s.getHoverName().getString());

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
        if (!scaleReady) return; // wait until we know the global scale from tallest pool

        int contentWidth = getContentWidth();
        List<RewardPool> allActivePools = getActivePools();
        if (allActivePools.isEmpty()) return;

        int currentColumns = getCurrentColumns();
        int displayCount = Math.min(currentColumns, allActivePools.size());
        int sectionWidth = contentWidth / currentColumns;
        int totalPoolsWidth = displayCount * sectionWidth;
        int centeringOffset = (contentWidth - totalPoolsWidth) / 2;

        for (int i = 0; i < displayCount; i++) {
            int poolIndex = (scrollIndex + i) % allActivePools.size();
            RewardPool pool = allActivePools.get(poolIndex);
            int currentX = MARGIN_X + centeringOffset + (i * sectionWidth);

            double poolScale = this.globalPoolScale;
            int headerH = (int) (Sprite.LOOTRUN_POOL_TOP_SECTION.height() * poolScale);
            int itemsStartY = MARGIN_Y + (int) (headerH * 0.69);

            createItemButtons(currentX, itemsStartY, pool, sectionWidth, poolScale);
        }
    }

    private void renderPoolHeader(int startX, int sectionWidth, double poolScale, String title) {
        int headerW = (int) (Sprite.LOOTRUN_POOL_TOP_SECTION.width() * poolScale);
        int headerH = (int) (Sprite.LOOTRUN_POOL_TOP_SECTION.height() * poolScale);
        int headerX = startX + (sectionWidth - headerW) / 2;
        int headerY = MARGIN_Y;

        if (activeType == RewardType.LOOTRUN) {
            this.addRenderableWidget(
                    new ImageWidget(headerX, headerY, headerW, headerH, Sprite.LOOTRUN_POOL_TOP_SECTION));
        } else {
            this.addRenderableWidget(new ImageWidget(headerX, headerY, headerW, headerH, Sprite.RAID_POOL_TOP_SECTION));
        }

        int titleWidth = (int) (this.font.width(title) * poolScale);
        int plaqueY = headerY + (int) (7 * poolScale);
        int titleX = headerX + (headerW - titleWidth) / 2;
        this.addRenderableWidget(
                new TextWidget(titleX, plaqueY, Component.literal(title), 0xFFFFFFFF, (float) poolScale));
    }

    private void createItemButtons(int startX, int startY, RewardPool pool, int totalWidth, double poolScale) {
        RewardService.INSTANCE.getItems(pool).thenAccept(items -> Minecraft.getInstance()
                .execute(() -> {
                    if (getActivePools().stream().noneMatch(p -> p == pool)) return;

                    List<SimpleItem> filteredItems = items.stream()
                            .filter(this::matchesFilters)
                            .filter(item -> {
                                GuideItemStack stack = getGuideItemStack(item);
                                return stack != null && !stack.isEmpty();
                            })
                            .toList();

                    List<SectionData> sections = buildSections(filteredItems);
                    renderSectionsCommon(startX, startY, sections, totalWidth, poolScale, pool);
                }));
    }

    private int renderSection(
            int startX, int startY, String title, List<SimpleItem> items, int sectionWidth, double poolScale) {
        // Always render the section header, even if there are no items

        // Render header
        int headerW = (int) (Sprite.POOL_MIDDLE_SECTION_HEADER.width() * poolScale);
        int headerH = (int) (Sprite.POOL_MIDDLE_SECTION_HEADER.height() * poolScale);
        int headerX = startX + (sectionWidth - headerW) / 2;
        int headerY = startY;
        this.addRenderableWidget(
                new ImageWidget(headerX, headerY, headerW, headerH, Sprite.POOL_MIDDLE_SECTION_HEADER));

        // Section Title centered in upper area
        int titleX = headerX + Math.max(1, (int) (8 * poolScale));
        int titleY = headerY + Math.max(1, (int) (4 * poolScale)); // Offset to be in the "upper area"
        this.addRenderableWidget(
                new TextWidget(titleX, titleY, Component.literal(title), 0xFFFFFFFF, (float) poolScale));

        int interiorWidth = (int) (INTERIOR_BODY_WIDTH * poolScale); // The "body" width where items sit
        int gridWidth = (int) ((ITEMS_PER_ROW * BASE_PITCH - (BASE_PITCH - BASE_ITEM_SIZE)) * poolScale);
        // We center the grid within the interior body width, not the full sectionWidth
        int bodyX = startX + (sectionWidth - interiorWidth) / 2;
        int leftPad = bodyX + Math.max(0, (interiorWidth - gridWidth) / 2);

        int rows = (int) Math.ceil(items.size() / (double) ITEMS_PER_ROW);

        // Middle Section Backgrounds (for additional rows only)
        int middleW = headerW; // Match header width
        int middleH = (int) (Sprite.POOL_MIDDLE_SECTION.height() * poolScale);
        int middleX = headerX;

        // Render additional row backgrounds (if rows > 1)
        for (int r = 1; r < rows; r++) {
            int rowY = headerY + headerH + (r - 1) * middleH;
            this.addRenderableWidget(new ImageWidget(middleX, rowY, middleW, middleH, Sprite.POOL_MIDDLE_SECTION));
        }

        int itemSize = (int) (BASE_ITEM_SIZE * poolScale);
        int pitch = (int) (BASE_PITCH * poolScale);

        for (int i = 0; i < items.size(); i++) {
            SimpleItem item = items.get(i);
            int row = i / ITEMS_PER_ROW;
            int col = i % ITEMS_PER_ROW;

            int x = leftPad + col * pitch;
            int y;
            if (row == 0) {
                // First row is inside the header (in the lower area)
                // Middle section header height is 41 natural; item is 16;
                // We want to center the 16px item in the lower part below the title.
                y = headerY + headerH - (int) (18 * poolScale);
            } else {
                // Additional rows are inside their respective middle section backgrounds (22 natural)
                y = headerY + headerH + (row - 1) * middleH + (middleH - itemSize) / 2 + 2;
            }

            GuideItemStack stack = getGuideItemStack(item);
            ItemButton<GuideItemStack> button = new ItemButton<>(x, y, itemSize, itemSize, stack, item);
            this.addRenderableWidget(button);
            itemWidgets.add(button);
        }

        return headerY + headerH + (rows > 1 ? (rows - 1) * middleH : 0);
    }

    private void renderBottomSection(int startX, int startY, int sectionWidth, double poolScale) {
        int bottomW = (int) (Sprite.POOL_BOTTOM_SECTION.width() * poolScale);
        int bottomH = (int) (Sprite.POOL_BOTTOM_SECTION.height() * poolScale);
        int bottomX = startX + (sectionWidth - bottomW) / 2;
        int bottomY = startY;

        this.addRenderableWidget(new ImageWidget(bottomX, bottomY, bottomW, bottomH, Sprite.POOL_BOTTOM_SECTION));
    }

    private void addFilterButton(
            String label, Sprite icon, BooleanSupplier getter, Consumer<Boolean> setter, int x, int y, int w) {
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
        int contentWidth = getContentWidth();
        double scale = this.scaleReady ? this.globalPoolScale : 1.0;
        int poolScaledWidth = (int) Math.max(1, Math.round(Sprite.LOOTRUN_POOL_TOP_SECTION.width() * scale));
        return Math.max(1, contentWidth / poolScaledWidth);
    }

    private GuideItemStack getGuideItemStack(SimpleItem item) {
        if (item instanceof SimpleTierItem s) {
            String suffix =
                    switch (s.getItemTypeEnum()) {
                        case POWDER -> " " + s.getTier();
                        case AMPLIFIER -> " " + MathUtils.toRoman(s.getTier());
                        default -> "";
                    };
            if (!suffix.isEmpty()) {
                return wynnItemsByName.get(s.getName() + suffix);
            }
        }
        return wynnItemsByName.get(item.getName());
    }

    // === Vertical-first scaling ===
    private void recalcScaleAsync() {
        this.recalculating = true;
        List<RewardPool> pools = getActivePools();
        if (pools.isEmpty()) {
            this.globalPoolScale = 1.0;
            this.scaleReady = true;
            this.recalculating = false;
            this.populateItemWidgets();
            return;
        }

        AtomicInteger remaining = new AtomicInteger(pools.size());
        Map<RewardPool, List<SimpleItem>> itemsByPool = new HashMap<>();

        for (RewardPool pool : pools) {
            RewardService.INSTANCE.getItems(pool).whenComplete((items, ex) -> Minecraft.getInstance()
                    .execute(() -> {
                        if (ex != null) {
                            itemsByPool.put(pool, List.of());
                        } else {
                            // We use UNFILTERED items for scaling to ensure consistent pool size
                            List<SimpleItem> forScaling = items.stream()
                                    .filter(it -> {
                                        GuideItemStack stack = getGuideItemStack(it);
                                        return stack != null && !stack.isEmpty();
                                    })
                                    .toList();
                            itemsByPool.put(pool, forScaling);
                        }

                        if (remaining.decrementAndGet() == 0) {
                            finalizeScale(pools, itemsByPool);
                        }
                    }));
        }
    }

    private void finalizeScale(List<RewardPool> pools, Map<RewardPool, List<SimpleItem>> itemsByPool) {
        // All pools loaded; compute tallest natural height
        double tallest = 0.0;
        for (RewardPool p : pools) {
            List<SimpleItem> list = itemsByPool.getOrDefault(p, List.of());
            double h = computeNaturalPoolHeight(list);
            if (h > tallest) tallest = h;
        }

        double available = this.height - MARGIN_Y - BOTTOM_PADDING;
        if (tallest <= 0) {
            tallest =
                    Sprite.LOOTRUN_POOL_TOP_SECTION.height() * TOP_AWNING_OVERLAP + Sprite.POOL_BOTTOM_SECTION.height();
        }

        this.globalPoolScale = available / tallest;
        this.scaleReady = true;
        this.lastWidth = this.width;
        this.lastHeight = this.height;
        this.recalculating = false;

        // If multiple resizes happened during calculation, run one more pass
        if (this.pendingRecalc) {
            this.pendingRecalc = false;
            this.minecraft.execute(this::recalcScaleAsync);
            return;
        }

        // Rebuild to apply scale across layout
        this.minecraft.execute(this::rebuildWidgets);
    }

    private double computeNaturalPoolHeight(List<SimpleItem> items) {
        // Sections depend on active type; itemsPerRow is fixed 9
        int headerH = Sprite.POOL_MIDDLE_SECTION_HEADER.height(); // 41
        int middleH = Sprite.POOL_MIDDLE_SECTION.height(); // 22
        int bottomH = Sprite.POOL_BOTTOM_SECTION.height(); // 13
        double topOverlap = Sprite.LOOTRUN_POOL_TOP_SECTION.height() * TOP_AWNING_OVERLAP;

        int sectionsHeight = 0;
        List<SectionData> sections = buildSections(items);
        for (SectionData sd : sections) {
            sectionsHeight += sectionHeightForCount(sd.items.size(), ITEMS_PER_ROW, headerH, middleH);
        }
        if (sectionsHeight == 0) return topOverlap + bottomH; // minimal footprint
        return topOverlap + sectionsHeight + bottomH;
    }

    private int sectionHeightForCount(int count, int itemsPerRow, int headerH, int middleH) {
        // Always account for the header height even if the section has no items
        if (count <= 0) return headerH;
        int rows = (int) Math.ceil(count / (double) itemsPerRow);
        return headerH + Math.max(0, (rows - 1) * middleH);
    }

    // --- Common section building and rendering helpers ---
    private List<SectionData> buildSections(List<SimpleItem> items) {
        List<SectionData> sections = new ArrayList<>();
        if (activeType == RewardType.RAID) {
            Map<SimpleItemType, List<SimpleItem>> grouped = new HashMap<>();
            for (SimpleItem item : items) {
                SimpleItemType type = item.getItemTypeEnum();
                grouped.computeIfAbsent(type, k -> new ArrayList<>()).add(item);
            }

            sections.add(new SectionData("Aspects", grouped.getOrDefault(SimpleItemType.ASPECT, List.of())));
            sections.add(new SectionData("Tomes", grouped.getOrDefault(SimpleItemType.TOME, List.of())));
            sections.add(new SectionData("Gear", grouped.getOrDefault(SimpleItemType.GEAR, List.of())));
            sections.add(new SectionData(
                    "Misc",
                    items.stream()
                            .filter(i -> !List.of(SimpleItemType.ASPECT, SimpleItemType.TOME, SimpleItemType.GEAR)
                                    .contains(i.getItemTypeEnum()))
                            .toList()));
        } else { // LOOTRUN by rarity tiers
            Map<GearTier, List<SimpleItem>> groupedByRarity = new HashMap<>();
            for (SimpleItem item : items) {
                groupedByRarity
                        .computeIfAbsent(item.getRarityEnum(), k -> new ArrayList<>())
                        .add(item);
            }
            GearTier[] tiers = new GearTier[] {
                GearTier.MYTHIC, GearTier.FABLED, GearTier.LEGENDARY, GearTier.RARE, GearTier.UNIQUE, GearTier.NORMAL
            };
            for (GearTier tier : tiers) {
                List<SimpleItem> tierItems = groupedByRarity.getOrDefault(tier, new ArrayList<>());
                sections.add(new SectionData(tier.getName(), tierItems));
            }
        }
        return sections;
    }

    private void renderSectionsCommon(
            int startX, int startY, List<SectionData> sections, int totalWidth, double poolScale, RewardPool pool) {
        int currentY = startY;
        for (SectionData sd : sections) {
            currentY = renderSection(startX, currentY, sd.title, sd.items, totalWidth, poolScale);
        }
        renderBottomSection(startX, currentY, totalWidth, poolScale);
        renderPoolHeader(startX, totalWidth, poolScale, pool.getShortName());
    }

    // Lightweight inner model to represent a visual section in a pool
    private static class SectionData {
        final String title;
        final List<SimpleItem> items;

        SectionData(String title, List<SimpleItem> items) {
            this.title = title;
            this.items = items;
        }
    }
}
