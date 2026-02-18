package com.wynnventory.model.item.trademarket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynnventory.model.item.Icon;
import com.wynnventory.model.item.TimestampedObject;
import com.wynnventory.model.item.simple.SimpleItem;
import java.time.Duration;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrademarketItemSummary extends TimestampedObject {
    private final SimpleItem item = new SimpleItem();
    private final TrademarketPriceSummary calculatedPriceInfo = new TrademarketPriceSummary();
    private static final Duration DATA_LIFESPAN = Duration.ofMinutes(5);

    private boolean shiny;
    private Integer tier;

    public SimpleItem getItem() {
        return item;
    }

    public TrademarketPriceSummary getPriceInfo() {
        return calculatedPriceInfo;
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
        return calculatedPriceInfo.getAverageMid80PercentPrice();
    }

    @JsonProperty("average_mid_80_percent_price")
    public void setAverageMid80PercentPrice(Double value) {
        calculatedPriceInfo.setAverageMid80PercentPrice(value);
    }

    @JsonProperty("average_price")
    public Double getAveragePrice() {
        return calculatedPriceInfo.getAveragePrice();
    }

    @JsonProperty("average_price")
    public void setAveragePrice(Double value) {
        calculatedPriceInfo.setAveragePrice(value);
    }

    @JsonProperty("highest_price")
    public Integer getHighestPrice() {
        return calculatedPriceInfo.getHighestPrice();
    }

    @JsonProperty("highest_price")
    public void setHighestPrice(Integer value) {
        calculatedPriceInfo.setHighestPrice(value);
    }

    @JsonProperty("lowest_price")
    public Integer getLowestPrice() {
        return calculatedPriceInfo.getLowestPrice();
    }

    @JsonProperty("lowest_price")
    public void setLowestPrice(Integer value) {
        calculatedPriceInfo.setLowestPrice(value);
    }

    @JsonProperty("unidentified_average_mid_80_percent_price")
    public Double getUnidentifiedAverageMid80PercentPrice() {
        return calculatedPriceInfo.getUnidentifiedAverageMid80PercentPrice();
    }

    @JsonProperty("unidentified_average_mid_80_percent_price")
    public void setUnidentifiedAverageMid80PercentPrice(Double value) {
        calculatedPriceInfo.setUnidentifiedAverageMid80PercentPrice(value);
    }

    @JsonProperty("unidentified_average_price")
    public Double getUnidentifiedAveragePrice() {
        return calculatedPriceInfo.getUnidentifiedAveragePrice();
    }

    @JsonProperty("unidentified_average_price")
    public void setUnidentifiedAveragePrice(Double value) {
        calculatedPriceInfo.setUnidentifiedAveragePrice(value);
    }

    public boolean isEmpty() {
        return item.getName() == null || item.getName().isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TrademarketItemSummary other) {
            return shiny == other.shiny
                    && Objects.equals(item, other.item)
                    && Objects.equals(calculatedPriceInfo, other.calculatedPriceInfo)
                    && Objects.equals(tier, other.tier);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, calculatedPriceInfo, shiny, tier);
    }

    @Override
    public String toString() {
        return "CalculatedPriceItem{" + "item="
                + item + ", calculatedPriceInfo="
                + calculatedPriceInfo + ", shiny="
                + shiny + ", tier="
                + tier + ", timestamp="
                + timestamp + '}';
    }
}
