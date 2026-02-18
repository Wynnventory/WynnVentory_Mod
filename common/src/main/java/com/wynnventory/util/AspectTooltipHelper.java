package com.wynnventory.util;

import com.wynntils.models.gear.type.GearTier;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.reward.RewardPoolDocument;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class AspectTooltipHelper {
    private static final ChatFormatting MYTHIC_COLOR = GearTier.MYTHIC.getChatFormatting();

    private AspectTooltipHelper() {}

    public static List<Component> buildLines(RewardPoolDocument pool) {
        List<SimpleItem> mythics = pool.getMythicAspects();

        if (mythics.isEmpty()) {
            return Collections.emptyList();
        }

        Component header = Component.literal("Mythic Aspects").withStyle(MYTHIC_COLOR, ChatFormatting.BOLD);

        List<Component> bullets = mythics.stream()
                .map(i -> {
                    ClassIcon ic = ClassIcon.fromAspectType(i.getType());
                    String txt = (ic != null ? ic.get() + " " : "") + i.getName();
                    return Component.literal("• " + txt).withStyle(ChatFormatting.GRAY);
                })
                .collect(Collectors.toList());

        List<Component> out = new ArrayList<>(bullets.size() + 1);
        out.add(header);
        out.addAll(bullets);
        return out;
    }

    enum ClassIcon {
        WARRIOR(""),
        ARCHER(""),
        MAGE(""),
        ASSASSIN(""),
        SHAMAN("");

        private final String icon;

        ClassIcon(String icon) {
            this.icon = icon;
        }

        public String get() {
            return icon;
        }

        public static ClassIcon fromAspectType(String type) {
            return switch (type.toLowerCase()) {
                case "warrioraspect" -> WARRIOR;
                case "archeraspect" -> ARCHER;
                case "mageaspect" -> MAGE;
                case "assassinaspect" -> ASSASSIN;
                case "shamanaspect" -> SHAMAN;
                default -> null;
            };
        }
    }
}
