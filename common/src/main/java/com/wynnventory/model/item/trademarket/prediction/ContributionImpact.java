package com.wynnventory.model.item.trademarket.prediction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContributionImpact {
    private Double baselineRollPct;
    private Double factor;
    private Integer amountEmeralds;
    private String direction;

    public Double getBaselineRollPct() {
        return baselineRollPct;
    }

    public void setBaselineRollPct(Double baselineRollPct) {
        this.baselineRollPct = baselineRollPct;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Integer getAmountEmeralds() {
        return amountEmeralds;
    }

    public void setAmountEmeralds(Integer amountEmeralds) {
        this.amountEmeralds = amountEmeralds;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
