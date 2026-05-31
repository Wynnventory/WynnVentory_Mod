package com.wynnventory.model.item.trademarket.prediction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PricePredictionResponse {
    private String name;
    private Integer tier;
    private Integer estimatedPrice;
    private Integer baselinePrice;
    private Integer modelTier;
    private Double confidence;
    private Integer sampleSize;
    private List<PriceContribution> contributions;
    private Integer rerollAdjustment;
    private Integer shinyAdjustment;
    private BasePrice basePrice;
    private CommodityPrice commodityPrice;
    private String modelUpdatedAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public Integer getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Integer estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public Integer getBaselinePrice() {
        return baselinePrice;
    }

    public void setBaselinePrice(Integer baselinePrice) {
        this.baselinePrice = baselinePrice;
    }

    public Integer getModelTier() {
        return modelTier;
    }

    public void setModelTier(Integer modelTier) {
        this.modelTier = modelTier;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Integer getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    public List<PriceContribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<PriceContribution> contributions) {
        this.contributions = contributions;
    }

    public Integer getRerollAdjustment() {
        return rerollAdjustment;
    }

    public void setRerollAdjustment(Integer rerollAdjustment) {
        this.rerollAdjustment = rerollAdjustment;
    }

    public Integer getShinyAdjustment() {
        return shinyAdjustment;
    }

    public void setShinyAdjustment(Integer shinyAdjustment) {
        this.shinyAdjustment = shinyAdjustment;
    }

    public BasePrice getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BasePrice basePrice) {
        this.basePrice = basePrice;
    }

    public CommodityPrice getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(CommodityPrice commodityPrice) {
        this.commodityPrice = commodityPrice;
    }

    public String getModelUpdatedAt() {
        return modelUpdatedAt;
    }

    public void setModelUpdatedAt(String modelUpdatedAt) {
        this.modelUpdatedAt = modelUpdatedAt;
    }
}
