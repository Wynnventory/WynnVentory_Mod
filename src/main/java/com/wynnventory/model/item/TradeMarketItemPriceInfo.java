package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeMarketItemPriceInfo {
    @JsonProperty("average_price")
    private Double averagePrice;

    @JsonProperty("average_mid_80_percent_price")
    private Double average80Price;

    @JsonProperty("highest_price")
    private int highestPrice;

    @JsonProperty("lowest_price")
    private int lowestPrice;

    @JsonProperty("unidentified_average_price")
    private Double unidentifiedAveragePrice;

    @JsonProperty("unidentified_average_mid_80_percent_price")
    private Double unidentifiedAverage80Price;

    // Getters and Setters
    public int getAveragePrice() {
        if (averagePrice == null) {
            return 0;
        }

        return averagePrice.intValue();
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public int getAverage80Price() {
        if (average80Price == null) {
            return 0;
        }

        return average80Price.intValue();
    }

    public void setAverage80Price(Double average80Price) {
        this.average80Price = average80Price;
    }

    public int getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(int highestPrice) {
        this.highestPrice = highestPrice;
    }

    public int getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(int lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public int getUnidentifiedAveragePrice() {
        if(unidentifiedAveragePrice == null){
            return 0;
        }

        return unidentifiedAveragePrice.intValue();
    }

    public void setUnidentifiedAveragePrice(Double unidentifiedAveragePrice) {
        this.unidentifiedAveragePrice = unidentifiedAveragePrice;
    }

    public int getUnidentifiedAverage80Price() {
        if(unidentifiedAverage80Price == null) {
            return 0;
        }

        return unidentifiedAverage80Price.intValue();
    }

    public void setUnidentifiedAverage80Price(Double unidentifiedAverage80Price) {
        this.unidentifiedAverage80Price = unidentifiedAverage80Price;
    }

    public boolean isEmpty() {
        return averagePrice == null && average80Price == null && highestPrice == 0 && lowestPrice == 0 && unidentifiedAveragePrice == null && unidentifiedAverage80Price == null;
    }

    @Override
    public String toString() {
        return "ItemPrice{" +
                "averagePrice=" + averagePrice +
                ", highestPrice=" + highestPrice +
                ", lowestPrice=" + lowestPrice +
                ", unidentifiedAveragePrice=" + unidentifiedAveragePrice +
                '}';
    }
}
