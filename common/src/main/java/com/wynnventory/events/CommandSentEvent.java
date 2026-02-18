package com.wynnventory.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class CommandSentEvent extends Event implements ICancellableEvent {
    private final String command;

    public CommandSentEvent(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
