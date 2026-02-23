package com.wynnventory.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public final class ChatUtils {
    private static final FontDescription PILL_FONT =
            new FontDescription.Resource(Identifier.withDefaultNamespace("banner/pill"));
    private static final Style BACKGROUND_STYLE =
            Style.EMPTY.withFont(PILL_FONT).withColor(ChatFormatting.GREEN);
    private static final Style FOREGROUND_STYLE =
            Style.EMPTY.withFont(PILL_FONT).withColor(ChatFormatting.BLACK).withoutShadow();
    private static final Component WYNNVENTORY_BACKGROUND_PILL = Component.literal(
                    "\uE060\uDAFF\uDFFF\uE046\uDAFF\uDFFF\uE048\uDAFF\uDFFF\uE03D\uDAFF\uDFFF\uE03D\uDAFF\uDFFF\uE045\uDAFF\uDFFF\uE034\uDAFF\uDFFF\uE03D\uDAFF\uDFFF\uE043\uDAFF\uDFFF\uE03E\uDAFF\uDFFF\uE041\uDAFF\uDFFF\uE048\uDAFF\uDFFF\uE062\uDAFF\uDFB7")
            .withStyle(BACKGROUND_STYLE);
    private static final Component WYNNVENTORY_FOREGROUND_PILL = Component.literal(
                    "\uDB00\uDC05\uE016\uE018\uE00D\uE00D\uE015\uE004\uE00D\uE013\uE00E\uE011\uE018\uDB00\uDC06")
            .withStyle(FOREGROUND_STYLE);

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
        return Component.empty()
                .append(WYNNVENTORY_BACKGROUND_PILL)
                .append(WYNNVENTORY_FOREGROUND_PILL)
                .append(message);
    }

    private static void send(Component component) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;
        mc.player.displayClientMessage(component, false);
    }
}
