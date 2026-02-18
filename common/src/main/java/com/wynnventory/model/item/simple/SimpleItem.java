package com.wynnventory.model.item.simple;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.WynnItemData;
import com.wynntils.models.items.items.game.AspectItem;
import com.wynntils.models.items.items.game.DungeonKeyItem;
import com.wynntils.models.items.items.game.EmeraldItem;
import com.wynntils.models.items.items.game.InsulatorItem;
import com.wynntils.models.items.items.game.RuneItem;
import com.wynntils.models.items.items.game.SimulatorItem;
import com.wynntils.models.items.items.game.TomeItem;
import com.wynnventory.api.service.IconService;
import com.wynnventory.model.item.Icon;
import com.wynnventory.model.item.TimestampedObject;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.StringUtils;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "itemType",
        visible = true,
        defaultImpl = SimpleItem.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SimpleGearItem.class, name = "GearItem"),
    @JsonSubTypes.Type(value = SimpleTierItem.class, name = "IngredientItem"),
    @JsonSubTypes.Type(value = SimpleTierItem.class, name = "MaterialItem"),
    @JsonSubTypes.Type(value = SimpleTierItem.class, name = "PowderItem"),
    @JsonSubTypes.Type(value = SimpleTierItem.class, name = "AmplifierItem"),
    @JsonSubTypes.Type(value = SimpleTierItem.class, name = "HorseItem"),
    @JsonSubTypes.Type(value = SimpleTierItem.class, name = "EmeraldPouchItem")
})
public class SimpleItem extends TimestampedObject {
    protected String name = "";
    protected GearTier rarity = GearTier.NORMAL;
    protected SimpleItemType itemType;
    protected String type = "";
    protected Icon icon;
    protected int amount;

    public SimpleItem() {}

    public SimpleItem(String name, GearTier rarity, SimpleItemType itemType, String type) {
        this(name, rarity, itemType, type, null);
    }

    public SimpleItem(String name, GearTier rarity, SimpleItemType itemType, String type, Icon icon) {
        this(name, rarity, itemType, type, icon, 1);
    }

    public SimpleItem(String name, GearTier rarity, SimpleItemType itemType, String type, Icon icon, int amount) {
        this.name = name != null ? name : "";
        this.rarity = rarity;
        this.itemType = itemType;
        this.type = type != null ? type : "";
        this.icon = icon;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        if (rarity == GearTier.NORMAL) return "Common";

        return rarity.getName();
    }

    @JsonIgnore
    public GearTier getRarityEnum() {
        return rarity;
    }

    public String getItemType() {
        return itemType.getType();
    }

    @JsonIgnore
    public SimpleItemType getItemTypeEnum() {
        return itemType;
    }

    public String getType() {
        return type;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public void setRarity(String rarity) {
        this.rarity = GearTier.fromString(rarity);
    }

    public void setItemType(String itemType) {
        this.itemType = SimpleItemType.fromType(itemType);
    }

    @JsonProperty("type")
    @JsonAlias("subtype")
    public void setType(String type) {
        this.type = type != null ? type : "";
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimpleItem other) {
            return Objects.equals(name, other.name)
                    && Objects.equals(rarity, other.rarity)
                    && Objects.equals(itemType, other.itemType)
                    && Objects.equals(type, other.type)
                    && amount == other.amount;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rarity, itemType, type, amount);
    }

    @Override
    public String toString() {
        return "SimpleItem{" + "name='"
                + name + '\'' + ", rarity='"
                + rarity + '\'' + ", itemType='"
                + itemType + '\'' + ", type='"
                + type + '\'' + ", icon="
                + icon + ", amount="
                + amount + '}';
    }

    public static SimpleItem from(ItemStack stack) {
        return from(ItemStackUtils.getWynnItem(stack));
    }

    public static SimpleItem from(WynnItem item) {
        return switch (item) {
            case SimulatorItem simItem -> fromSimulatorItem(simItem);
            case InsulatorItem insItem -> fromInsulatorItem(insItem);
            case RuneItem runeItem -> fromRuneItem(runeItem);
            case DungeonKeyItem dungeonKeyItem -> fromDungeonKeyItem(dungeonKeyItem);
            case EmeraldItem emeraldItem -> fromEmeraldItem(emeraldItem);
            case AspectItem aspectItem -> fromAspectItem(aspectItem);
            case TomeItem tomeItem -> fromTomeItem(tomeItem);
            case null, default -> null;
        };
    }

    private static SimpleItem fromSimulatorItem(SimulatorItem item) {
        return createSimpleItem(item, item.getGearTier(), SimpleItemType.SIMULATOR, "Simulator");
    }

    private static SimpleItem fromInsulatorItem(InsulatorItem item) {
        return createSimpleItem(item, item.getGearTier(), SimpleItemType.INSULATOR, "Insulator");
    }

    private static SimpleItem fromRuneItem(RuneItem item) {
        return createSimpleItem(item, SimpleItemType.RUNE);
    }

    private static SimpleItem fromDungeonKeyItem(DungeonKeyItem item) {
        return createSimpleItem(item, SimpleItemType.DUNGEON_KEY);
    }

    private static SimpleItem fromEmeraldItem(EmeraldItem emeraldItem) {
        return createSimpleItem(
                emeraldItem,
                GearTier.NORMAL,
                SimpleItemType.EMERALD_ITEM,
                emeraldItem.getUnit().name());
    }

    private static SimpleItem fromAspectItem(AspectItem aspectItem) {
        return createSimpleItem(
                aspectItem,
                aspectItem.getGearTier(),
                SimpleItemType.ASPECT,
                aspectItem.getRequiredClass().getName() + "Aspect");
    }

    private static SimpleItem fromTomeItem(TomeItem tomeItem) {
        return new SimpleItem(
                tomeItem.getName().replace("Unidentified ", ""),
                tomeItem.getGearTier(),
                SimpleItemType.TOME,
                tomeItem.getItemInfo().type().name());
    }

    private static SimpleItem createSimpleItem(WynnItem item, SimpleItemType itemType) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);
        return createSimpleItem(item, GearTier.NORMAL, itemType, StringUtils.toCamelCase(name));
    }

    private static SimpleItem createSimpleItem(WynnItem item, GearTier rarity, SimpleItemType itemType, String type) {
        String name = ItemStackUtils.getWynntilsOriginalNameAsString(item);
        int amount = ((ItemStack) item.getData().get(WynnItemData.ITEMSTACK_KEY)).getCount();
        return new SimpleItem(name, rarity, itemType, type, IconService.INSTANCE.getIcon(name), amount);
    }
}
