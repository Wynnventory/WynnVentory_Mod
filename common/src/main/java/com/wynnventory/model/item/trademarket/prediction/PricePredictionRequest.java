package com.wynnventory.model.item.trademarket.prediction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wynnventory.model.item.ItemStat;
import com.wynnventory.model.item.simple.SimpleGearItem;
import java.util.LinkedHashMap;
import java.util.Map;

public class PricePredictionRequest {
    private String name;
    private Map<String, Double> statRolls = new LinkedHashMap<>();
    private int rerollCount;

    @JsonProperty("isShiny")
    private boolean shiny;

    public PricePredictionRequest() {}

    public PricePredictionRequest(String name, Map<String, Double> statRolls, int rerollCount, boolean shiny) {
        this.name = name;
        setStatRolls(statRolls);
        this.rerollCount = rerollCount;
        this.shiny = shiny;
    }

    public static PricePredictionRequest from(SimpleGearItem item) {
        Map<String, Double> statRolls = new LinkedHashMap<>();

        for (ItemStat stat : item.getActualStatsWithPercentage()) {
            double rollPercentage = stat.getRollPercentage();
            if (Double.isFinite(rollPercentage)) {
                statRolls.put(stat.getApiName(), rollPercentage);
            }
        }

        return new PricePredictionRequest(item.getName(), statRolls, item.getRerollCount(), item.isShiny());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Double> getStatRolls() {
        return statRolls;
    }

    public void setStatRolls(Map<String, Double> statRolls) {
        this.statRolls = new LinkedHashMap<>();
        if (statRolls == null) return;

        for (Map.Entry<String, Double> entry : statRolls.entrySet()) {
            Double value = entry.getValue();
            if (entry.getKey() != null && value != null && Double.isFinite(value)) {
                this.statRolls.put(entry.getKey(), value);
            }
        }
    }

    public int getRerollCount() {
        return rerollCount;
    }

    public void setRerollCount(int rerollCount) {
        this.rerollCount = rerollCount;
    }

    @JsonProperty("isShiny")
    public boolean isShiny() {
        return shiny;
    }

    @JsonProperty("isShiny")
    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }
}
