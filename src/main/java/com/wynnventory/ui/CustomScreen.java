package com.wynnventory.ui;

import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.gear.GuideGearItemStack;
import com.wynntils.utils.render.FontRenderer;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CustomScreen extends Screen {
    protected List<GuideGearItemStack> allGearItems = List.of();
    private final List<WynnventoryButton> elementButtons = new ArrayList<>();

    private List<Lootpool> raidpools = new ArrayList<>();
    private List<Lootpool> lootrunpools = new ArrayList<>();

    public CustomScreen(Component title) {
        super(title);

        WynnventoryAPI api = new WynnventoryAPI();
        raidpools = api.getLootpools("raid");
        lootrunpools = api.getLootpools("lootrun");

        getAllGearItems();
    }

    @Override
    protected void init() {
        super.init();

        // Render items in a grid
        int gridX = 50; // Starting X position
        int gridY = 80; // Starting Y position
        int itemSize = 16; // Item size (standard 16x16 pixels)
        int itemsPerRow = 5; // Number of items per row
        int padding = 8; // Padding between items

        List<LootpoolItem> items = new ArrayList<>(lootrunpools.get(0).getItems());
        int renderedItems = 0;
        for (int i = 0; i < items.size(); i++) {
            int x = gridX + (renderedItems % itemsPerRow) * (itemSize + padding);
            int y = gridY + (renderedItems / itemsPerRow) * (itemSize + padding);

            for (GuideGearItemStack stack : allGearItems) {
                if (stack.getGearInfo().name().equals(items.get(i).getName())) {
                    WynnventoryButton button = new WynnventoryButton(x, y, 10, 10, stack, this);
                    elementButtons.add(button);
                    this.addRenderableWidget(button);

                    renderedItems++;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for(WynnventoryButton button : elementButtons) {
            if(button.isHovered()) {
                guiGraphics.renderTooltip(
                        FontRenderer.getInstance().getFont(), button.getItemStack(), mouseX, mouseY);
            }
        }
    }

    private List<GuideGearItemStack> getAllGearItems() {
        if (allGearItems.isEmpty()) {
            allGearItems = Models.Gear.getAllGearInfos().map(GuideGearItemStack::new).toList();
        }

        return allGearItems;
    }
}
