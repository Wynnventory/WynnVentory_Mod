package com.wynnventory;

import com.wynnventory.event.ContainerSetContentEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.regex.Pattern;

public class LootRewardHandler {

    private static Pattern LOOT1 = Pattern.compile("\uDAFF\uDFF2\uE00A\uDAFF\uDF6F\uF006"); // TODO

    @SubscribeEvent
    public void onHandleContainerContent(ContainerSetContentEvent event) {
        System.out.println("We're in screen: " + event.getScreenTitle());

//        for (ItemStack item : event.getItems()) {
//            System.out.println(item.getItemName().getString());
//        }
    }
}
