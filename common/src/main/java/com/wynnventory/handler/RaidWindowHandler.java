package com.wynnventory.handler;

import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.gui.GambitItem;
import com.wynnventory.core.queue.QueueScheduler;
import com.wynnventory.events.RaidWindowOpenedEvent;
import com.wynnventory.model.item.simple.SimpleGambitItem;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public final class RaidWindowHandler {

    @SubscribeEvent
    public void onRaidWindowOpened(RaidWindowOpenedEvent event) {
        for(ItemStack stack : event.getItems()) {
            WynnItem wynnItem = ItemStackUtils.getWynnItem(stack);

            if (wynnItem == null) continue;
            if(wynnItem instanceof GambitItem item) {
                QueueScheduler.GAMBIT_QUEUE.addItem(new SimpleGambitItem(item));
            }
        }
    }
}
