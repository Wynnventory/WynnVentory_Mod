package com.wynnventory.handler;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.input.KeyBindManager;
import com.wynnventory.core.input.KeyBinds;
import com.wynnventory.events.ClientTickEvent;
import com.wynnventory.events.InventoryKeyPressEvent;
import com.wynnventory.util.ChatUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Map;

public final class KeyBindHandler {

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof ChatScreen) return;
        if (mc.screen != null && mc.screen.getFocused() instanceof EditBox) return;

        for (Map.Entry<KeyBinds, KeyMapping> entry : KeyBindManager.all()) {
            while (entry.getValue().consumeClick()) {
                handle(entry.getKey());
            }
        }
    }

    @SubscribeEvent
    public void onInventoryKeyPress(InventoryKeyPressEvent event) {
        for (Map.Entry<KeyBinds, KeyMapping> entry : KeyBindManager.all()) {
            if (!entry.getKey().allowInInventory) continue;
            if (entry.getValue().matches(event.getKeyEvent())) {
                handle(entry.getKey());
            }
        }
    }

    private void handle(KeyBinds key) {
        if (key == KeyBinds.OPEN_REWARD_POOL) {
            WynnventoryMod.logInfo("N pressed");
        } else if (key == KeyBinds.SETTINGS_TOGGLE_TOOLTIPS) {
            boolean previousState = ModConfig.getInstance().getTooltipSettings().isShowTooltips();
            ModConfig.getInstance().getTooltipSettings().setShowTooltips(!previousState);

            if(previousState) {
                ChatUtils.info(Component.translatable("key.wynnventory.settings.toggleTooltips.message.disabled"));
            } else {
                ChatUtils.info(Component.translatable("key.wynnventory.settings.toggleTooltips.message.enabled"));
            }

        } else if (key == KeyBinds.SETTINGS_TOGGLE_BOXED_TOOLTIPS) {
            boolean previousState = ModConfig.getInstance().getTooltipSettings().isShowBoxedItemTooltips();
            ModConfig.getInstance().getTooltipSettings().setShowBoxedItemTooltips(!previousState);

            if(previousState) {
                ChatUtils.info(Component.translatable("key.wynnventory.settings.toggleBoxedTooltips.message.disabled"));
            } else {
                ChatUtils.info(Component.translatable("key.wynnventory.settings.toggleBoxedTooltips.message.enabled"));
            }
        }
    }
}
