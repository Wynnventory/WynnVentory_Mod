package com.wynnventory.handler;

import com.wynnventory.core.command.CommandRouter;
import com.wynnventory.events.CommandAddedEvent;
import com.wynnventory.events.CommandSentEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class CommandHandler {
    @SubscribeEvent
    public void onCommandSent(CommandSentEvent event) {
        if (CommandRouter.handleCommand(event.getCommand())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCommandAdded(CommandAddedEvent event) {
        CommandRouter.onCommandsRebuilt(event.getRoot(), event.getContext());
    }
}
