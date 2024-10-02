package com.wynnventory.ui;

import com.mojang.blaze3d.platform.InputConstants;
import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.screens.guides.tome.GuideTomeItemStack;
import com.wynntils.utils.render.FontRenderer;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class CustomScreen extends Screen {
    Map<String, List<GuideItemStack>> stacksByName = new HashMap<>();

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

        loadAllItems();
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

                String itemName = item.getName();
                List<GuideItemStack> matchingStacks = stacksByName.get(itemName);

                if (matchingStacks != null && !matchingStacks.isEmpty()) {
                    for (GuideItemStack stack : matchingStacks) {
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

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int arg1, int arg2, float arg3) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000); // Solid black
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (WynnventoryMod.KEY_OPEN_CONFIG.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void loadAllItems() {
        List<GuideGearItemStack> gear = Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList();
        List<GuideTomeItemStack> tomes = Models.Rewards.getAllTomeInfos().map(GuideTomeItemStack::new).toList();

        for (GuideGearItemStack stack : gear) {
            String name = stack.getGearInfo().name();
            stacksByName.computeIfAbsent(name, k -> new ArrayList<>()).add(stack);
        }

        for (GuideTomeItemStack stack : tomes) {
            String name = stack.getTomeInfo().name();
            stacksByName.computeIfAbsent(name, k -> new ArrayList<>()).add(stack);
        }
    }
}
