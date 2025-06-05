package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.items.items.game.MaterialItem;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.ModInfo;
import com.wynnventory.util.TradeMarketPriceParser;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeMarketItem extends CrowdSourcedData {
    private final SimplifiedItem item;
    private final int listingPrice;
    private final int amount;

    protected TradeMarketItem(SimplifiedItem item, int listingPrice, int amount) {
        super();
        this.item = item;
        this.listingPrice = listingPrice;
        this.amount = amount;
    }

    public static TradeMarketItem from(ItemStack itemStack) {
        Optional<WynnItem> wynnItemOptional = Models.Item.getWynnItem(itemStack);

        if (wynnItemOptional.isEmpty()) return null;

        TradeMarketPriceInfo priceInfo = TradeMarketPriceParser.calculateItemPriceInfo(itemStack);
        WynnItem wynnItem = wynnItemOptional.get();

        if (wynnItem instanceof GearItem gearItem) {
            return new TradeMarketItem(new SimplifiedGearItem(gearItem), priceInfo.price(), priceInfo.amount());
        }

        if (wynnItem instanceof IngredientItem ingredientItem) {
            return new TradeMarketItem(new SimplifiedCraftingItem(ingredientItem), priceInfo.price(), priceInfo.amount());
        }

        if (wynnItem instanceof MaterialItem materialItem) {
            return new TradeMarketItem(new SimplifiedCraftingItem(materialItem), priceInfo.price(), priceInfo.amount());
        }

        return null;
    }

    public int getListingPrice() {
        return listingPrice;
    }

    public int getAmount() {
        return amount;
    }

    public SimplifiedItem getItem() {
        return item;
    }

    @JsonProperty("hash_code")
    public int getHashCode() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TradeMarketItem other) {
            return listingPrice == other.listingPrice &&
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
