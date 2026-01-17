package com.wynnventory.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class ContainerUtil {
    public final AbstractContainerScreen<?> screen;
    public final int containerId;
    public final String title;

    private ContainerUtil(AbstractContainerScreen<?> screen, int containerId, String title) {
        this.screen = screen;
        this.containerId = containerId;
        this.title = title;
    }

    public static ContainerUtil current() {
        Screen screen = Minecraft.getInstance().screen;
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) return null;

        return new ContainerUtil(containerScreen, containerScreen.getMenu().containerId, containerScreen.getTitle().getString());
    }

    public boolean matchesContainer(int packetContainerId) {
        return this.containerId == packetContainerId;
    }
}
