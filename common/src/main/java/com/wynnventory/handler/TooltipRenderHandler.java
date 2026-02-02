package com.wynnventory.handler;

import com.wynntils.mc.event.ItemTooltipRenderEvent;
import com.wynnventory.core.queue.QueueManager;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;
import com.wynnventory.model.item.trademarket.TrademarketItemSummary;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Vector2i;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class TooltipRenderHandler {
    private ItemStack lastItem;

    @SubscribeEvent
    public void onTrademarketTooltipRendered(TrademarketTooltipRenderedEvent event) {
        Slot hoveredItemSlot = event.getItemSlot();
        if (hoveredItemSlot.container instanceof Inventory) return;

        ItemStack hoveredItem = hoveredItemSlot.getItem();
        if (lastItem == hoveredItem) return;
        lastItem = hoveredItem;

        TrademarketListing listing = TrademarketListing.from(hoveredItem);
        if (listing == null) return;

        QueueManager.TRADEMARKET_QUEUE.addItem(listing);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltipRendered(ItemTooltipRenderEvent.Pre event) {
        ItemStack itemStack = event.getItemStack();
        TrademarketItemSnapshot snapshot = TrademarketItemSnapshot.resolveSnapshot(itemStack);
        if(snapshot == null || snapshot.live() == null) return;

        List<Component> tooltipLines = event.getTooltips();
        if (tooltipLines == null || tooltipLines.isEmpty()) return;

        List<Component> priceLines = getTooltips(snapshot.live(), itemStack.getCustomName()); //TODO: move somewhere?
        List<ClientTooltipComponent> priceComponents = RenderUtils.toClientComponents(priceLines, Optional.empty());
        List<ClientTooltipComponent> vanillaComponents = RenderUtils.toClientComponents(tooltipLines, itemStack.getTooltipImage());

        Vector2i tooltipCoords = RenderUtils.calculateTooltipCoords(event.getMouseX(), event.getMouseY(), vanillaComponents, priceComponents);
        ClientTooltipPositioner fixed = new RenderUtils.FixedTooltipPositioner(tooltipCoords.x, tooltipCoords.y);

        event.getGuiGraphics().renderTooltip(Minecraft.getInstance().font, priceComponents, event.getMouseX(), event.getMouseY(), fixed, itemStack.get(DataComponents.TOOLTIP_STYLE));
    }

    private List<Component> getTooltips(TrademarketItemSummary summary, Component customName) {
        List<Component> tooltips = new java.util.ArrayList<>(List.of());

        tooltips.add(customName);
        if(summary == null ||summary.isEmpty()) {
            tooltips.add(Component.literal("No data yet.").withStyle(ChatFormatting.RED));
            return tooltips;
        }

        tooltips.add(createPriceLine("80% avg",      summary.getAverageMid80PercentPrice()));
        tooltips.add(createPriceLine("Unid 80% avg", summary.getUnidentifiedAverageMid80PercentPrice()));
        tooltips.add(createPriceLine("Avg",          summary.getAveragePrice()));
        tooltips.add(createPriceLine("Unid Avg",     summary.getUnidentifiedAveragePrice()));
        tooltips.add(createPriceLine("Highest",      summary.getHighestPrice()));
        tooltips.add(createPriceLine("Lowest",       summary.getLowestPrice()));

        tooltips.removeIf(Objects::isNull);
        return tooltips;
    }

    private Component createPriceLine(String name, Integer value) {
        if(value == null) return null;

        MutableComponent line = Component.literal(name + ": ").withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        line.append(Component.literal(value.toString())
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        return line;
    }

    private Component createPriceLine(String name, Double value) {
        if (value == null) return null;

        return createPriceLine(name, value.intValue());
    }
}