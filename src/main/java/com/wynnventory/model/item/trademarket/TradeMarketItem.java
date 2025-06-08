package com.wynnventory.model.item.trademarket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.*;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynnventory.model.item.CrowdSourcedData;
import com.wynnventory.model.item.simplified.SimplifiedGearItem;
import com.wynnventory.model.item.simplified.SimplifiedItem;
import com.wynnventory.model.item.simplified.SimplifiedTieredItem;
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

        else if (wynnItem instanceof IngredientItem ingredientItem) {
            return new TradeMarketItem(new SimplifiedTieredItem(ingredientItem), priceInfo.price(), priceInfo.amount());
        }

        else if (wynnItem instanceof MaterialItem materialItem) {
            return new TradeMarketItem(new SimplifiedTieredItem(materialItem), priceInfo.price(), priceInfo.amount());
        }

        else if (wynnItem instanceof PowderItem powderItem) {
            return new TradeMarketItem(new SimplifiedTieredItem(powderItem), priceInfo.price(), priceInfo.amount());
        }

        else if (wynnItem instanceof AmplifierItem amplifierItem) {
            return new TradeMarketItem(new SimplifiedTieredItem(amplifierItem), priceInfo.price(), priceInfo.amount());
        }

        else if (wynnItem instanceof InsulatorItem insulatorItem) {
            return new TradeMarketItem(new SimplifiedItem(insulatorItem), priceInfo.price(), priceInfo.amount());
        }

        else if (wynnItem instanceof SimulatorItem simulatorItem) {
            return new TradeMarketItem(new SimplifiedItem(simulatorItem), priceInfo.price(), priceInfo.amount());
        }

        else if (wynnItem instanceof HorseItem horseItem) {
            return new TradeMarketItem(new SimplifiedTieredItem(horseItem), priceInfo.price(), priceInfo.amount());
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
