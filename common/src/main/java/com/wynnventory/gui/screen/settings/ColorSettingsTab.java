package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.ColorSettings;
import com.wynnventory.gui.screen.SettingsScreen;
import com.wynnventory.util.EmeraldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ColorSettingsTab implements SettingsTab {
    @Override
    public void init(SettingsScreen screen, int x1, int x2, int y, int w, int h) {
        ColorSettings s = ModConfig.getInstance().getColorSettings();
        Minecraft mc = Minecraft.getInstance();

        screen.addPublic(CycleButton.onOffBuilder(s.isShowColors())
                .create(x1, y, w, 20, Component.translatable("gui.wynnventory.settings.colors.showColors"), (btn, val) -> s.setShowColors(val)));

        int currentY = y + h + 15;
        EditBox minPriceBox = new EditBox(mc.font, x1, currentY, w, 20, Component.translatable("gui.wynnventory.settings.colors.colorMinPrice"));
        minPriceBox.setValue(String.valueOf(s.getColorMinPrice()));
        minPriceBox.setFilter(val -> val.matches("\\d*"));
        minPriceBox.setResponder(val -> {
            if (!val.isEmpty()) {
                try {
                    s.setColorMinPrice(Integer.parseInt(val));
                } catch (NumberFormatException ignored) {}
            }
        });
        screen.addPublic(minPriceBox);

        currentY += h + 15;
        final EditBox[] hexBoxArr = new EditBox[1];
        EditBox hexBox = new EditBox(mc.font, x1, currentY, w, 20, Component.translatable("gui.wynnventory.settings.colors.hexCode"));
        hexBox.setValue(String.format("#%06X", s.getHighlightColor()));
        hexBox.setFilter(val -> val.matches("^#?[0-9a-fA-F]{0,6}$"));
        hexBoxArr[0] = hexBox;
        screen.addPublic(hexBox);

        currentY += h + 15;
        ColorSlider slider = new ColorSlider(x1, currentY, w - 30, 20, Component.translatable("gui.wynnventory.settings.colors.highlightColor"), getHue(s.getHighlightColor()), s, hexBoxArr);

        hexBox.setResponder(val -> {
            String hex = val.startsWith("#") ? val.substring(1) : val;
            if (hex.length() == 6) {
                try {
                    int color = Integer.parseInt(hex, 16);
                    s.setHighlightColor(color);
                    slider.updateFromColor(color);
                } catch (NumberFormatException ignored) {}
            }
        });
        screen.addPublic(slider);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta, int x1, int x2, int y, int w, int h) {
        ColorSettings s = ModConfig.getInstance().getColorSettings();
        Minecraft mc = Minecraft.getInstance();

        graphics.drawString(mc.font, Component.translatable("gui.wynnventory.settings.colors.colorMinPrice"), x1, y + h + 4, 0xFFAAAAAA);

        // Emerald formatted string next to the input box
        String formattedEmeralds = EmeraldUtils.getFormattedString(s.getColorMinPrice(), false);
        graphics.drawString(mc.font, Component.literal(formattedEmeralds), x1 + 150 + 10, y + h + 15 + 6, 0xFF55FF55);
        
        int hexLabelY = y + (h + 15) + h + 4;
        graphics.drawString(mc.font, Component.translatable("gui.wynnventory.settings.colors.hexCode"), x1, hexLabelY, 0xFFAAAAAA);
        
        int sliderLabelY = y + 2 * (h + 15) + h + 4;
        graphics.drawString(mc.font, Component.translatable("gui.wynnventory.settings.colors.highlightColor"), x1, sliderLabelY, 0xFFAAAAAA);
        
        // Preview box for highlight color
        int previewX = x1 + 150 - 20;
        int previewY = y + 2 * (h + 15) + h + 15;
        graphics.fill(previewX, previewY, previewX + 20, previewY + 20, 0xFF000000 | s.getHighlightColor());
    }

    private static float getHue(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        if (delta == 0) return 0;
        float hue;
        if (max == r) hue = ((g - b) / delta);
        else if (max == g) hue = (b - r) / delta + 2;
        else hue = (r - g) / delta + 4;
        hue /= 6;
        if (hue < 0) hue += 1;
        return hue;
    }

    private static class ColorSlider extends AbstractSliderButton {
        private final ColorSettings settings;
        private final EditBox[] hexBoxArr;
        private boolean isUpdating = false;

        public ColorSlider(int x, int y, int width, int height, Component message, double value, ColorSettings settings, EditBox[] hexBoxArr) {
            super(x, y, width, height, message, value);
            this.settings = settings;
            this.hexBoxArr = hexBoxArr;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.translatable("gui.wynnventory.settings.colors.highlightColor"));
        }

        @Override
        protected void applyValue() {
            if (isUpdating) return;
            isUpdating = true;
            int color = Mth.hsvToRgb((float) this.value, 1.0f, 1.0f) & 0xFFFFFF;
            settings.setHighlightColor(color);
            if (hexBoxArr[0] != null) {
                hexBoxArr[0].setValue(String.format("#%06X", color));
            }
            isUpdating = false;
        }

        public void updateFromColor(int color) {
            if (isUpdating) return;
            isUpdating = true;
            float hue = getHue(color);
            if (hue == 0 && this.value > 0.5) {
                hue = 1.0f;
            }
            this.value = hue;
            this.updateMessage();
            isUpdating = false;
        }
    }
}
