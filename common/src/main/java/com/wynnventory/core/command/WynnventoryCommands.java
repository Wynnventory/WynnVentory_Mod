package com.wynnventory.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.queue.QueueScheduler;
import com.wynnventory.util.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public final class WynnventoryCommands {
    public static final String PREFIX = "wynnventory";

    private WynnventoryCommands() {}

    public static void registerClientCommands(CommandDispatcher<Minecraft> dispatcher) {
        LiteralArgumentBuilder<Minecraft> root = LiteralArgumentBuilder.literal(PREFIX);

        root.then(literalClient("send").executes(ctx -> {
            sendCollectedData();
            return 1;
        }));

        root.then(literalClient("reloadConfig").executes(ctx -> {
            ModConfig.reload();
            return 1;
        }));

        dispatcher.register(root);
    }

    public static void registerSuggestions(
            RootCommandNode<SharedSuggestionProvider> root, CommandBuildContext context) {
        LiteralCommandNode<SharedSuggestionProvider> node = literalSuggestion(PREFIX)
                .then(literalSuggestion("send").executes(ctx -> 1))
                .then(literalSuggestion("reloadConfig").executes(ctx -> 1))
                .build();

        addNode(root, node);
    }

    private static void sendCollectedData() {
        QueueScheduler.sendQueuedItems();
        ChatUtils.info(Component.translatable("command.wynnventory.send.sendCollectedData"));
    }

    private static LiteralArgumentBuilder<Minecraft> literalClient(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private static LiteralArgumentBuilder<SharedSuggestionProvider> literalSuggestion(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private static <S> void addNode(CommandNode<S> root, CommandNode<S> node) {
        CommandNode<S> existing = root.getChild(node.getName());
        if (existing == null) {
            root.addChild(node);
            return;
        }
        for (CommandNode<S> child : node.getChildren()) {
            addNode(existing, child);
        }
    }
}
