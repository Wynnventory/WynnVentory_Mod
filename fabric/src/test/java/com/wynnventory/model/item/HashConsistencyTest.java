package com.wynnventory.model.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.wynntils.models.gear.type.GearTier;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleItemType;
import com.wynnventory.model.item.simple.SimpleTierItem;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class HashConsistencyTest {
    @Test
    void testSimpleItemHash() {
        SimpleItem item1 = new SimpleItem("ItemA", GearTier.NORMAL, SimpleItemType.EMERALD_ITEM, "TypeA", null, 1);
        SimpleItem item2 = new SimpleItem("ItemA", GearTier.NORMAL, SimpleItemType.EMERALD_ITEM, "TypeA", null, 1);
        SimpleItem item3 = new SimpleItem("ItemA", GearTier.NORMAL, SimpleItemType.EMERALD_ITEM, "TypeA", null, 2);

        assertEquals(item1.hashCode(), item2.hashCode(), "Identical SimpleItems should have same hashes.");
        assertNotEquals(
                item1.hashCode(), item3.hashCode(), "SimpleItems with different amount should have different hash.");
    }

    @Test
    void testSimpleGearItemHash() {
        SimpleGearItem gear1 = new SimpleGearItem(
                "Hero", GearTier.MYTHIC, "SPEAR", null, 1, false, 4, Optional.empty(), 98.3f, new ArrayList<>());
        SimpleGearItem gear2 = new SimpleGearItem(
                "Hero", GearTier.MYTHIC, "SPEAR", null, 1, false, 4, Optional.empty(), 98.3f, new ArrayList<>());
        SimpleGearItem gear3 = new SimpleGearItem(
                "Hero", GearTier.MYTHIC, "SPEAR", null, 2, false, 4, Optional.empty(), 98.3f, new ArrayList<>());

        assertEquals(gear1.hashCode(), gear2.hashCode(), "Identical SimpleGearItems should have same hashes.");
        assertNotEquals(
                gear1.hashCode(),
                gear3.hashCode(),
                "SimpleGearItems with different amount should have different hash.");
    }

    @Test
    void testSimpleGearItemWithStatsHash() {
        // Since we can't easily instantiate Wynntils classes (ShinyStat, StatActualValue, StatType)
        // due to their complex constructors and internal logic that might depend on game registry,
        // we'll focus on testing that SimpleGearItem's hashCode is consistent with its fields.

        // We can verify that different combinations of primitive/Simple fields result in different hashes.
        SimpleGearItem gearBase = new SimpleGearItem(
                "Hero", GearTier.MYTHIC, "SPEAR", null, 1, false, 4, Optional.empty(), 98.3f, new ArrayList<>());

        SimpleGearItem gearDifferentUnidentified = new SimpleGearItem(
                "Hero", GearTier.MYTHIC, "SPEAR", null, 1, true, 4, Optional.empty(), 98.3f, new ArrayList<>());

        SimpleGearItem gearDifferentReroll = new SimpleGearItem(
                "Hero", GearTier.MYTHIC, "SPEAR", null, 1, false, 5, Optional.empty(), 98.3f, new ArrayList<>());

        assertNotEquals(gearBase.hashCode(), gearDifferentUnidentified.hashCode(), "unidentified should affect hash");
        assertNotEquals(gearBase.hashCode(), gearDifferentReroll.hashCode(), "rerollCount should affect hash");

        // Note: Full testing with ShinyStat and actualStatsWithPercentage would require
        // a more complex test environment that can properly handle Wynntils classes.
        // Given that SimpleGearItem uses these in its hashCode, any change in them
        // will affect the hash as long as their own hashCode/equals are consistent.
    }

    @Test
    void testSimpleTierItemHash() {
        SimpleTierItem tierItem1 =
                new SimpleTierItem("IngredientA", GearTier.NORMAL, SimpleItemType.INGREDIENT, "TypeA", null, 1, 3);
        SimpleTierItem tierItem2 =
                new SimpleTierItem("IngredientA", GearTier.NORMAL, SimpleItemType.INGREDIENT, "TypeA", null, 1, 3);
        SimpleTierItem tierItem3 =
                new SimpleTierItem("IngredientA", GearTier.RARE, SimpleItemType.INGREDIENT, "TypeA", null, 1, 3);

        assertEquals(tierItem1.hashCode(), tierItem2.hashCode(), "Identical SimpleTierItems should have same hashes.");
        assertNotEquals(
                tierItem1.hashCode(),
                tierItem3.hashCode(),
                "SimpleTierItems with different rarity should have different hash.");
    }

    @Test
    void testTrademarketListingHash() {
        SimpleGearItem gear1 = new SimpleGearItem(
                "Hero", GearTier.MYTHIC, "SPEAR", null, 1, false, 4, Optional.empty(), 98.3f, new ArrayList<>());
        SimpleGearItem gear2 = new SimpleGearItem(
                "Hero", GearTier.MYTHIC, "SPEAR", null, 1, false, 4, Optional.empty(), 98.3f, new ArrayList<>());

        TrademarketListing listing1 = new TrademarketListing(gear1, 1000, 1);
        TrademarketListing listing2 = new TrademarketListing(gear2, 1000, 1);

        assertEquals(
                listing1.hashCode(), listing2.hashCode(), "Identical TrademarketListings should have same hashes.");
    }
}
