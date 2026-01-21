package com.wynnventory.model.item.trademarket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.wynnventory.data.TimestampedObject;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.Icon;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleItemWithPriceInfo extends TimestampedObject {
    @JsonUnwrapped
    private final SimpleItem item = new SimpleItem();

    @JsonUnwrapped
    private final PriceInfo priceInfo = new PriceInfo();

    private boolean shiny;
    private Integer tier;

    public SimpleItem getItem() {
        return item;
    }

    public PriceInfo getPriceInfo() {
        return priceInfo;
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    @JsonProperty("name")
    public String getName() {
        return item.getName();
    }

    @JsonProperty("name")
    public void setName(String name) {
        item.setName(name);
    }

    @JsonProperty("rarity")
    public String getRarity() {
        return item.getRarity();
    }

    @JsonProperty("rarity")
    public void setRarity(String rarity) {
        item.setRarity(rarity);
    }

    @JsonProperty("item_type")
    public String getItemType() {
        return item.getItemType();
    }

    @JsonProperty("item_type")
    public void setItemType(String itemType) {
        item.setItemType(itemType);
    }

    @JsonProperty("type")
    public String getType() {
        return item.getType();
    }

    @JsonProperty("type")
    public void setType(String type) {
        item.setType(type);
    }

    @JsonProperty("icon")
    public Icon getIcon() {
        return item.getIcon();
    }

    @JsonProperty("icon")
    public void setIcon(Icon icon) {
        item.setIcon(icon);
    }

    @JsonProperty("amount")
    public int getAmount() {
        return item.getAmount();
    }

    @JsonProperty("amount")
    public void setAmount(int amount) {
        item.setAmount(amount);
    }

    @JsonProperty("average_mid_80_percent_price")
    public Double getAverageMid80PercentPrice() {
        return priceInfo.getAverageMid80PercentPrice();
    }

    @JsonProperty("average_mid_80_percent_price")
    public void setAverageMid80PercentPrice(Double value) {
        priceInfo.setAverageMid80PercentPrice(value);
    }

    @JsonProperty("average_price")
    public Double getAveragePrice() {
        return priceInfo.getAveragePrice();
    }

    @JsonProperty("average_price")
    public void setAveragePrice(Double value) {
        priceInfo.setAveragePrice(value);
    }

    @JsonProperty("highest_price")
    public Integer getHighestPrice() {
        return priceInfo.getHighestPrice();
    }

    @JsonProperty("highest_price")
    public void setHighestPrice(Integer value) {
        priceInfo.setHighestPrice(value);
    }

    @JsonProperty("lowest_price")
    public Integer getLowestPrice() {
        return priceInfo.getLowestPrice();
    }

    @JsonProperty("lowest_price")
    public void setLowestPrice(Integer value) {
        priceInfo.setLowestPrice(value);
    }

    @JsonProperty("total_count")
    public Integer getTotalCount() {
        return priceInfo.getTotalCount();
    }

    @JsonProperty("total_count")
    public void setTotalCount(Integer value) {
        priceInfo.setTotalCount(value);
    }

    @JsonProperty("unidentified_average_mid_80_percent_price")
    public Double getUnidentifiedAverageMid80PercentPrice() {
        return priceInfo.getUnidentifiedAverageMid80PercentPrice();
    }

    @JsonProperty("unidentified_average_mid_80_percent_price")
    public void setUnidentifiedAverageMid80PercentPrice(Double value) {
        priceInfo.setUnidentifiedAverageMid80PercentPrice(value);
    }

    @JsonProperty("unidentified_average_price")
    public Double getUnidentifiedAveragePrice() {
        return priceInfo.getUnidentifiedAveragePrice();
    }

    @JsonProperty("unidentified_average_price")
    public void setUnidentifiedAveragePrice(Double value) {
        priceInfo.setUnidentifiedAveragePrice(value);
    }

    @JsonProperty("unidentified_count")
    public Integer getUnidentifiedCount() {
        return priceInfo.getUnidentifiedCount();
    }

    @JsonProperty("unidentified_count")
    public void setUnidentifiedCount(Integer value) {
        priceInfo.setUnidentifiedCount(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimpleItemWithPriceInfo other) {
            return shiny == other.shiny &&
                    Objects.equals(item, other.item) &&
                    Objects.equals(priceInfo, other.priceInfo) &&
                    Objects.equals(tier, other.tier);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, priceInfo, shiny, tier);
    }
}
