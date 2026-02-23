package com.wynnventory.feature.tooltip.aspect;

import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.ItemTooltipRenderEvent;
import com.wynnventory.api.service.RewardService;
import com.wynnventory.model.container.PartyFinderContainer;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardType;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.RenderUtils;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class AspectTooltipFeature {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltipRendered(ItemTooltipRenderEvent.Pre event) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null
                && PartyFinderContainer.matchesTitle(screen.getTitle().getString())) {
            renderPartyFinderAspects(
                    event.getGuiGraphics(),
                    event.getMouseX(),
                    event.getMouseY(),
                    event.getItemStack(),
                    event.getTooltips());
        }
    }

    private void renderPartyFinderAspects(
            GuiGraphics guiGraphics, int mouseX, int mouseY, ItemStack stack, List<Component> vanillaLines) {
        StyledText originalName = ItemStackUtils.getWynntilsOriginalName(stack);
        if (originalName == null) return;

        String name = originalName.getStringWithoutFormatting();
        RewardPool pool = RewardPool.fromFullName(name);
        if (pool == null || pool.getType() != RewardType.RAID) return;

        RewardService.INSTANCE.getRaidPools().thenAccept(pools -> pools.stream()
                .filter(p -> p.getRewardPool().equals(pool))
                .findFirst()
                .ifPresent(p -> {
                    List<Component> tooltipLines = AspectTooltipHelper.buildLines(p);
                    if (tooltipLines.isEmpty()) return;

                    RenderUtils.drawTooltip(guiGraphics, mouseX, mouseY, vanillaLines, tooltipLines);
                }));
    }
}
