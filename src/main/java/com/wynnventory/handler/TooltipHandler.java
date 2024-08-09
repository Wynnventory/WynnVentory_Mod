package com.wynnventory.handler;

import com.wynntils.core.components.Models;
import com.wynntils.models.items.items.game.GearItem;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.model.item.TradeMarketItem;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TooltipHandler {
    private static final WynnventoryAPI API = new WynnventoryAPI();

    public static void registerTooltips() {
        // Register the ItemTooltipCallback event
        ItemTooltipCallback.EVENT.register(TooltipHandler::onTooltip);
    }

    private static void onTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> tooltips) {
        final String tradeMarketTitleText = "Trademarket Price Info";
        final Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(itemStack, GearItem.class);

        if(findComponentByText(tooltips, tradeMarketTitleText)) {
            return;
        }

        if(gearItemOptional.isPresent()) {
            TradeMarketItemPriceInfo priceInfo = API.fetchItemPriceForItem(itemStack);

            Component spacer = Component.literal("").withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            Component priceInfoTitle = Component.literal("Trademarket Price Info").withStyle(ChatFormatting.GOLD);
            tooltips.add(spacer);
            tooltips.add(priceInfoTitle);

            if(priceInfo == null) {
                Component noPriceData = Component.literal("No price data available yet!").withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
                tooltips.add(noPriceData);
                tooltips.add(spacer);
            } else {
                Component highestPrice = Component.literal("Highest: " + priceInfo.getHighestPrice()).withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
                Component medianPrice = Component.literal("Median: " + priceInfo.getAveragePrice()).withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
                Component lowestPrice = Component.literal("Lowest: "+ priceInfo.getLowestPrice()).withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));

                tooltips.add(highestPrice);
                tooltips.add(medianPrice);
                tooltips.add(lowestPrice);
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