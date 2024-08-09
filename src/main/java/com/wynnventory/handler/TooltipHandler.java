package com.wynnventory.handler;

import com.wynntils.core.components.Models;
import com.wynntils.models.emeralds.EmeraldModel;
import com.wynntils.models.emeralds.type.EmeraldUnits;
import com.wynntils.models.items.items.game.GearItem;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TooltipHandler {
    private static final WynnventoryAPI API = new WynnventoryAPI();
    private static GearItem lastHoveredItem;
    private static TradeMarketItemPriceInfo lastHoveredItemPriceInfo;

    public static void registerTooltips() {
        // Register the ItemTooltipCallback event
        ItemTooltipCallback.EVENT.register(TooltipHandler::onTooltip);
    }

    private static void onTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> tooltips) {
        final String tradeMarketTitleText = "Trademarket Price Info";
        final Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(itemStack, GearItem.class);

        if (findComponentByText(tooltips, tradeMarketTitleText)) {
            return;
        }

        if (gearItemOptional.isPresent()) {
            TradeMarketItemPriceInfo priceInfo;
            GearItem currentItem = gearItemOptional.get();

            // Check if the price info is already cached
            if (lastHoveredItem == null || !lastHoveredItem.getName().equals(currentItem.getName())) {
                lastHoveredItem = currentItem;
                lastHoveredItemPriceInfo = API.fetchItemPriceForItem(itemStack);
            }

            Component spacer = Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            Component priceInfoTitle = Component.literal("Trademarket Price Info").withStyle(ChatFormatting.GOLD);
            tooltips.add(spacer);
            tooltips.add(priceInfoTitle);

            if (lastHoveredItemPriceInfo == null) {
                Component noPriceData = Component.literal("No price data available yet!").withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
                tooltips.add(noPriceData);
                tooltips.add(spacer);
            } else {
                final NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                final EmeraldModel emeraldModel = new EmeraldModel();

                int highestPrice = lastHoveredItemPriceInfo.getHighestPrice();
                int medianPrice = lastHoveredItemPriceInfo.getAveragePrice();
                int lowestPrice = lastHoveredItemPriceInfo.getLowestPrice();
                int unidentifiedAveragePrice = lastHoveredItemPriceInfo.getUnidentifiedAveragePrice();

                Component highestPriceComponent = Component.literal("Max: " + numberFormat.format(highestPrice) + EmeraldUnits.EMERALD.getSymbol()).withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)).append(Component.literal(" (" + emeraldModel.getFormattedString(highestPrice, false) + ")").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                Component medianPriceComponent = Component.literal("Min: " + numberFormat.format(medianPrice) + EmeraldUnits.EMERALD.getSymbol()).withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)).append(Component.literal(" (" + emeraldModel.getFormattedString(medianPrice, false) + ")").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                Component lowestPriceComponent = Component.literal("Avg: " + numberFormat.format(lowestPrice) + EmeraldUnits.EMERALD.getSymbol()).withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)).append(Component.literal(" (" + emeraldModel.getFormattedString(lowestPrice, false) + ")").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                Component unidentifiedAveragePriceComponent = Component.literal("Unidentified Avg: " + numberFormat.format(unidentifiedAveragePrice) + EmeraldUnits.EMERALD.getSymbol()).withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)).append(Component.literal(" (" + emeraldModel.getFormattedString(unidentifiedAveragePrice, false) + ")").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

                tooltips.add(highestPriceComponent);
                tooltips.add(lowestPriceComponent);
                tooltips.add(medianPriceComponent);
                tooltips.add(unidentifiedAveragePriceComponent);
            }
        }
    }

    private static boolean findComponentByText(List<Component> tooltips, String searchText) {
        for (Component component : tooltips) {
            String text = component.getString();
            if (text.contains(searchText)) {
                return true;
            }
        }

        return false; // If no matching component is found
    }
}