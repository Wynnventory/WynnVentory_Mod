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
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    private static final EmeraldModel EMERALD_MODEL = new EmeraldModel();
    private static final String TITLE_TEXT = "Trade Market Price Info";

    private static GearItem lastHoveredItem;
    private static TradeMarketItemPriceInfo lastHoveredItemPriceInfo;

    public static void registerTooltips() {
        ItemTooltipCallback.EVENT.register(TooltipHandler::onTooltip);
    }

    private static void onTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> tooltips) {
        if (containsComponent(tooltips, TITLE_TEXT)) return;

        Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(itemStack, GearItem.class);
        gearItemOptional.ifPresent(gearItem -> {
            if (!gearItem.equals(lastHoveredItem)) {
                lastHoveredItem = gearItem;
                lastHoveredItemPriceInfo = API.fetchItemPrices(itemStack);
            }

            addPriceInfoToTooltip(tooltips, lastHoveredItemPriceInfo);
        });
    }

    private static void addPriceInfoToTooltip(List<Component> tooltips, TradeMarketItemPriceInfo priceInfo) {
        addTooltipSpacer(tooltips);
        addTooltipTitle(tooltips, TITLE_TEXT);

        if (priceInfo == null) {
            addTooltipLine(tooltips, "No price data available yet!", ChatFormatting.RED);
        } else {
            addFormattedPrice(tooltips, "Max: ", priceInfo.getHighestPrice());
            addFormattedPrice(tooltips, "Min: ", priceInfo.getLowestPrice());
            addFormattedPrice(tooltips, "Avg: ", priceInfo.getAveragePrice());

            if (priceInfo.getUnidentifiedAveragePrice() != null) {
                addFormattedPrice(tooltips, "Unidentified Avg: ", priceInfo.getUnidentifiedAveragePrice().intValue());
            }
        }
    }

    private static void addFormattedPrice(List<Component> tooltips, String label, int price) {
        if (price > 0) {
            String formattedPrice = NUMBER_FORMAT.format(price) + EmeraldUnits.EMERALD.getSymbol();
            String formattedEmeralds = EMERALD_MODEL.getFormattedString(price, false);
            Component component = Component.literal(label + formattedPrice)
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
                    .append(Component.literal(" (" + formattedEmeralds + ")")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            tooltips.add(component);
        }
    }

    private static void addTooltipSpacer(List<Component> tooltips) {
        addTooltipTitle(tooltips, "");
    }

    private static void addTooltipTitle(List<Component> tooltips, String title) {
        tooltips.add(Component.literal(title).withStyle(ChatFormatting.GOLD));
    }

    private static void addTooltipLine(List<Component> tooltips, String text, ChatFormatting color) {
        tooltips.add(Component.literal(text).withStyle(Style.EMPTY.withColor(color)));
    }

    private static boolean containsComponent(List<Component> tooltips, String searchText) {
        return tooltips.stream().anyMatch(component -> component.getString().contains(searchText));
    }
}