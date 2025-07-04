package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wynnventory.enums.Region;
import com.wynnventory.enums.RegionType;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Lootpool extends CrowdSourcedData {
    private final Set<LootpoolItem> items = new HashSet<>();
    private String region;
    private RegionType type;

    public Lootpool() {
        super();
    }

    public Lootpool(Region region) {
        super();
        this.region = region.getShortName();
        this.type   = region.getRegionType();
    }

    public void addItem(LootpoolItem item) {
        items.add(item);
    }

    public void addItems(List<LootpoolItem> items) {
        this.items.addAll(items);
    }

    public void removeItem(LootpoolItem item) {
        items.remove(item);
    }

    public void removeItems(List<LootpoolItem> items) {
        items.forEach(this.items::remove);
    }

    public Set<LootpoolItem> getItems() {
        return items;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public RegionType getType() {
        return type;
    }

    public void setType(RegionType type) {
        this.type = type;
    }

    @JsonIgnore
    public List<LootpoolItem> getMythics() {
        return this.items.stream()
                .filter(i ->
                        "Mythic".equalsIgnoreCase(i.getRarity())
                )
                .sorted(Comparator.comparing(LootpoolItem::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

    }

    @JsonIgnore
    public List<LootpoolItem> getMythicAspects() {
        return this.items.stream()
                .filter(i ->
                        "Mythic".equalsIgnoreCase(i.getRarity()) &&
                        "AspectItem".equalsIgnoreCase(i.getItemType())
                )
                .sorted(Comparator.comparing(LootpoolItem::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Shared sorter: bucket → rarity desc → type/name α
     */
    private List<LootpoolItem> sortByBucket(ToIntFunction<LootpoolItem> bucketFn) {
        return items.stream()
                .sorted(Comparator
                        .comparingInt(bucketFn)
                        .thenComparingInt(i -> -getRarityRank(i))
                        .thenComparing(LootpoolItem::getType, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(LootpoolItem::getName, String.CASE_INSENSITIVE_ORDER)
                )
                .toList();
    }

    @JsonIgnore
    public List<LootpoolItem> getLootrunSortedItems() {
        return sortByBucket(this::getLootrunBuckets);
    }

    @JsonIgnore
    public List<LootpoolItem> getRaidSortedItems() {
        return sortByBucket(this::getRaidBuckets);
    }

    private int getLootrunBuckets(LootpoolItem i) {
        if (i.getShinyStat().isPresent())                return 1;
        if ("TomeItem".equalsIgnoreCase(i.getItemType()))return 7;
        if ("Mythic".equalsIgnoreCase(i.getRarity()))    return 2;
        if ("Fabled".equalsIgnoreCase(i.getRarity()))    return 3;
        if ("Legendary".equalsIgnoreCase(i.getRarity())) return 4;
        if ("Rare".equalsIgnoreCase(i.getRarity()))      return 5;
        if ("Unique".equalsIgnoreCase(i.getRarity()))    return 6;
        if ("Common".equalsIgnoreCase(i.getRarity()))    return 8;
        return 9; // anything else
    }

    private int getRaidBuckets(LootpoolItem i) {
        if ("AspectItem".equalsIgnoreCase(i.getItemType())) {
            return 1;
        }
        if ("TomeItem".equalsIgnoreCase(i.getItemType())) {
            return 2;
        }
        if ("GearItem".equalsIgnoreCase(i.getItemType())) {
            return 3;
        }
        return 4;
    }

    private int getRarityRank(LootpoolItem i) {
        String r = i.getRarity();
        if (r == null) return 0;
        return switch (r.trim().toLowerCase()) {
            case "mythic"    -> 6;
            case "fabled"    -> 5;
            case "legendary" -> 4;
            case "rare"      -> 3;
            case "unique"    -> 2;
            case "common"    -> 1;
            default          -> 0;
        };
    }
}
