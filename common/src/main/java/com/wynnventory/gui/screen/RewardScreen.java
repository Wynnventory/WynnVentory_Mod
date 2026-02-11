package com.wynnventory.gui.screen;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.wynnventory.api.RewardManager;
import com.wynnventory.gui.Sprite;
import com.wynnventory.gui.widget.ItemButton;
import com.wynnventory.gui.widget.ImageButton;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.RewardScreenSettings;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardType;
import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.aspect.GuideAspectItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.powder.GuidePowderItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RewardScreen extends Screen {

    private final Screen parent;
    private RewardType activeType = RewardType.LOOTRUN;
    private final Map<String, List<GuideItemStack>> wynnItemsByName = new HashMap<>();

    private int scrollIndex = 0;
    private final int lootrunColumns = ModConfig.getInstance().getRewardScreenSettings().getLootrunColumns();
    private final int raidColumns = ModConfig.getInstance().getRewardScreenSettings().getRaidColumns();

    private EditBox searchBar;
    private String currentQuery = "";


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
            loadAllItems();
        }

        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 10;
        int startX = (this.width - (buttonWidth * 2 + spacing)) / 2;
        int startY = 10;

        Button lootrunButton = Button.builder(Component.translatable("gui.wynnventory.reward.lootrun"), button -> {
            this.activeType = RewardType.LOOTRUN;
            this.scrollIndex = 0;
            this.rebuildWidgets();
        }).bounds(startX, startY, buttonWidth, buttonHeight).build();
        lootrunButton.active = (activeType != RewardType.LOOTRUN);
        this.addRenderableWidget(lootrunButton);

        Button raidButton = Button.builder(Component.translatable("gui.wynnventory.reward.raid"), button -> {
            this.activeType = RewardType.RAID;
            this.scrollIndex = 0;
            this.rebuildWidgets();
        }).bounds(startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight).build();
        raidButton.active = (activeType != RewardType.RAID);
        this.addRenderableWidget(raidButton);

        // Settings Button
        int sideButtonSize = 20;
        this.addRenderableWidget(new ImageButton(this.width - sideButtonSize - 10, startY, sideButtonSize, sideButtonSize, Sprite.SETTINGS_BUTTON, b -> SettingsScreen.open(), Component.literal("Open mod settings")));

        // Reload Button
        this.addRenderableWidget(new ImageButton(this.width - (sideButtonSize * 2) - 15, startY, sideButtonSize, sideButtonSize, Sprite.RELOAD_BUTTON, b -> {
            RewardManager.reloadAllPools();
            this.rebuildWidgets();
        }, Component.literal("Reload Lootpools")));

        List<RewardPool> activePools = getActivePools();

        // Carousel buttons
        int navButtonWidth = 20;
        int navButtonHeight = 20;
        int navY = 40;
        int margin = 20;

        int currentColumns = getCurrentColumns();

        Button prevButton = Button.builder(Component.literal("<"), button -> {
            scrollIndex--;
            if (scrollIndex < 0) {
                scrollIndex = activePools.size() - 1;
            }
            this.rebuildWidgets();
        }).bounds(margin, navY, navButtonWidth, navButtonHeight).build();
        prevButton.active = activePools.size() > currentColumns;
        this.addRenderableWidget(prevButton);

        Button nextButton = Button.builder(Component.literal(">"), button -> {
            scrollIndex++;
            if (scrollIndex >= activePools.size()) {
                scrollIndex = 0;
            }
            this.rebuildWidgets();
        }).bounds(this.width - 130 - margin, navY, navButtonWidth, navButtonHeight).build();
        nextButton.active = activePools.size() > currentColumns;
        this.addRenderableWidget(nextButton);

        // Search Bar
        int sidebarWidth = 100;
        int sidebarX = this.width - sidebarWidth - 10;
        searchBar = new EditBox(this.font, sidebarX, navY, sidebarWidth, 20, Component.translatable("gui.wynnventory.reward.search"));
        searchBar.setValue(currentQuery);
        searchBar.setResponder(text -> {
            this.currentQuery = text;
            this.rebuildWidgets();
        });
        this.addRenderableWidget(searchBar);

        // Filters
        int filterY = navY + 25;
        RewardScreenSettings s = ModConfig.getInstance().getRewardScreenSettings();
        addFilterButton("Mythic", s::isShowMythic, s::setShowMythic, sidebarX, filterY, sidebarWidth);
        addFilterButton("Fabled", s::isShowFabled, s::setShowFabled, sidebarX, filterY + 22, sidebarWidth);
        addFilterButton("Legendary", s::isShowLegendary, s::setShowLegendary, sidebarX, filterY + 44, sidebarWidth);
        addFilterButton("Rare", s::isShowRare, s::setShowRare, sidebarX, filterY + 66, sidebarWidth);
        addFilterButton("Unique", s::isShowUnique, s::setShowUnique, sidebarX, filterY + 88, sidebarWidth);
        addFilterButton("Common", s::isShowCommon, s::setShowCommon, sidebarX, filterY + 110, sidebarWidth);
        addFilterButton("Set", s::isShowSet, s::setShowSet, sidebarX, filterY + 132, sidebarWidth);

        populateItemWidgets();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        int sidebarWidth = 100;
        int startY = 40;
        int startX = getStartX();
        int contentWidth = getContentWidth();

        List<RewardPool> allActivePools = getActivePools();
        int currentColumns = getCurrentColumns();

        if (!allActivePools.isEmpty()) {
            int displayCount = Math.min(currentColumns, allActivePools.size());
            int sectionWidth = contentWidth / currentColumns;

            for (int i = 0; i < displayCount; i++) {
                int poolIndex = (scrollIndex + i) % allActivePools.size();
                RewardPool pool = allActivePools.get(poolIndex);
                int currentX = startX + (i * sectionWidth);

                graphics.fill(currentX, startY, currentX + sectionWidth - 2, startY + 12, 0x44FFFFFF);

                int nameWidth = this.font.width(pool.getShortName());
                graphics.drawString(this.font, pool.getShortName(), currentX + (sectionWidth - nameWidth) / 2, startY + 2, 0xFFFFFFFF);

                if (i > 0) {
                    graphics.fill(currentX - 1, startY, currentX, this.height - 10, 0x22FFFFFF);
                }
            }
        }

        int sidebarX = this.width - sidebarWidth - 10;
        graphics.fill(sidebarX, startY, this.width - 10, this.height - 10, 0x22FFFFFF);
        graphics.drawCenteredString(this.font, Component.translatable("gui.wynnventory.reward.search"), sidebarX + sidebarWidth / 2, startY + 10, 0xFFAAAAAA);

