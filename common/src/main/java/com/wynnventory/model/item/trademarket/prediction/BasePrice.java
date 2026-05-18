package com.wynnventory.model.item.trademarket.prediction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BasePrice {
    private Integer p25;
    private Integer median;

    public Integer getP25() {
        return p25;
    }

    public void setP25(Integer p25) {
        this.p25 = p25;
    }

    public Integer getMedian() {
        return median;
    }

    public void setMedian(Integer median) {
        this.median = median;
    }
}
