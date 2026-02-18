package com.wynnventory.model.reward;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wynntils.models.gear.type.GearTier;
import com.wynnventory.model.item.ModInfoProvider;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleItemType;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RewardPoolDocument extends ModInfoProvider {
    private List<SimpleItem> items = new ArrayList<>();
    private RewardPool rewardPool;

    public RewardPoolDocument() {
        super();
    }

    public RewardPoolDocument(List<SimpleItem> items, RewardPool rewardPool) {
        super();
        this.items = items;
        this.rewardPool = rewardPool;
    }

    public List<SimpleItem> getItems() {
        return items;
    }

    @JsonIgnore
    public List<SimpleItem> getMythicAspects() {
        return items.stream()
                .filter(item ->
                        item.getItemTypeEnum() == SimpleItemType.ASPECT && item.getRarityEnum() == GearTier.MYTHIC)
                .toList();
    }

    public String getRegion() {
        return rewardPool.getFullName();
    }

    public String getType() {
        return rewardPool.getType().name();
    }

    public void setItems(List<SimpleItem> items) {
        this.items = items;
    }

    public void setRewardPool(RewardPool rewardPool) {
        this.rewardPool = rewardPool;
    }

    @JsonAlias("region")
    public void setRegion(String region) {
        this.rewardPool = RewardPool.fromFullName(region);
    }

    @JsonIgnore
    public RewardPool getRewardPool() {
        return rewardPool;
    }
}
