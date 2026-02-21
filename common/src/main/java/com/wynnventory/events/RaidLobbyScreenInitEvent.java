package com.wynnventory.events;

import com.wynnventory.gui.widget.WynnventoryButton;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.bus.api.Event;

public class RaidLobbyScreenInitEvent extends Event {
    private final AbstractContainerScreen<?> screen;
    private final Consumer<WynnventoryButton> addRenderableWidgetConsumer;
    private final int leftPos;
    private final int topPos;
    private final int imageWidth;

    public RaidLobbyScreenInitEvent(
            AbstractContainerScreen<?> screen,
            Consumer<WynnventoryButton> addRenderableWidgetConsumer,
            int leftPos,
            int topPos,
            int imageWidth) {
        this.screen = screen;
        this.addRenderableWidgetConsumer = addRenderableWidgetConsumer;
        this.leftPos = leftPos;
        this.topPos = topPos;
        this.imageWidth = imageWidth;
    }

    public void addRenderableWidget(WynnventoryButton widget) {
        addRenderableWidgetConsumer.accept(widget);
    }

    public AbstractContainerScreen<?> getScreen() {
        return screen;
    }

    public int getLeftPos() {
        return leftPos;
    }

    public int getTopPos() {
        return topPos;
    }

    public int getImageWidth() {
        return imageWidth;
    }
}
