package com.wynnventory.model.screen;

import com.wynntils.models.items.items.game.AspectItem;
import com.wynntils.screens.guides.GuideItemStack;
import com.wynnventory.model.item.info.AspectInfo;
import com.wynnventory.util.parsing.ComponentConverter;
import com.wynnventory.util.parsing.HtmlParser;
import com.wynnventory.util.parsing.TagNode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
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
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("   " + ChatFormatting.GRAY + "Tier I " + ChatFormatting.DARK_GRAY + ">>>>>>>>>> "
                + aspectInfo.gearTier().getChatFormatting() + "Tier II " + ChatFormatting.GRAY + "[0/"
                + (aspectInfo.tiers().get(2).threshold() - 1) + "]"));
        tooltip.add(Component.empty());

        for (String htmlLine : aspectInfo.tiers().get(1).description()) {
            String cleanedHtmlLine = htmlLine.replace("\n", "").replace("\r", "");
            Component lineComponent = parseHtmlToComponent(cleanedHtmlLine);
            tooltip.add(lineComponent);
        }

        return tooltip;
    }

    public static Component parseHtmlToComponent(String html) {
        HtmlParser parser = new HtmlParser(html);
        TagNode rootNode = parser.parse();

        Style defaultStyle = Style.EMPTY;
        Component component = Component.empty();
        for (TagNode child : rootNode.children) {
            Component childComponent = ComponentConverter.convertTagNodeToComponent(child, defaultStyle);
            component = component.copy().append(childComponent);
        }
        return component;
    }

    public AspectInfo getAspectInfo() {
        return aspectInfo;
    }

    public MutableComponent getName() {
        return name;
    }

    public List<Component> getGeneratedTooltip() {
        return generatedTooltip;
    }

    public void setGeneratedTooltip(List<Component> generatedTooltip) {
        this.generatedTooltip = generatedTooltip;
    }
}
