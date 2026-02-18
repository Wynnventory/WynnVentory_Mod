package com.wynnventory.model.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public record Container(AbstractContainerScreen<?> screen, int containerId, String title) {
    public static Container current() {
        Screen screen = Minecraft.getInstance().screen;
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return null;
        }

        return new Container(
                containerScreen,
                containerScreen.getMenu().containerId,
                containerScreen.getTitle().getString());
    }

    public boolean matchesContainer(int packetContainerId) {
        return this.containerId == packetContainerId;
    }
}
