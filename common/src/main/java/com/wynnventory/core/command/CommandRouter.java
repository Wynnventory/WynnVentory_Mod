package com.wynnventory.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.RootCommandNode;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.util.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

public final class CommandRouter {
    private static final CommandDispatcher<Minecraft> DISPATCHER = buildDispatcher();

    public static void onCommandsRebuilt(RootCommandNode<SharedSuggestionProvider> root, CommandBuildContext context) {
        WynnventoryCommands.registerSuggestions(root, context);
    }

    public static boolean handleCommand(String command) {
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
