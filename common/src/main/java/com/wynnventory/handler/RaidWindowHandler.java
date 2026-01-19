package com.wynnventory.handler;

import com.wynntils.core.components.Models;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.gui.GambitItem;
import com.wynnventory.event.RaidWindowOpenedEvent;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import com.wynnventory.queue.QueueManager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Optional;

public final class RaidWindowHandler {

    @SubscribeEvent
    public void onRaidWindowOpened(RaidWindowOpenedEvent event) {
        for(ItemStack stack : event.getItems()) {
            Optional<WynnItem> wynnItem = Models.Item.getWynnItem(stack);

            if(wynnItem.isPresent() && wynnItem.get() instanceof GambitItem item) {
                QueueManager.GAMBIT_QUEUE.addItem(new SimpleGambitItem(item));
            }
        }
    }
}
