package com.wynnventory;

import com.wynntils.mc.event.ScreenInitEvent;
import com.wynntils.models.containers.Container;
import com.wynntils.models.containers.containers.TradeMarketPrimaryContainer;
import com.wynntils.models.items.items.game.GearItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class Wynnventory_MarketContainer extends TradeMarketPrimaryContainer {
    public static final Logger LOGGER = LoggerFactory.getLogger("wynnventory_mod");
    private Container currentContainer = null;
    private static final List<Container> containerTypes = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onScreenInit(ScreenInitEvent e) {
        LOGGER.error("Ich bin initted");
        currentContainer = null;

        if (!(e.getScreen() instanceof AbstractContainerScreen<?> screen)) return;

        for (Container container : containerTypes) {
            if (container.isScreen(screen)) {
                currentContainer = container;
                currentContainer.setContainerId(screen.getMenu().containerId);
                break;
            }
        }
    }
}