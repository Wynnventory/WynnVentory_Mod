package com.wynnventory.events;

import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.SharedSuggestionProvider;
import net.neoforged.bus.api.Event;

public class CommandAddedEvent extends Event {
    private final RootCommandNode<SharedSuggestionProvider> root;

    public CommandAddedEvent(RootCommandNode<SharedSuggestionProvider> root) {
        this.root = root;
    }

    public RootCommandNode<SharedSuggestionProvider> getRoot() {
        return root;
    }

}
