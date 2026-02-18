package com.wynnventory.handler;

import com.wynnventory.core.input.KeyBindManager;
import com.wynnventory.core.input.KeyBinds;
import com.wynnventory.events.ClientTickEvent;
import com.wynnventory.events.InventoryKeyPressEvent;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.neoforged.bus.api.SubscribeEvent;

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
        if (key.callback != null) {
            key.callback.run();
        }
    }
}