//        for (ItemButton<GuideItemStack> widget : itemWidgets) {
//            if (widget.isHovered()) {
//                graphics.setTooltipForNextFrame(this.font, widget.getItemStack(), mouseX, mouseY);
//            }
//        }
    }


    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
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
            wynnItemsByName.computeIfAbsent(nameMapper.apply(item), k -> new ArrayList<>()).add(item);
        }
    }

    private void populateItemWidgets() {
        int startY = 40;
        int startX = getStartX();
        int contentWidth = getContentWidth();

        List<RewardPool> allActivePools = getActivePools();
        int currentColumns = getCurrentColumns();

        if (allActivePools.isEmpty()) return;

        int displayCount = Math.min(currentColumns, allActivePools.size());
        int sectionWidth = contentWidth / currentColumns;

        for (int i = 0; i < displayCount; i++) {
            int poolIndex = (scrollIndex + i) % allActivePools.size();
            RewardPool pool = allActivePools.get(poolIndex);
            int currentX = startX + (i * sectionWidth);

            // Items grid metrics within this section
            int itemsPerRow = (sectionWidth - 10) / 20; // 18px buttons with 2px gaps roughly
            int itemXStart = currentX + 5;
            int itemYStart = startY + 15;

            RewardScreenSettings s = ModConfig.getInstance().getRewardScreenSettings();
            List<SimpleItem> items = RewardManager.getItems(pool);

            int displayedItemIndex = 0;
            for (SimpleItem item : items) {
                if (!matchesFilters(item, s)) continue;
                if (!currentQuery.isEmpty() && !item.getName().toLowerCase().contains(currentQuery.toLowerCase())) continue;

                List<GuideItemStack> stacks = wynnItemsByName.get(item.getName());
                if (stacks == null || stacks.isEmpty()) continue;

                GuideItemStack stack = stacks.get(0);
                int row = displayedItemIndex / itemsPerRow;
                int col = displayedItemIndex % itemsPerRow;

                int x = itemXStart + col * 20;
                int y = itemYStart + row * 20;

                // Stop if we would draw beyond bottom area
                if (y + 18 > this.height - 10) break;

                ItemButton<GuideItemStack> button = new ItemButton<>(x, y, 18, 18, stack);
                this.addRenderableWidget(button);

                displayedItemIndex++;
            }
        }
    }

    private void addFilterButton(String label, java.util.function.BooleanSupplier getter, Consumer<Boolean> setter, int x, int y, int w) {
        this.addRenderableWidget(Button.builder(Component.literal(label + ": " + (getter.getAsBoolean() ? "ON" : "OFF")), b -> {
            setter.accept(!getter.getAsBoolean());
            try {
                ModConfig.getInstance().save();
            } catch (IOException ignored) {}
            this.rebuildWidgets();
        }).bounds(x, y, w, 20).build());
    }

    private boolean matchesFilters(SimpleItem item, RewardScreenSettings s) {
        String r = item.getRarity();
        if (r == null) return s.isShowCommon();
        return switch (r.toLowerCase()) {
            case "mythic" -> s.isShowMythic();
            case "fabled" -> s.isShowFabled();
            case "legendary" -> s.isShowLegendary();
            case "rare" -> s.isShowRare();
            case "unique" -> s.isShowUnique();
            case "set" -> s.isShowSet();
            default -> s.isShowCommon();
        };
    }


    private int getStartX() {
        return 20 + 20 + 15;
    }

    private int getEndX() {
        return this.width - 110 - 20 - 20 - 15;
    }

    private int getContentWidth() {
        return getEndX() - getStartX();
    }

    private List<RewardPool> getActivePools() {
        return Stream.of(RewardPool.values())
                .filter(pool -> pool.getType() == activeType)
                .toList();
    }

    private int getCurrentColumns() {
        return activeType == RewardType.LOOTRUN ? lootrunColumns : raidColumns;
    }
}