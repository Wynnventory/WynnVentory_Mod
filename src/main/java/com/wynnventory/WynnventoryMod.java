package com.wynnventory;

import com.wynnventory.core.InitHandler;
import com.wynnventory.core.ModInfo;
import com.wynnventory.input.KeyBindingManager;
import com.wynnventory.input.KeyEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class WynnventoryMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		if (!ModInfo.init()) return;

		ModInfo.logDebug("Test");

		InitHandler.initialize();
		KeyBindingManager.register();
		KeyEventHandler.register();

		ModInfo.logInfo("Initialized Wynnventory v" + ModInfo.VERSION);
	}
}