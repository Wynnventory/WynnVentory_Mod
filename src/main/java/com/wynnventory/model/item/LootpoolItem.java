package com.wynnventory.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wynntils.core.components.Models;
import com.wynntils.models.gear.GearModel;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.gear.type.GearInstance;
import com.wynntils.models.gear.type.GearRestrictions;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.*;
import com.wynntils.models.items.properties.GearTierItemProperty;
import com.wynntils.models.stats.type.ShinyStat;
import com.wynnventory.core.ModInfo;
import com.wynnventory.util.IconManager;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LootpoolItem {
    private String itemType;
    private int amount;
    private String name;
    private String rarity;
    private ShinyStat shinyStat;
    private String type;
    private Icon icon;
    protected static final List<Class<? extends WynnItem>> LOOT_CLASSES = Arrays.asList(
            GearItem.class,
            InsulatorItem.class,
            SimulatorItem.class,
            EmeraldItem.class,
            MiscItem.class,
            RuneItem.class,
            DungeonKeyItem.class,
            AspectItem.class,
            AmplifierItem.class,
            PowderItem.class,
            GearBoxItem.class,
            TomeItem.class
    );

    public LootpoolItem() {
    }

    public LootpoolItem(String itemType, int amount, String name, String rarity, ShinyStat shinyStat, String type) {
        this.itemType = itemType;
        this.amount = amount;
        this.name = name;
        this.rarity = rarity;
        this.shinyStat = shinyStat;
        this.type = type;
        this.icon = IconManager.getIcon(name);
    }

    public LootpoolItem(WynnItem wynnItem) {
        this.itemType = wynnItem.getClass().getSimpleName();
        this.name = Objects.requireNonNull(ItemStackUtils.getWynntilsOriginalName(wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY))).getLastPart().getComponent().getString();
        this.amount = ((ItemStack) wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount();
        this.name = name.replace("Unidentified ", "");
        this.type = wynnItem.getClass().getSimpleName().replace("Item", "");
        this.rarity = "Common";
        this.icon = IconManager.getIcon(name);

        if (wynnItem instanceof GearItem gearItem) {
            GearInstance gearInstance = new GearModel().parseInstance(gearItem.getItemInfo(), (ItemStack) wynnItem.getData().get(WynnItemData.ITEMSTACK_KEY));
            gearInstance.shinyStat().ifPresent(stat -> shinyStat = stat);

            this.name = ItemStackUtils.getGearItemName(gearItem);
            this.icon = IconManager.getIcon(name);
            this.rarity = gearItem.getGearTier().getName();
            this.type = gearItem.getGearType().name();
        } else if (wynnItem instanceof SimulatorItem || wynnItem instanceof InsulatorItem) {
            this.rarity = ((GearTierItemProperty) wynnItem).getGearTier().getName();
        } else if (wynnItem instanceof TomeItem tomeItem) {
            this.name = tomeItem.getName();
            this.rarity = tomeItem.getGearTier().getName();
            this.type = tomeItem.getItemInfo().type().name();
        } else if (wynnItem instanceof AspectItem aspectItem) {
            this.rarity = aspectItem.getGearTier().getName();

            String classReq = aspectItem.getRequiredClass().getName();
            if (classReq != null && !classReq.isEmpty()) {
                this.type = classReq + this.type;
            }
        } else if (wynnItem instanceof EmeraldItem emeraldItem) {
            this.type = emeraldItem.getUnit().name();
        } else if (wynnItem instanceof RuneItem runeItem) {
            this.type = runeItem.getType().name();
        } else if (wynnItem instanceof PowderItem powderItem) {
            this.name = powderItem.getName().replaceAll("[✹✦❉❋✤]", "").trim();
            this.type = powderItem.getPowderProfile().element().getName() + this.type;
        } else if (wynnItem instanceof AmplifierItem amplifierItem) {
            this.rarity = amplifierItem.getGearTier().getName();
            String[] nameParts = this.name.split(" ");

            if (nameParts.length > 1) {
                this.type = nameParts[0] + nameParts[1];
            }
        }
    }

    public static List<LootpoolItem> createLootpoolItemsFromWynnItem(List<WynnItem> wynnItems) {
        List<LootpoolItem> lootpoolItems = new ArrayList<>();

        for (WynnItem wynnItem : wynnItems) {
            lootpoolItems.addAll(createLootpoolItemFromWynnItem(wynnItem));
        }

        return lootpoolItems;
    }

    public static List<LootpoolItem> createLootpoolItemsFromItemStack(List<ItemStack> items) {
        List<WynnItem> wynnItems = new ArrayList<>();
        items.forEach(item -> Models.Item.getWynnItem(item).ifPresent(wynnItems::add));

        return createLootpoolItemsFromWynnItem(wynnItems);
    }

    public static List<LootpoolItem> createLootpoolItemFromWynnItem(WynnItem wynnItem) {
        List<LootpoolItem> lootpoolItems = new ArrayList<>();

        if (wynnItem instanceof GearBoxItem gearBoxItem) {
            List<GearInfo> possibleGear = Models.Gear.getPossibleGears(gearBoxItem);

            String name;
            String rarity;
            String type;
            for (GearInfo gearInfo : possibleGear) {
                if (gearInfo.requirements().quest().isPresent() || gearInfo.metaInfo().restrictions() == GearRestrictions.UNTRADABLE || gearInfo.metaInfo().restrictions() == GearRestrictions.QUEST_ITEM) {
                    continue;
                }

                name = gearInfo.name();
                rarity = gearInfo.tier().name();
                type = gearInfo.type().name();

                lootpoolItems.add(new LootpoolItem("GearItem", 1, name, rarity, null, type));
            }

            return lootpoolItems;
        } else if (wynnItem instanceof GearItem gearItem) {
            GearInfo itemInfo = gearItem.getItemInfo();
            if (itemInfo.requirements().quest().isPresent() || itemInfo.metaInfo().restrictions() == GearRestrictions.UNTRADABLE || itemInfo.metaInfo().restrictions() == GearRestrictions.QUEST_ITEM) {
                return lootpoolItems;
            }
        }

        if (LootpoolItem.LOOT_CLASSES.contains(wynnItem.getClass())) {
            lootpoolItems.add(new LootpoolItem(wynnItem));
        } else {
            ModInfo.logDebug("Unknown class: " + wynnItem.getClass());
        }


        return lootpoolItems;
    }

    public static List<LootpoolItem> createLootpoolItemFromItemStack(ItemStack item) {
        Optional<WynnItem> wynnItem = Models.Item.getWynnItem(item);
        return wynnItem.map(LootpoolItem::createLootpoolItemFromWynnItem).orElseGet(ArrayList::new);

    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setShinyStat(ShinyStat shinyStat) {
        this.shinyStat = shinyStat;
    }

    public Optional<ShinyStat> getShinyStat() {
        return Optional.ofNullable(this.shinyStat);
    }

    public boolean isShiny() {
        return this.shinyStat != null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself, return true
        if (this == o) return true;

        // Check if o is an instance of LootpoolItem or return false
        if (o == null || getClass() != o.getClass()) return false;

        // Typecast o to LootpoolItem to compare the attributes
        LootpoolItem that = (LootpoolItem) o;

        // Compare each field of the class
        return amount == that.amount &&
                Objects.equals(itemType, that.itemType) &&
                Objects.equals(name, that.name) &&
                Objects.equals(rarity, that.rarity) &&
                Objects.equals(shinyStat, that.shinyStat) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemType, amount, name, rarity, shinyStat, type);
    }

    @JsonIgnore
    public ChatFormatting getRarityColor() {
        return ItemStackUtils.getRarityColor(rarity);
    }
}