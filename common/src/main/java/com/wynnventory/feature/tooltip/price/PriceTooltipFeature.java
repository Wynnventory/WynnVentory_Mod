package com.wynnventory.feature.tooltip.price;

import com.wynntils.mc.event.ItemTooltipRenderEvent;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.util.RenderUtils;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class PriceTooltipFeature {
    private final PriceTooltipFactory tooltipFactory = new PriceTooltipFactory(new PriceTooltipBuilder());

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onTooltipRendered(ItemTooltipRenderEvent.Pre event) {
        if (ModConfig.getInstance().getTooltipSettings().isShowTooltips()) {
            renderTooltip(
                    event.getGuiGraphics(),
                    event.getMouseX(),
                    event.getMouseY(),
                    event.getItemStack(),
                    event.getTooltips());
        }
    }

    private void renderTooltip(
            GuiGraphics guiGraphics, int mouseX, int mouseY, ItemStack stack, List<Component> vanillaLines) {
        if (vanillaLines == null || vanillaLines.isEmpty()) return;

        List<Component> priceLines = tooltipFactory.getPriceTooltip(stack);
        if (priceLines.isEmpty()) return;

        RenderUtils.drawTooltip(guiGraphics, mouseX, mouseY, vanillaLines, priceLines);
    }
}
