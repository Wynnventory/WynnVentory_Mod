package com.wynnventory.model.item.trademarket.prediction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceContribution {
    private String apiName;
    private Integer rollPercentage;
    private Double weight;
    private Double priceMultiplier;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Integer getRollPercentage() {
        return rollPercentage;
    }

    public void setRollPercentage(Integer rollPercentage) {
        this.rollPercentage = rollPercentage;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(Double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }
}
