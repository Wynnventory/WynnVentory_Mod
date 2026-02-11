package com.wynnventory.handler;

import com.wynntils.mc.event.ItemTooltipRenderEvent;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.queue.QueueScheduler;
import com.wynnventory.core.tooltip.PriceTooltipBuilder;
import com.wynnventory.core.tooltip.PriceTooltipFactory;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Vector2i;

import java.util.List;
import java.util.Optional;

public final class TooltipRenderHandler {
    private ItemStack lastItem;
    private final PriceTooltipFactory tooltipFactory = new PriceTooltipFactory(new PriceTooltipBuilder());

    @SubscribeEvent
    public void onTrademarketTooltipRendered(TrademarketTooltipRenderedEvent event) {
        Slot hoveredItemSlot = event.getItemSlot();
        if (hoveredItemSlot.container instanceof Inventory) return;

        ItemStack hoveredItem = hoveredItemSlot.getItem();
        if (lastItem == hoveredItem) return;
        lastItem = hoveredItem;

        TrademarketListing listing = TrademarketListing.from(hoveredItem);
        if (listing == null) return;

        QueueScheduler.TRADEMARKET_QUEUE.addItem(listing);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltipRendered(ItemTooltipRenderEvent.Pre event) {
        if(!ModConfig.getInstance().getTooltipSettings().isShowTooltips()) return;

        ItemStack stack = event.getItemStack();
        List<Component> vanillaLines = event.getTooltips();
        if (vanillaLines == null || vanillaLines.isEmpty()) return;

        List<Component> priceLines = tooltipFactory.getPriceTooltip(stack);
        if (priceLines.isEmpty()) return;

        List<ClientTooltipComponent> priceComponents = RenderUtils.toClientComponents(priceLines, Optional.empty());
        List<ClientTooltipComponent> vanillaComponents = RenderUtils.toClientComponents(vanillaLines, stack.getTooltipImage());

        Vector2i tooltipCoords = RenderUtils.calculateTooltipCoords(event.getMouseX(), event.getMouseY(), vanillaComponents, priceComponents);
        ClientTooltipPositioner fixed = new RenderUtils.FixedTooltipPositioner(tooltipCoords.x, tooltipCoords.y);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        guiGraphics.pose().pushMatrix();

        float scale = RenderUtils.getScaleFactor(priceComponents);
        guiGraphics.pose().scale(scale, scale);

        guiGraphics.renderTooltip(Minecraft.getInstance().font, priceComponents, event.getMouseX(), event.getMouseY(), fixed, stack.get(DataComponents.TOOLTIP_STYLE));
        guiGraphics.pose().popMatrix();
    }
}