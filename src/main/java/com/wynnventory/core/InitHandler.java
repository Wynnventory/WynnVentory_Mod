package com.wynnventory.core;

import com.wynnventory.api.WynnventoryScheduler;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.util.IconManager;
import com.wynnventory.util.LootpoolManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class InitHandler {

    public static void initialize() {
        AutoConfig.register(ConfigManager.class, GsonConfigSerializer::new);
        WynnventoryScheduler.startScheduledTask();
        LootpoolManager.reloadAllPools();
        IconManager.fetchAll();
    }
}