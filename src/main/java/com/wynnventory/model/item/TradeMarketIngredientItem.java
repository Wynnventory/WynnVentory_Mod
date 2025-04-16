package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.ModInfo;
import com.wynnventory.util.TradeMarketPriceParser;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class TradeMarketIngredientItem extends TradeMarketItem {
    private final SimplifiedIngredientItem item;

    public TradeMarketIngredientItem(IngredientItem ingredientItem, int listingPrice, int amount) {
        super(listingPrice, amount, McUtils.playerName(), ModInfo.VERSION);
        this.item = new SimplifiedIngredientItem(ingredientItem);
    }

    public static TradeMarketIngredientItem from(ItemStack itemStack) {
        return com.wynntils.core.components.Models.Item
                .asWynnItem(itemStack, IngredientItem.class)
                .flatMap(ingredient -> {
                    TradeMarketPriceInfo priceInfo = TradeMarketPriceParser.calculateItemPriceInfo(itemStack);
                    if (priceInfo != TradeMarketPriceInfo.EMPTY) {
                        return Optional.of(new TradeMarketIngredientItem(ingredient, priceInfo.price(), priceInfo.amount()));
                    }
                    return Optional.empty();
                })
                .orElse(null);
    }

    public SimplifiedIngredientItem getItem() {
        return item;
    }

    @JsonProperty("hash_code")
    public int getHashCode() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TradeMarketIngredientItem other) {
            return this.listingPrice == other.listingPrice &&
                    amount == other.amount &&
                    Objects.equals(item, other.item) &&
                    Objects.equals(playerName, other.playerName) &&
                    Objects.equals(modVersion, other.modVersion);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, listingPrice, amount, playerName, modVersion);
    }
}