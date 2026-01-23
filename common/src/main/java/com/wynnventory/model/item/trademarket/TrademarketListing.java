package com.wynnventory.model.item.trademarket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynntils.models.trademarket.type.TradeMarketPriceInfo;
import com.wynnventory.model.item.ModInfoProvider;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class TrademarketListing extends ModInfoProvider {
    private SimpleItem item;
    private int price;
    private int quantity;

    protected TrademarketListing(SimpleItem item, int price, int quantity) {
        this.item = item;
        this.price = price;
        this.quantity = quantity;
    }

    public static TrademarketListing from(ItemStack itemStack) {
        SimpleItem item = ItemStackUtils.toSimpleItem(itemStack);

        if(item == null) return null;

        TradeMarketPriceInfo priceInfo = ItemStackUtils.calculateItemPriceInfo(itemStack);

        if (priceInfo == null) return null;

        return new TrademarketListing(item, priceInfo.price(), priceInfo.amount());
    }

    @JsonProperty("listingPrice")
    public int getListingPrice() {
        return price;
    }

    @JsonProperty("amount")
    public int getQuantity() {
        return quantity;
    }

    public SimpleItem getItem() {
        return item;
    }

    @JsonProperty("hash_code")
    public int getHashCode() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TrademarketListing other) {
            return price == other.price &&
                    quantity == other.quantity &&
                    Objects.equals(item, other.item);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, price, quantity);
    }
}

