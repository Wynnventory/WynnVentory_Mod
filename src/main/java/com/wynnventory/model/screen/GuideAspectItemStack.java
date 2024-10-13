package com.wynnventory.model.screen;

import com.wynntils.models.items.items.game.AspectItem;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynnventory.model.item.info.AspectInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class GuideAspectItemStack extends GuideItemStack {
    private final AspectInfo aspectInfo;
    private final MutableComponent name;
    private List<Component> generatedTooltip = List.of();

    public GuideAspectItemStack(AspectInfo aspectInfo) {
        super(aspectInfo.material().itemStack(), new AspectItem(aspectInfo.classType(), aspectInfo.gearTier(), 1), aspectInfo.name());

        this.aspectInfo = aspectInfo;
        this.name = Component.literal(aspectInfo.name());
    }

    @Override
    public Component getHoverName() {
        return name;
    }

    @Override
    public List<Component> getTooltipLines(Item.TooltipContext context, Player player, TooltipFlag isAdvanced) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(getHoverName().copy().withStyle(aspectInfo.gearTier().getChatFormatting()));
        tooltip.add(Component.literal(aspectInfo.tiers().get(1).description()));

        return tooltip;
    }
}
