package com.wynnventory.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.queue.QueueScheduler;
import net.minecraft.client.Minecraft;

public final class WynnventoryCommands {
    private static final CommandDispatcher<Minecraft> DISPATCHER = new CommandDispatcher<>();
    private static final String PREFIX = "wynnventory";

    public static void init() {
        DISPATCHER.register(
                literal(PREFIX)
                        .then(literal("send")
                                .executes(c -> {
                                    QueueScheduler.processQueuedItems();
                                    return 1;
                                })
                        )
        );
    }

    public static boolean handleCommand(String command) {
        if (!command.startsWith(PREFIX)) return false; // not our command

        Minecraft mc = Minecraft.getInstance();
        ParseResults<Minecraft> parse = DISPATCHER.parse(new StringReader(command), mc);

        try {
            DISPATCHER.execute(parse);
            return true;
        } catch (CommandSyntaxException e) {
            WynnventoryMod.logError("Unable to execute command '{}'. Error: {}", command, e.getMessage());
            return true;
        }
    }

    private static LiteralArgumentBuilder<Minecraft> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }
}
