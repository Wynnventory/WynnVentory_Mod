package com.wynnventory.handler;

import com.wynntils.core.components.Models;
import com.wynntils.mc.event.ItemTooltipRenderEvent;
import com.wynntils.models.emeralds.type.EmeraldUnits;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.GearBoxItem;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.config.settings.DisplayOptions;
import com.wynnventory.core.queue.QueueManager;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.item.trademarket.TrademarketItemSnapshot;
import com.wynnventory.model.item.trademarket.TrademarketItemSummary;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.util.EmeraldUtils;
import com.wynnventory.util.RenderUtils;
import com.wynnventory.util.StringUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

import java.util.*;

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
        List<Component> tooltipLines = event.getTooltips();
        if (tooltipLines == null || tooltipLines.isEmpty()) return;

        Optional<WynnItem> optionalWynnItem = Models.Item.getWynnItem(itemStack);
        if(!optionalWynnItem.isPresent()) return;

        WynnItem wynnItem = optionalWynnItem.get();

        List<Component> priceLines = new ArrayList<>();

        if(wynnItem instanceof GearBoxItem gearboxItem) {
            Map<GearInfo, TrademarketItemSnapshot> snapshots = TrademarketItemSnapshot.resolveGearBoxItem(gearboxItem);
            for(Map.Entry<GearInfo, TrademarketItemSnapshot> entry : snapshots.entrySet()) {
                if(entry.getValue() == null || entry.getValue().live() == null) continue;
                priceLines.addAll(getTooltips(entry.getValue().live(), Component.literal(entry.getKey().name()).withStyle(entry.getKey().tier().getChatFormatting())));
                priceLines.add(Component.literal(""));
            }
        } else {
            TrademarketItemSnapshot snapshot = TrademarketItemSnapshot.resolveSnapshot(itemStack);
            if(snapshot == null || snapshot.live() == null) return;

            priceLines.addAll(getTooltips(snapshot.live(), itemStack.getCustomName()));
        }


        List<ClientTooltipComponent> priceComponents = RenderUtils.toClientComponents(priceLines, Optional.empty());
        List<ClientTooltipComponent> vanillaComponents = RenderUtils.toClientComponents(tooltipLines, itemStack.getTooltipImage());

        Vector2i tooltipCoords = RenderUtils.calculateTooltipCoords(event.getMouseX(), event.getMouseY(), vanillaComponents, priceComponents);
        ClientTooltipPositioner fixed = new RenderUtils.FixedTooltipPositioner(tooltipCoords.x, tooltipCoords.y);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        guiGraphics.pose().pushMatrix();

        float scale = RenderUtils.getScaleFactor(priceComponents);
        guiGraphics.pose().scale(scale, scale);

        guiGraphics.renderTooltip(Minecraft.getInstance().font, priceComponents, event.getMouseX(), event.getMouseY(), fixed, itemStack.get(DataComponents.TOOLTIP_STYLE));
        guiGraphics.pose().popMatrix();
    }

    private List<Component> getTooltips(TrademarketItemSummary summary, Component customName) {
        List<Component> tooltips = new java.util.ArrayList<>(List.of());

        tooltips.add(customName);
        if(summary == null || summary.isEmpty()) {
            tooltips.add(Component.literal("No data yet.").withStyle(ChatFormatting.RED));
            return tooltips;
        }

        if(ModConfig.get().getTooltipSettings().isShowAverage80Price())     tooltips.add(createPriceLine("80% avg",      summary.getAverageMid80PercentPrice()));
        if(ModConfig.get().getTooltipSettings().isShowUnidAverage80Price()) tooltips.add(createPriceLine("Unid 80% avg", summary.getUnidentifiedAverageMid80PercentPrice()));
        if(ModConfig.get().getTooltipSettings().isShowAveragePrice())       tooltips.add(createPriceLine("Avg",          summary.getAveragePrice()));
        if(ModConfig.get().getTooltipSettings().isShowUnidAveragePrice())   tooltips.add(createPriceLine("Unid Avg",     summary.getUnidentifiedAveragePrice()));
        if(ModConfig.get().getTooltipSettings().isShowMaxPrice())           tooltips.add(createPriceLine("Highest",      summary.getHighestPrice()));
        if(ModConfig.get().getTooltipSettings().isShowMinPrice())           tooltips.add(createPriceLine("Lowest",       summary.getLowestPrice()));

        tooltips.removeIf(Objects::isNull);
        return tooltips;
    }

    private Component createPriceLine(String name, Integer value) {
        if(value == null) return null;

        String price = ModConfig.get().getTooltipSettings().getDisplayFormat().equals(DisplayOptions.FORMATTED) ? EmeraldUtils.getFormattedString(value, false) : StringUtils.formatNumber(value) + EmeraldUnits.EMERALD.getSymbol();
        int priceColor = ModConfig.get().getColorSettings().isShowColors() && value >= ModConfig.get().getColorSettings().getColorMinPrice() ? ModConfig.get().getColorSettings().getHighlightColor() : ChatFormatting.GRAY.getColor();

        MutableComponent line = Component.literal(name + ": ").withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        line.append(Component.literal(price)
                .withStyle(Style.EMPTY.withColor(priceColor)));

        return line;
    }

    private Component createPriceLine(String name, Double value) {
        if (value == null) return null;

        return createPriceLine(name, value.intValue());
    }
}