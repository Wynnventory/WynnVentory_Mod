package com.wynnventory.model.item.trademarket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceInfo {
    private Double averageMid80PercentPrice;
    private Double averagePrice;
    private Integer highestPrice;
    private Integer lowestPrice;
    private Integer totalCount;
    private Double unidentifiedAverageMid80PercentPrice;
    private Double unidentifiedAveragePrice;
    private Integer unidentifiedCount;

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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
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

    public Integer getUnidentifiedCount() {
        return unidentifiedCount;
    }

    public void setUnidentifiedCount(Integer unidentifiedCount) {
        this.unidentifiedCount = unidentifiedCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof PriceInfo other) {
            return Objects.equals(averageMid80PercentPrice, other.averageMid80PercentPrice) &&
                    Objects.equals(averagePrice, other.averagePrice) &&
                    Objects.equals(highestPrice, other.highestPrice) &&
                    Objects.equals(lowestPrice, other.lowestPrice) &&
                    Objects.equals(totalCount, other.totalCount) &&
                    Objects.equals(unidentifiedAverageMid80PercentPrice, other.unidentifiedAverageMid80PercentPrice) &&
                    Objects.equals(unidentifiedAveragePrice, other.unidentifiedAveragePrice) &&
                    Objects.equals(unidentifiedCount, other.unidentifiedCount);
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
                totalCount,
                unidentifiedAverageMid80PercentPrice,
                unidentifiedAveragePrice,
                unidentifiedCount
        );
    }
}
