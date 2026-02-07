package com.wynnventory.gui.screen;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.gui.screen.settings.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class SettingsScreen extends Screen {

    private final Screen parent;
    private Tab activeTab = Tab.TOOLTIP;

    public SettingsScreen(Screen parent) {
        super(Component.translatable("gui.wynnventory.settings.title"));
        this.parent = parent;
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new SettingsScreen(mc.screen));
    }

    public <T extends AbstractWidget> T addPublic(T widget) {
        return super.addRenderableWidget(widget);
    }

    @Override
    protected void init() {
        int tabWidth = 80;
        int tabHeight = 20;
        int spacing = 5;
        int totalTabsWidth = (tabWidth + spacing) * Tab.values().length - spacing;
        int startX = (this.width - totalTabsWidth) / 2;
        int startY = 30;

        for (Tab tab : Tab.values()) {
            Button tabButton = Button.builder(tab.getTitle(), button -> {
                this.activeTab = tab;
                this.rebuildWidgets();
            }).bounds(startX + tab.ordinal() * (tabWidth + spacing), startY, tabWidth, tabHeight).build();
            if (this.activeTab == tab) {
                tabButton.active = false;
            }
            this.addRenderableWidget(tabButton);
        }

        int optionsY = startY + tabHeight + 20;
        int column1X = this.width / 2 - 155;
        int column2X = this.width / 2 + 5;
        int buttonWidth = 150;
        int entryHeight = 24;

        activeTab.getTab().init(this, column1X, column2X, optionsY, buttonWidth, entryHeight);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);

        int tabHeight = 20;
        int startY = 30;
        int optionsY = startY + tabHeight + 20;
        int column1X = this.width / 2 - 155;
        int column2X = this.width / 2 + 5;
        int buttonWidth = 150;
        int entryHeight = 24;

        activeTab.getTab().render(graphics, mouseX, mouseY, delta, column1X, column2X, optionsY, buttonWidth, entryHeight);

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        try {
            ModConfig.getInstance().save();
        } catch (IOException e) {
            WynnventoryMod.logError("Failed to save config", e);
        }
        this.minecraft.setScreen(this.parent);
    }

    private enum Tab {
        TOOLTIP("gui.wynnventory.settings.tab.tooltip", new TooltipSettingsTab()),
        PRICE_HIGHLIGHT("gui.wynnventory.settings.tab.highlighting", new PriceHighlightSettingsTab()),
        NOTIFICATIONS("gui.wynnventory.settings.tab.notifications", new NotificationSettingsTab()),
        RARITY("gui.wynnventory.settings.tab.rarity", new RaritySettingsTab());

        private final String translationKey;
        private final SettingsTab tab;

        Tab(String translationKey, SettingsTab tab) {
            this.translationKey = translationKey;
            this.tab = tab;
        }

        public Component getTitle() {
            return Component.translatable(translationKey);
        }

        public SettingsTab getTab() {
            return tab;
        }
    }
}
