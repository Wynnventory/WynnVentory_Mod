package com.wynnventory.events;

import com.wynnventory.gui.widget.WynnventoryButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.bus.api.Event;

import java.util.function.Consumer;

public class RaidLobbyScreenInitEvent extends Event {
    private final AbstractContainerScreen<?> screen;
    private final Consumer<WynnventoryButton> addRenderableWidgetConsumer;

    public RaidLobbyScreenInitEvent(AbstractContainerScreen<?> screen, Consumer<WynnventoryButton> addRenderableWidgetConsumer) {
        this.screen = screen;
        this.addRenderableWidgetConsumer = addRenderableWidgetConsumer;
    }

    public void addRenderableWidget(WynnventoryButton widget) {
        addRenderableWidgetConsumer.accept(widget);
    }

    public AbstractContainerScreen<?> getScreen() {
        return screen;
    }
}
