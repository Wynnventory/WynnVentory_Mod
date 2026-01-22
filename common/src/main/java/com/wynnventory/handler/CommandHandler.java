package com.wynnventory.handler;

import com.wynnventory.core.command.WynnventoryCommandManager;
import com.wynnventory.event.CommandAddedEvent;
import com.wynnventory.event.CommandSentEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class CommandHandler {

    @SubscribeEvent
    public void onCommandSent(CommandSentEvent event) {
        if (WynnventoryCommandManager.handleCommand(event.getCommand())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCommandAdded(CommandAddedEvent event) {
        WynnventoryCommandManager.onCommandsRebuilt(event.getRoot(), event.getContext());
    }

}