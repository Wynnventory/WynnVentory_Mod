package com.wynnventory.feature.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.RootCommandNode;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.events.CommandAddedEvent;
import com.wynnventory.events.CommandSentEvent;
import com.wynnventory.util.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.neoforged.bus.api.SubscribeEvent;

public final class CommandFeature {
    private static final CommandDispatcher<Minecraft> DISPATCHER = buildDispatcher();

    @SubscribeEvent
    public void onCommandSent(CommandSentEvent event) {
        if (handleCommand(event.getCommand())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCommandAdded(CommandAddedEvent event) {
        onCommandsRebuilt(event.getRoot());
    }

    private static void onCommandsRebuilt(RootCommandNode<SharedSuggestionProvider> root) {
        WynnventoryCommands.registerSuggestions(root);
    }

    private static boolean handleCommand(String command) {
        if (!command.startsWith(WynnventoryCommands.PREFIX)) return false;

        ParseResults<Minecraft> parse = DISPATCHER.parse(new StringReader(command), Minecraft.getInstance());

        try {
            DISPATCHER.execute(parse);
        } catch (CommandSyntaxException e) {
            ChatUtils.error(e.getRawMessage().getString());
            WynnventoryMod.logError("Failed to execute command '{}'", command, e);
        } catch (Exception e) {
            ChatUtils.error("Command failed. Check logs for details.");
            WynnventoryMod.logError("Failed to execute command '{}'", command, e);
        }
        return true;
    }

    private static CommandDispatcher<Minecraft> buildDispatcher() {
        CommandDispatcher<Minecraft> dispatcher = new CommandDispatcher<>();
        WynnventoryCommands.registerClientCommands(dispatcher);
        return dispatcher;
    }
}
