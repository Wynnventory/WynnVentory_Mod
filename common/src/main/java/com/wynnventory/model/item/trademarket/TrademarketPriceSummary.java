package com.wynnventory.model.item.trademarket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrademarketPriceSummary {
    private Double averageMid80PercentPrice;
    private Double averagePrice;
    private Integer highestPrice;
    private Integer lowestPrice;
    private Double unidentifiedAverageMid80PercentPrice;
    private Double unidentifiedAveragePrice;

    public Double getAverageMid80PercentPrice() {
        return averageMid80PercentPrice;
    }

    public void setAverageMid80PercentPrice(Double averageMid80PercentPrice) {
        this.averageMid80PercentPrice = averageMid80PercentPrice;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Integer getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(Integer highestPrice) {
        this.highestPrice = highestPrice;
    }

    public Integer getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(Integer lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public Double getUnidentifiedAverageMid80PercentPrice() {
        return unidentifiedAverageMid80PercentPrice;
    }

    public void setUnidentifiedAverageMid80PercentPrice(Double unidentifiedAverageMid80PercentPrice) {
        this.unidentifiedAverageMid80PercentPrice = unidentifiedAverageMid80PercentPrice;
    }

    public Double getUnidentifiedAveragePrice() {
        return unidentifiedAveragePrice;
    }

    public void setUnidentifiedAveragePrice(Double unidentifiedAveragePrice) {
        this.unidentifiedAveragePrice = unidentifiedAveragePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TrademarketPriceSummary other) {
            return Objects.equals(averageMid80PercentPrice, other.averageMid80PercentPrice)
                    && Objects.equals(averagePrice, other.averagePrice)
                    && Objects.equals(highestPrice, other.highestPrice)
                    && Objects.equals(lowestPrice, other.lowestPrice)
                    && Objects.equals(unidentifiedAverageMid80PercentPrice, other.unidentifiedAverageMid80PercentPrice)
                    && Objects.equals(unidentifiedAveragePrice, other.unidentifiedAveragePrice);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                averageMid80PercentPrice,
                averagePrice,
                highestPrice,
                lowestPrice,
                unidentifiedAverageMid80PercentPrice,
                unidentifiedAveragePrice);
    }

    @Override
    public String toString() {
        return "CalculatedPriceInfo{" + "averageMid80PercentPrice="
                + averageMid80PercentPrice + ", averagePrice="
                + averagePrice + ", highestPrice="
                + highestPrice + ", lowestPrice="
                + lowestPrice + ", unidentifiedAverageMid80PercentPrice="
                + unidentifiedAverageMid80PercentPrice + ", unidentifiedAveragePrice="
                + unidentifiedAveragePrice + '}';
    }
}
