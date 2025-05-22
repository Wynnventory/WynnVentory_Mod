package com.wynnventory.input;

import com.wynntils.utils.mc.McUtils;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.ui.LootpoolScreen;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;

public class KeyEventHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KeyBindingManager.OPEN_POOLS.consumeClick()) {
                client.setScreen(new LootpoolScreen(Component.literal("Lootruns")));
            }

            if (KeyBindingManager.TOGGLE_TOOLTIP.hasStateChanged()) {
                ConfigManager config = ConfigManager.getInstance();
                config.setShowTooltips(!config.isShowTooltips());
                AutoConfig.getConfigHolder(ConfigManager.class).save();

                McUtils.sendMessageToClient(Component.literal("[Wynnventory] Trade Market item tooltips "
                                + (config.isShowTooltips() ? "enabled" : "disabled"))
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
            }

            if (KeyBindingManager.TOGGLE_BOXED_TOOLTIP.hasStateChanged()) {
                ConfigManager config = ConfigManager.getInstance();
                config.setShowBoxedItemTooltips(!config.isShowBoxedItemTooltips());
                AutoConfig.getConfigHolder(ConfigManager.class).save();

                McUtils.sendMessageToClient(Component.literal("[Wynnventory] Boxed tooltips "
                                + (config.isShowBoxedItemTooltips() ? "enabled" : "disabled"))
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
            }
        });
    }
}