package com.wynnventory.core.event;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class ScreenOpenedEvent extends Event {
    private final Screen screen;
    private final Screen oldScreen;

    protected ScreenOpenedEvent(Screen screen, Screen oldScreen) {
        this.screen = screen;
        this.oldScreen = oldScreen;
    }

    public Screen getScreen() {
        return screen;
    }

    public Screen getOldScreen() {
        return oldScreen;
    }

    public static final class Pre extends ScreenOpenedEvent implements ICancellableEvent {
        public Pre(Screen screen, Screen oldScreen) {
            super(screen, oldScreen);
        }
    }

    public static final class Post extends ScreenOpenedEvent {
        public Post(Screen screen, Screen oldScreen) {
            super(screen, oldScreen);
        }
    }
}