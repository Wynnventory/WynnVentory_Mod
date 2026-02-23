package com.wynnventory.feature;

import com.wynntils.core.components.Models;
import com.wynntils.screens.guides.aspect.GuideAspectItemStack;
import com.wynnventory.api.service.RewardService;
import com.wynnventory.events.RaidLobbyScreenInitEvent;
import com.wynnventory.gui.Sprite;
import com.wynnventory.gui.widget.ImageWidget;
import com.wynnventory.gui.widget.ItemButton;
import com.wynnventory.gui.widget.TextWidget;
import com.wynnventory.model.reward.RewardPoolDocument;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;

public final class AspectOverlayFeature {
    @SubscribeEvent
    public void onScreenInit(RaidLobbyScreenInitEvent event) {
        Map<String, GuideAspectItemStack> aspectStacks = Models.Aspect.getAllAspectInfos()
                .map(info -> new GuideAspectItemStack(info, 1))
                .collect(Collectors.toMap(stack -> stack.getAspectInfo().name(), Function.identity()));
        List<RewardPoolDocument> raidPools =
                RewardService.INSTANCE.getRaidPools().join();

        int startX = event.getLeftPos() + event.getImageWidth() + 30;
        int startY = event.getTopPos();

        event.addRenderableWidget(new ImageWidget(
                startX,
                startY,
                Sprite.MYTHIC_ASPECT_DISPLAY.width(),
                Sprite.MYTHIC_ASPECT_DISPLAY.height(),
                Sprite.MYTHIC_ASPECT_DISPLAY));

        int itemSize = 16;
        int spacing = 18;

        for (int i = 0; i < Math.min(raidPools.size(), 5); i++) {
            RewardPoolDocument pool = raidPools.get(i);
            int rowY = startY + 3 + (i * 35);

            Component title = Component.literal(pool.getRewardPool().getShortName())
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);

            // Center text in the nameplate area (approx 34 pixels wide)
            int textWidth = Minecraft.getInstance().font.width(title);
            int textX = startX + 17 + (34 - textWidth) / 2;
            event.addRenderableWidget(new TextWidget(textX, rowY + 3, title));

            int buttonY = rowY + 15;
            final int[] buttonX = {startX + 8};

            pool.getMythicAspects().forEach(lootItem -> {
                GuideAspectItemStack stack = aspectStacks.get(lootItem.getName());
                if (stack == null) return;

                ItemButton<GuideAspectItemStack> button =
                        new ItemButton<>(buttonX[0], buttonY, itemSize, itemSize, stack, lootItem);
                event.addRenderableWidget(button);
                buttonX[0] += spacing;
            });
        }
    }
}
