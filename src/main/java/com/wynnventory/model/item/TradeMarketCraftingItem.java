package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.items.items.game.MaterialItem;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.ModInfo;
import com.wynnventory.util.TradeMarketPriceParser;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class TradeMarketCraftingItem extends TradeMarketItem {
    private final SimplifiedCraftingItem item;

    public TradeMarketCraftingItem(IngredientItem ingredientItem, int listingPrice, int amount) {
        super(listingPrice, amount, McUtils.playerName(), ModInfo.VERSION);
        this.item = new SimplifiedCraftingItem(ingredientItem);
    }

    public TradeMarketCraftingItem(MaterialItem materialItem, int listingPrice, int amount) {
        super(listingPrice, amount, McUtils.playerName(), ModInfo.VERSION);
        this.item = new SimplifiedCraftingItem(materialItem);
    }

    public static TradeMarketCraftingItem from(ItemStack itemStack) {
        Optional<WynnItem> wynnItemOptional = Models.Item.getWynnItem(itemStack);

        if (wynnItemOptional.isEmpty()) return null;

        TradeMarketPriceInfo priceInfo = TradeMarketPriceParser.calculateItemPriceInfo(itemStack);
        WynnItem wynnItem = wynnItemOptional.get();

        if (wynnItem instanceof IngredientItem ingredientItem) {
            return new TradeMarketCraftingItem(ingredientItem, priceInfo.price(), priceInfo.amount());
        }

        if (wynnItem instanceof MaterialItem materialItem) {
            return new TradeMarketCraftingItem(materialItem, priceInfo.price(), priceInfo.amount());
        }

        return null;
    }

    public SimplifiedCraftingItem getItem() {
        return item;
    }

    @JsonProperty("hash_code")
    public int getHashCode() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TradeMarketCraftingItem other) {
            return this.listingPrice == other.listingPrice &&
                    amount == other.amount &&
                    Objects.equals(item, other.item);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, listingPrice, amount);
    }
}