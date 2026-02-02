package com.wynnventory;

import com.wynnventory.bootstrap.WynnventoryPreLaunch;
import com.wynnventory.core.InitHandler;
import com.wynnventory.core.ModInfo;
import com.wynnventory.input.KeyBindingManager;
import com.wynnventory.input.KeyEventHandler;
import net.fabricmc.api.ClientModInitializer;

/**
 * Main client initializer for Wynnventory
 * Mixin configuration loading is handled in {@link WynnventoryPreLaunch}
 */
public class WynnventoryMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Verify mixins were loaded during pre-launch (diagnostic logging)
		String loadedConfig = WynnventoryPreLaunch.getLoadedConfigName();
		if (loadedConfig != null) {
			ModInfo.LOGGER.debug("Mixins loaded via pre-launch: {}", loadedConfig);
		} else {
			ModInfo.LOGGER.warn("Mixins may not have been loaded during pre-launch phase!");
		}

		if (!ModInfo.init()) return;

		InitHandler.initialize();
		KeyBindingManager.register();
		KeyEventHandler.register();

		ModInfo.logInfo("Initialized Wynnventory v" + ModInfo.VERSION);
	}
}