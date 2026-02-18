package com.wynnventory.handler;

import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.gui.GambitItem;
import com.wynntils.screens.guides.aspect.GuideAspectItemStack;
import com.wynnventory.api.service.RewardService;
import com.wynnventory.core.queue.QueueScheduler;
import com.wynnventory.events.RaidLobbyPopulatedEvent;
import com.wynnventory.events.RaidLobbyScreenInitEvent;
import com.wynnventory.gui.widget.ItemButton;
import com.wynnventory.gui.widget.TextWidget;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import com.wynnventory.model.reward.RewardPoolDocument;
import com.wynnventory.util.ItemStackUtils;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public final class RaidWindowHandler {
    @SubscribeEvent
    public void onRaidLobbyPopulated(RaidLobbyPopulatedEvent event) {
        for (ItemStack stack : event.getItems()) {
            WynnItem wynnItem = ItemStackUtils.getWynnItem(stack);
            if (wynnItem instanceof GambitItem item) {
                QueueScheduler.GAMBIT_QUEUE.addItem(new SimpleGambitItem(item));
            }
        }
    }

    @SubscribeEvent
    public void onScreenInit(RaidLobbyScreenInitEvent event) {
        Map<String, GuideAspectItemStack> aspectStacks = Models.Aspect.getAllAspectInfos()
                .map(info -> new GuideAspectItemStack(info, 1))
                .collect(Collectors.toMap(stack -> stack.getAspectInfo().name(), Function.identity()));
        List<RewardPoolDocument> raidPools =
                RewardService.INSTANCE.getRaidPools().join();

        int currentY = 50;
        int itemSize = 16;
        int spacing = 22;
        int startX = 50;

        for (RewardPoolDocument pool : raidPools) {
            Component title = Component.literal(pool.getRewardPool().getShortName() + " Mythic Aspects")
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
            event.addRenderableWidget(new TextWidget(startX, currentY, title));

            int buttonY = currentY + 12;
            final int[] buttonX = {startX};

            pool.getMythicAspects().forEach(lootItem -> {
                GuideAspectItemStack stack = aspectStacks.get(lootItem.getName());
                if (stack == null) return;

                ItemButton<GuideAspectItemStack> button =
                        new ItemButton<>(buttonX[0], buttonY, itemSize, itemSize, stack, lootItem);
                event.addRenderableWidget(button);
                buttonX[0] += spacing;
            });

            currentY += 40;
        }
    }
}
