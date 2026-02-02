package com.wynnventory.handler;

import com.wynntils.mc.event.ItemTooltipRenderEvent;
import com.wynnventory.core.queue.QueueManager;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;
import com.wynnventory.model.item.trademarket.TrademarketItemSummary;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.util.FixedTooltipPositioner;
import com.wynnventory.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Vector2i;

import java.util.List;
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

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        Optional<TooltipComponent> tooltipImage = itemStack.getTooltipImage();
        Identifier background = itemStack.get(DataComponents.TOOLTIP_STYLE);

        List<Component> tooltipLines = event.getTooltips();
        if (tooltipLines == null || tooltipLines.isEmpty()) return;
        List<ClientTooltipComponent> vanillaComponents = toClientComponents(tooltipLines, tooltipImage);
        List<Component> priceLines = getTooltips(snapshot.live(), itemStack.getCustomName()); //TODO: move somewhere?
        List<ClientTooltipComponent> priceComponents = toClientComponents(priceLines, Optional.empty());


        Vector2i tooltipCoords = RenderUtils.calculateTooltipCoords(event.getMouseX(), event.getMouseY(), vanillaComponents, priceComponents);
        ClientTooltipPositioner fixed = new FixedTooltipPositioner(tooltipCoords.x, tooltipCoords.y);
        event.getGuiGraphics().renderTooltip(font, priceComponents, event.getMouseX(), event.getMouseY(),fixed, background);
    }

    public static List<Component> getTooltips(TrademarketItemSummary summary, Component customName) {
        List<Component> tooltips = new java.util.ArrayList<>(List.of());

        tooltips.add(customName);
        if(summary == null ||summary.isEmpty()) {
            tooltips.add(Component.literal("No data yet.").withStyle(ChatFormatting.RED));
            return tooltips;
        }

        if(summary.getAverageMid80PercentPrice() != null) tooltips.add(createPriceLine("80% avg", summary.getAverageMid80PercentPrice().toString()));
        if(summary.getUnidentifiedAverageMid80PercentPrice() != null) tooltips.add(createPriceLine("Unid 80% avg", summary.getUnidentifiedAverageMid80PercentPrice().toString()));
        if(summary.getAveragePrice() != null) tooltips.add(createPriceLine("Avg", summary.getAveragePrice().toString()));
        if(summary.getUnidentifiedAveragePrice() != null) tooltips.add(createPriceLine("Unid Avg", summary.getUnidentifiedAveragePrice().toString()));
        if(summary.getHighestPrice() != null) tooltips.add(createPriceLine("Highest", summary.getHighestPrice().toString()));
        if(summary.getLowestPrice() != null) tooltips.add(createPriceLine("Lowest", summary.getLowestPrice().toString()));

        return tooltips;
    }

    private static Component createPriceLine(String name, String price) {
        MutableComponent line = Component.literal(name + ": ").withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        line.append(Component.literal(price)
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        return line;
    }

    private static List<ClientTooltipComponent> toClientComponents(List<Component> lines, Optional<TooltipComponent> tooltipImage) {
        List<ClientTooltipComponent> list = lines.stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .collect(Util.toMutableList());

        tooltipImage.ifPresent(img ->
                list.add(list.isEmpty() ? 0 : 1, ClientTooltipComponent.create(img))
        );

        return list;
    }
}