package com.wynnventory.gui.screen;

import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Stream;

public class RewardScreen extends Screen {

    private final Screen parent;
    private RewardType activeType = RewardType.LOOTRUN;

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
        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 10;
        int startX = (this.width - (buttonWidth * 2 + spacing)) / 2;
        int startY = 10;

        Button lootrunButton = Button.builder(Component.translatable("gui.wynnventory.reward.lootrun"), button -> {
            this.activeType = RewardType.LOOTRUN;
            this.rebuildWidgets();
        }).bounds(startX, startY, buttonWidth, buttonHeight).build();
        lootrunButton.active = (activeType != RewardType.LOOTRUN);
        this.addRenderableWidget(lootrunButton);

        Button raidButton = Button.builder(Component.translatable("gui.wynnventory.reward.raid"), button -> {
            this.activeType = RewardType.RAID;
            this.rebuildWidgets();
        }).bounds(startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight).build();
        raidButton.active = (activeType != RewardType.RAID);
        this.addRenderableWidget(raidButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        int sidebarWidth = 100;
        int startY = 40;
        int contentWidth = this.width - sidebarWidth - 20;
        int startX = 10;

        List<RewardPool> activePools = Stream.of(RewardPool.values())
                .filter(pool -> pool.getType() == activeType)
                .toList();

        if (!activePools.isEmpty()) {
            int sectionWidth = contentWidth / activePools.size();

            for (int i = 0; i < activePools.size(); i++) {
                RewardPool pool = activePools.get(i);
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
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}