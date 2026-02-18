package com.wynnventory.gui.screen;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.gui.screen.settings.NotificationSettingsTab;
import com.wynnventory.gui.screen.settings.PriceHighlightSettingsTab;
import com.wynnventory.gui.screen.settings.SettingsTab;
import com.wynnventory.gui.screen.settings.TooltipSettingsTab;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class SettingsScreen extends OptionsSubScreen {
    private final Screen parent;

    public SettingsScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("gui.wynnventory.settings.title"));
        this.parent = parent;
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new SettingsScreen(mc.screen));
    }

    public static Screen create(Screen screen) {
        Minecraft mc = Minecraft.getInstance();
        return new SettingsScreen(mc.screen);
    }

    public <T extends AbstractWidget> T addPublic(T widget) {
        return super.addRenderableWidget(widget);
    }

    @Override
    protected void addOptions() {
        for (Section section : Section.values()) {
            this.list.addHeader(Component.translatable(section.translationKey));
            section.getTab().addOptions(this.list);
            section.getTab().initCustomWidgets(this, this.list);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        for (Section section : Section.values()) {
            section.getTab().render(graphics, mouseX, mouseY, delta, 0, 0, 0, 0, 0);
        }
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

    private enum Section {
        TOOLTIP("gui.wynnventory.settings.section.tooltip", new TooltipSettingsTab()),
        PRICE_HIGHLIGHT("gui.wynnventory.settings.section.highlighting", new PriceHighlightSettingsTab()),
        NOTIFICATIONS("gui.wynnventory.settings.section.notifications", new NotificationSettingsTab());

        private final String translationKey;
        private final SettingsTab tab;

        Section(String translationKey, SettingsTab tab) {
            this.translationKey = translationKey;
            this.tab = tab;
        }

        public SettingsTab getTab() {
            return tab;
        }
    }
}
