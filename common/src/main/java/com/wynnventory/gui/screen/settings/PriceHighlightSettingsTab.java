package com.wynnventory.gui.screen.settings;

import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.PriceHighlightSettings;
import com.wynnventory.gui.screen.SettingsScreen;
import com.wynnventory.util.EmeraldUtils;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class PriceHighlightSettingsTab implements SettingsTab {
    @Override
    public List<OptionInstance<?>> getOptions() {
        PriceHighlightSettings s = ModConfig.getInstance().getPriceHighlightSettings();
        return List.of(
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.highlighting.showColors", s.isShowColors(), s::setShowColors),
                OptionInstance.createBoolean("gui.wynnventory.settings.highlighting.colorMinPrice", true, (b) -> {}),
                OptionInstance.createBoolean("gui.wynnventory.settings.highlighting.hexCode", true, (b) -> {}),
                OptionInstance.createBoolean("gui.wynnventory.settings.highlighting.highlightColor", true, (b) -> {}));
    }

    @Override
    public void addOptions(OptionsList list) {
        PriceHighlightSettings s = ModConfig.getInstance().getPriceHighlightSettings();
        list.addSmall(
                OptionInstance.createBoolean(
                        "gui.wynnventory.settings.highlighting.showColors", s.isShowColors(), s::setShowColors),
                null);
        list.addBig(
                OptionInstance.createBoolean("gui.wynnventory.settings.highlighting.colorMinPrice", true, (b) -> {}));
        list.addBig(OptionInstance.createBoolean("gui.wynnventory.settings.highlighting.hexCode", true, (b) -> {}));
        list.addBig(
                OptionInstance.createBoolean("gui.wynnventory.settings.highlighting.highlightColor", true, (b) -> {}));
    }

    private EditBox minPriceBox;
    private EditBox hexBox;
    private ColorSlider slider;
    private OptionsList listRef;

    @Override
    public void initCustomWidgets(SettingsScreen screen, OptionsList list) {
        PriceHighlightSettings s = ModConfig.getInstance().getPriceHighlightSettings();
        Minecraft mc = Minecraft.getInstance();

        minPriceBox = null;
        hexBox = null;
        slider = null;
        listRef = list;

        for (GuiEventListener child : list.children()) {
            if (child instanceof ContainerEventHandler ceh) {
                for (GuiEventListener entryChild : ceh.children()) {
                    if (entryChild instanceof AbstractWidget widget) {
                        String msg = widget.getMessage().getString();
                        if (msg.contains(Component.translatable("gui.wynnventory.settings.highlighting.colorMinPrice")
                                .getString())) {
                            setupMinPriceBox(screen, widget, s, mc);
                        } else if (msg.contains(Component.translatable("gui.wynnventory.settings.highlighting.hexCode")
                                .getString())) {
                            setupHexBox(screen, widget, s, mc);
                        } else if (msg.contains(
                                Component.translatable("gui.wynnventory.settings.highlighting.highlightColor")
                                        .getString())) {
                            setupColorSlider(screen, widget, s, mc);
                        }
                    }
                }
            }
        }

        if (hexBox != null && slider != null) {
            final EditBox[] hexBoxArr = new EditBox[] {hexBox};
            slider.setHexBoxArr(hexBoxArr);
            hexBox.setResponder(val -> {
                String hex = val.startsWith("#") ? val.substring(1) : val;
                if (hex.length() == 6) {
                    try {
                        int color = Integer.parseInt(hex, 16);
                        s.setHighlightColor(color);
                        slider.updateFromColor(color);
                    } catch (NumberFormatException ignored) {
                    }
                }
            });
        }
    }

    private void setupMinPriceBox(SettingsScreen screen, AbstractWidget dummy, PriceHighlightSettings s, Minecraft mc) {
        dummy.visible = false;
        dummy.active = false;
        int width = 150;
        int x = screen.width / 2 + 5;
        minPriceBox = new EditBox(mc.font, x, dummy.getY(), width, 20, dummy.getMessage());
        minPriceBox.setValue(String.valueOf(s.getColorMinPrice()));
        minPriceBox.setFilter(val -> val.matches("\\d*"));
        minPriceBox.setResponder(val -> {
            if (!val.isEmpty()) {
                try {
                    s.setColorMinPrice(Integer.parseInt(val));
                } catch (NumberFormatException ignored) {
                }
            }
        });
        screen.addPublic(minPriceBox);
    }

    private void setupHexBox(SettingsScreen screen, AbstractWidget dummy, PriceHighlightSettings s, Minecraft mc) {
        dummy.visible = false;
        dummy.active = false;
        int width = 150;
        int x = screen.width / 2 + 5;
        hexBox = new EditBox(mc.font, x, dummy.getY(), width, 20, dummy.getMessage());
        hexBox.setValue(String.format("#%06X", s.getHighlightColor()));
        hexBox.setFilter(val -> val.matches("^#?[0-9a-fA-F]{0,6}$"));
        screen.addPublic(hexBox);
    }

    private void setupColorSlider(SettingsScreen screen, AbstractWidget dummy, PriceHighlightSettings s, Minecraft mc) {
        dummy.visible = false;
        dummy.active = false;
        int width = 150;
        int x = screen.width / 2 + 5;
        slider = new ColorSlider(
                x, dummy.getY(), width, 20, dummy.getMessage(), getHue(s.getHighlightColor()), s, new EditBox[1]);
        screen.addPublic(slider);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta, int x1, int x2, int y, int w, int h) {
        PriceHighlightSettings s = ModConfig.getInstance().getPriceHighlightSettings();
        Minecraft mc = Minecraft.getInstance();

        if (minPriceBox != null && hexBox != null && slider != null && listRef != null) {
            int center = mc.getWindow().getGuiScaledWidth() / 2;

            // Define list boundaries for visibility check
            int listTop = listRef.getY();
            int listBottom = listRef.getY() + listRef.getHeight();

            // Update positions based on dummy widgets in the provided list
            for (GuiEventListener child : listRef.children()) {
                if (child instanceof ContainerEventHandler ceh) {
                    for (GuiEventListener entryChild : ceh.children()) {
                        if (entryChild instanceof AbstractWidget dummy) {
                            String msg = dummy.getMessage().getString();
                            int widgetX = center + 5;
                            int widgetY = dummy.getY();
                            boolean isVisible = widgetY >= listTop && (widgetY + 20) <= listBottom;

                            if (msg.contains(
                                    Component.translatable("gui.wynnventory.settings.highlighting.colorMinPrice")
                                            .getString())) {
                                minPriceBox.setY(widgetY);
                                minPriceBox.setX(widgetX);
                                minPriceBox.visible = isVisible;
                            } else if (msg.contains(
                                    Component.translatable("gui.wynnventory.settings.highlighting.hexCode")
                                            .getString())) {
                                hexBox.setY(widgetY);
                                hexBox.setX(widgetX);
                                hexBox.visible = isVisible;
                            } else if (msg.contains(
                                    Component.translatable("gui.wynnventory.settings.highlighting.highlightColor")
                                            .getString())) {
                                slider.setY(widgetY);
                                slider.setX(widgetX);
                                slider.visible = isVisible;
                            }
                        }
                    }
                }
            }

            // Draw labels and other elements only if their respective widgets are visible
            int labelX = center - 155;

            if (minPriceBox.visible) {
                Component minPriceLabel = Component.translatable("gui.wynnventory.settings.highlighting.colorMinPrice");
                graphics.drawString(mc.font, minPriceLabel, labelX, minPriceBox.getY() + 6, 0xFFFFFFFF);

                String formattedEmeralds = EmeraldUtils.getFormattedString(s.getColorMinPrice(), false);
                int textWidth = mc.font.width(formattedEmeralds);
                graphics.drawString(
                        mc.font,
                        Component.literal(formattedEmeralds),
                        minPriceBox.getX() - textWidth - 5,
                        minPriceBox.getY() + 6,
                        0xFF55FF55);
            }

            if (hexBox.visible) {
                Component hexCodeLabel = Component.translatable("gui.wynnventory.settings.highlighting.hexCode");
                graphics.drawString(mc.font, hexCodeLabel, labelX, hexBox.getY() + 6, 0xFFFFFFFF);
            }

            if (slider.visible) {
                Component highlightColorLabel =
                        Component.translatable("gui.wynnventory.settings.highlighting.highlightColor");
                graphics.drawString(mc.font, highlightColorLabel, labelX, slider.getY() + 6, 0xFFFFFFFF);

                // Preview box for highlight color - placed to the left of the slider
                int previewX = slider.getX() - 25;
                int previewY = slider.getY();
                graphics.fill(previewX, previewY, previewX + 20, previewY + 20, 0xFF000000 | s.getHighlightColor());
            }
        }
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
        private final PriceHighlightSettings settings;
        private EditBox[] hexBoxArr;
        private boolean isUpdating = false;

        public ColorSlider(
                int x,
                int y,
                int width,
                int height,
                Component message,
                double value,
                PriceHighlightSettings settings,
                EditBox[] hexBoxArr) {
            super(x, y, width, height, message, value);
            this.settings = settings;
            this.hexBoxArr = hexBoxArr;
            this.updateMessage();
        }

        public void setHexBoxArr(EditBox[] hexBoxArr) {
            this.hexBoxArr = hexBoxArr;
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.translatable("gui.wynnventory.settings.highlighting.highlightColor"));
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
