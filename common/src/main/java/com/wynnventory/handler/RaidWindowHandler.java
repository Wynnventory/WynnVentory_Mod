package com.wynnventory.handler;

import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.gui.GambitItem;
import com.wynntils.screens.guides.aspect.GuideAspectItemStack;
import com.wynnventory.api.service.RewardService;
import com.wynnventory.core.queue.QueueScheduler;
import com.wynnventory.events.RaidLobbyPopulatedEvent;
import com.wynnventory.events.RaidLobbyRenderedEvent;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardPoolDocument;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RaidWindowHandler {

    @SubscribeEvent
    public void onRaidLobbyPopulated(RaidLobbyPopulatedEvent event) {
        for (ItemStack stack : event.getItems()) {
            WynnItem wynnItem = ItemStackUtils.getWynnItem(stack);

            if (wynnItem == null) continue;
            if (wynnItem instanceof GambitItem item) {
                QueueScheduler.GAMBIT_QUEUE.addItem(new SimpleGambitItem(item));
            }
        }
    }

    @SubscribeEvent
    public void onRaidLobbyRendered(RaidLobbyRenderedEvent event) {
        // Cache aspect stack references for rendering
        Map<String, GuideAspectItemStack> aspectStacks = Models.Aspect.getAllAspectInfos()
                .map(info -> new GuideAspectItemStack(info, 1))
                .collect(Collectors.toMap(stack -> stack.getAspectInfo().name(), Function.identity()));

        List<RewardPoolDocument> raidPools = RewardService.INSTANCE.getRaidPools().join();
        List<WynnventoryItemButton<GuideAspectItemStack>> tooltipButtons = new ArrayList<>();

        int x = 20;
        int y = 75;
        int itemSize = 16;
        int spacing = 22;

        for (Lootpool pool : raidPools) {
            Region region = Region.getRegionByShortName(pool.getRegion());
            if (region == null) continue;

            // Draw section header
            Component title = Component.literal(region.getShortName() + " Mythic Aspects")
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
            guiGraphics.drawString(McUtils.mc().font, title, x, y, 0xFFFFFF);

            final int[] buttonX = {x};
            int buttonY = y + 12;

            // Render each mythic aspect as a button
            pool.getMythicAspects()
                    .forEach(lootItem -> {
                        GuideAspectItemStack stack = aspectStacks.get(lootItem.getName());
                        if (stack == null) return;

                        WynnventoryItemButton<GuideAspectItemStack> button =
                                new WynnventoryItemButton<>(buttonX[0], buttonY, itemSize, itemSize, stack, false);
                        button.setPosition(buttonX[0], buttonY);
                        button.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                        tooltipButtons.add(button);

                        buttonX[0] += spacing;
                    });

            y += 40;
        }

        // Render tooltip overlays
        for (WynnventoryItemButton<GuideAspectItemStack> button : tooltipButtons) {
            if (isMouseOver(button, mouseX, mouseY)) {
                guiGraphics.renderTooltip(
                        FontRenderer.getInstance().getFont(),
                        button.getItemStack(),
                        mouseX,
                        mouseY
                );

                PriceTooltipHelper.renderPriceInfoTooltip(
                        guiGraphics,
                        mouseX,
                        mouseY,
                        button.getItemStack(),
                        ItemStackUtils.getTooltips(button.getItemStack()),
                        false
                );
            }
        }
    }
}
