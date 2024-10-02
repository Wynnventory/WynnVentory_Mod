package com.wynnventory.ui;

import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.render.FontRenderer;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CustomScreen extends Screen {
    protected List<GuideGearItemStack> allGearItems = List.of();
    protected List<GuideTomeItemStack> allTomeItems = List.of();
    private final List<WynnventoryButton> elementButtons = new ArrayList<>();

    private List<Lootpool> raidpools = new ArrayList<>();
    private List<Lootpool> lootrunpools = new ArrayList<>();

    private int padding = 50;
    private int itemSize = 16; // Item size (standard 16x16 pixels)
    private int itemsPerRow = 5; // Number of items per row
    private int itemPadding = 8; // Padding between items
    private int colWidth = (itemSize * itemsPerRow) + (itemPadding * itemsPerRow);

    public CustomScreen(Component title) {
        super(title);

        WynnventoryAPI api = new WynnventoryAPI();
        raidpools = api.getLootpools("raid");
        lootrunpools = api.getLootpools("lootrun");

        getAllGearItems();
        getAllTomeItems();
    }

    @Override
    protected void init() {
        super.init();

        // Calculate total width to center the columns
        int totalColumns = lootrunpools.size();
        int totalWidth = totalColumns * colWidth + (totalColumns - 1) * padding;
        int startX = (this.width - totalWidth) / 2;

        // CREATE COLS
        for (int i = 0; i < lootrunpools.size(); i++) {
            int gridX = startX + i * (colWidth + padding); // Starting X position
            int gridY = 80; // Starting Y position

            int renderedItems = 0;

            List<LootpoolItem> items = new ArrayList<>(lootrunpools.get(i).getItems());
            for (LootpoolItem item : items) {
                int x = gridX + (renderedItems % itemsPerRow) * (itemSize + itemPadding);
                int y = gridY + (renderedItems / itemsPerRow) * (itemSize + itemPadding);

                for (GuideGearItemStack stack : allGearItems) {
                    if (stack.getGearInfo().name().equals(item.getName())) {
                        WynnventoryButton button = new WynnventoryButton(x, y, itemSize, itemSize, stack, this);
                        elementButtons.add(button);
                        this.addRenderableWidget(button);

                        renderedItems++;
                    }
                }

                for (GuideTomeItemStack stack : allTomeItems) {
                    if (stack.getTomeInfo().name().equals(item.getName())) {
                        WynnventoryButton button = new WynnventoryButton(x, y, itemSize, itemSize, stack, this);
                        elementButtons.add(button);
                        this.addRenderableWidget(button);

                        renderedItems++;
                    }
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Calculate total width to center the columns
        int totalColumns = lootrunpools.size();
        int totalWidth = totalColumns * colWidth + (totalColumns - 1) * padding;
        int startX = (this.width - totalWidth) / 2;

        int gridY = 80; // Starting Y position
        for (int i = 0; i < lootrunpools.size(); i++) {
            int gridX = startX + i * (colWidth + padding); // Starting X position

            String title = lootrunpools.get(i).getRegion();
            guiGraphics.drawCenteredString(this.font, title, gridX + ((colWidth - itemPadding) / 2), gridY - this.font.lineHeight - 10, 0xFFFFFFFF);
        }

        for (WynnventoryButton button : elementButtons) {
            if (button.isHovered()) {
                guiGraphics.renderTooltip(FontRenderer.getInstance().getFont(), button.getItemStack(), mouseX, mouseY);
            }
        }
    }

    private List<GuideGearItemStack> getAllGearItems() {
        if (allGearItems.isEmpty()) {
            allGearItems = Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList();
        }

        return allGearItems;
    }

    private List<GuideTomeItemStack> getAllTomeItems() {
        if (allTomeItems.isEmpty()) {
            // Populate list
            allTomeItems = Models.Rewards.getAllTomeInfos()
                    .map(GuideTomeItemStack::new)
                    .toList();
        }

        return allTomeItems;
    }
}
