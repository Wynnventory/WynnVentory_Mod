package com.wynnventory.gui.screen.settings;

import com.wynnventory.gui.screen.SettingsScreen;
import net.minecraft.client.gui.GuiGraphics;

public interface SettingsTab {
    void init(SettingsScreen screen, int x1, int x2, int y, int w, int h);
    
    default void render(GuiGraphics graphics, int mouseX, int mouseY, float delta, int x1, int x2, int y, int w, int h) {
    }
}
