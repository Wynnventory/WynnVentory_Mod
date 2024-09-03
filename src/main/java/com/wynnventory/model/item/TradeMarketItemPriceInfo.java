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

    // Default constructor
    public TradeMarketItemPriceInfo() {}

    // Getters and Setters

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Double getAverage80Price() {
        return average80Price;
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

    public Double getUnidentifiedAveragePrice() {
        return unidentifiedAveragePrice;
    }

    public void setUnidentifiedAveragePrice(Double unidentifiedAveragePrice) {
        this.unidentifiedAveragePrice = unidentifiedAveragePrice;
    }

    public Double getUnidentifiedAverage80Price() {
        return unidentifiedAverage80Price;
    }

    public void setUnidentifiedAverage80Price(Double unidentifiedAverage80Price) {
        this.unidentifiedAverage80Price = unidentifiedAverage80Price;
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
