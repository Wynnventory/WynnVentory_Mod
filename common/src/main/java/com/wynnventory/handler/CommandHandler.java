package com.wynnventory.handler;

import com.wynnventory.core.command.WynnventoryCommands;
import com.wynnventory.event.CommandSentEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class CommandHandler {

    @SubscribeEvent
    public void onCommandSent(CommandSentEvent event) {
        if (WynnventoryCommands.handleCommand(event.getCommand())) {
            event.setCanceled(true);
        }
    }
}