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

            // Header Background
            this.addRenderableWidget(new RectWidget(currentX, MARGIN_Y, sectionWidth - 2, 12, 0x44FFFFFF));

            // Pool Name (TextWidget)
            int nameWidth = this.font.width(pool.getShortName());
            int labelX = currentX + (sectionWidth - nameWidth) / 2;
            this.addRenderableWidget(new TextWidget(labelX, MARGIN_Y + 2, Component.literal(pool.getShortName())));

            // Section Separator (Vertical Line)
            if (i > 0) {
                this.addRenderableWidget(new RectWidget(currentX - 1, MARGIN_Y, 1, this.height - 10 - MARGIN_Y, 0x22FFFFFF));
            }

            // Items Grid
            int itemsPerRow = (sectionWidth - 10) / 20;
            int itemXStart = currentX + 5;
            int itemYStart = MARGIN_Y + 15;
            createItemButtons(itemXStart, itemYStart, pool, itemsPerRow);
        }
    }

    private void createItemButtons(int itemXStart, int itemYStart, RewardPool pool, int itemsPerRow) {
        RewardService.INSTANCE.getItems(pool).thenAccept(items -> Minecraft.getInstance().execute(() -> {
            if (getActivePools().stream().noneMatch(p -> p == pool)) return;

            int displayedItemIndex = 0;

            for (SimpleItem item : items) {
                if (!matchesFilters(item)) continue;

                GuideItemStack stack = getGuideItemStack(item);

                if (stack == null || stack.isEmpty()) continue;

                int row = displayedItemIndex / itemsPerRow;
                int col = displayedItemIndex % itemsPerRow;

                int x = itemXStart + col * 20;
                int y = itemYStart + row * 20;

                boolean shiny = item instanceof SimpleGearItem simpleGearItem && simpleGearItem.isShiny();

                ItemButton<GuideItemStack> button = new ItemButton<>(x, y, 18, 18, stack, item);
                this.addRenderableWidget(button);
                itemWidgets.add(button);

                displayedItemIndex++;
            }
        }));
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