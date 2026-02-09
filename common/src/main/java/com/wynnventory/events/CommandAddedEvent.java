package com.wynnventory.events;

import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.neoforged.bus.api.Event;

public class CommandAddedEvent extends Event {
    private final CommandBuildContext context;
    private final RootCommandNode<SharedSuggestionProvider> root;

    public CommandAddedEvent(RootCommandNode<SharedSuggestionProvider> root, CommandBuildContext context) {
        this.root = root;
        this.context = context;
    }

    public RootCommandNode<SharedSuggestionProvider> getRoot() {
        return root;
    }

    public CommandBuildContext getContext() {
        return context;
    }
}
