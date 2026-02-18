package com.wynnventory.gui.screen.settings;

import com.wynnventory.gui.screen.SettingsScreen;
import java.util.List;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.OptionsList;

public interface SettingsTab {
    List<OptionInstance<?>> getOptions();

    default void addOptions(OptionsList list) {
        List<OptionInstance<?>> options = getOptions();
        for (int i = 0; i < options.size(); i += 2) {
            OptionInstance<?> opt1 = options.get(i);
            OptionInstance<?> opt2 = (i + 1 < options.size()) ? options.get(i + 1) : null;
            list.addSmall(opt1, opt2);
        }
    }

    default void initCustomWidgets(SettingsScreen screen, OptionsList list) {}

    default void render(
            GuiGraphics graphics, int mouseX, int mouseY, float delta, int x1, int x2, int y, int w, int h) {}
}
