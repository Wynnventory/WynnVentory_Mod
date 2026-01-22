package com.wynnventory.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class ChatUtils {
    private static final Component PREFIX = Component.literal("[Wynnventory] ").withStyle(ChatFormatting.GREEN);

    private ChatUtils() {}

    public static void info(String message) {
        info(Component.literal(message));
    }

    public static void info(Component message) {
        send(prefixed(message.copy().withStyle(ChatFormatting.WHITE)));
    }

    public static void error(String message) {
        error(Component.literal(message));
    }

    public static void error(Component message) {
        send(prefixed(message.copy().withStyle(ChatFormatting.RED)));
    }

    private static Component prefixed(Component message) {
        return PREFIX.copy().append(message);
    }

    private static void send(Component component) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;
        mc.player.displayClientMessage(component, false);
    }
}
