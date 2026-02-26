package com.wynnventory.feature;

import com.wynntils.core.WynntilsMod;
import com.wynntils.core.mod.event.WynncraftConnectionEvent;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.feature.command.CommandFeature;
import com.wynnventory.feature.crowdsource.CrowdSourceFeature;
import com.wynnventory.feature.input.KeybindFeature;
import com.wynnventory.feature.joinmessage.MessageSeverity;
import com.wynnventory.feature.joinmessage.ServerJoinMessageFeature;
import com.wynnventory.feature.tooltip.aspect.AspectTooltipFeature;
import com.wynnventory.feature.tooltip.price.PriceTooltipFeature;
import com.wynnventory.feature.updater.AutoUpdateFeature;
import net.neoforged.bus.api.SubscribeEvent;

public enum FeatureManager {
    INSTANCE;

    // Registered with wynnventory
    private final AspectOverlayFeature overlayFeature = new AspectOverlayFeature();
    private final CommandFeature commandFeature = new CommandFeature();
    private final CrowdSourceFeature crowdSourceFeature = new CrowdSourceFeature();
    private final KeybindFeature keybindFeature = new KeybindFeature();

    // Registered with wynntils
    private final AspectTooltipFeature aspectTooltipFeature = new AspectTooltipFeature();
    private final FavouriteNotifyFeature favouriteNotifyFeature = new FavouriteNotifyFeature();
    private final PriceTooltipFeature priceTooltipFeature = new PriceTooltipFeature();

    private boolean isBetaServer = false;

    FeatureManager() {
        WynntilsMod.registerEventListener(new AutoUpdateFeature());
        WynntilsMod.registerEventListener(new ServerJoinMessageFeature());
    }

    @SubscribeEvent
    public void onConnect(WynncraftConnectionEvent.Connected event) {
        isBetaServer = event.getHost().equalsIgnoreCase("beta");

        if (shouldEnableFeatures()) {
            registerFeatures();
        } else {
            unregisterFeatures();

            if (WynnventoryMod.isBeta() && !isBetaServer) {
                ServerJoinMessageFeature.queueCharSelectionMessage(
                        MessageSeverity.ERROR, "feature.wynnventory.disabled.betaOnReleaseServer");
            } else {
                ServerJoinMessageFeature.queueCharSelectionMessage(
                        MessageSeverity.ERROR, "feature.wynnventory.disabled.releaseOnBetaServer");
            }
        }
    }

    public boolean shouldEnableFeatures() {
        return isBetaServer == WynnventoryMod.isBeta();
    }

    private void registerFeatures() {
        WynnventoryMod.registerEventListener(overlayFeature);
        WynnventoryMod.registerEventListener(commandFeature);
        WynnventoryMod.registerEventListener(crowdSourceFeature);
        WynnventoryMod.registerEventListener(keybindFeature);

        WynntilsMod.registerEventListener(aspectTooltipFeature);
        WynntilsMod.registerEventListener(favouriteNotifyFeature);
        WynntilsMod.registerEventListener(priceTooltipFeature);
    }

    private void unregisterFeatures() {
        WynnventoryMod.unregisterEventListener(overlayFeature);
        WynnventoryMod.unregisterEventListener(commandFeature);
        WynnventoryMod.unregisterEventListener(crowdSourceFeature);
        WynnventoryMod.unregisterEventListener(keybindFeature);

        WynntilsMod.unregisterEventListener(aspectTooltipFeature);
        WynntilsMod.unregisterEventListener(favouriteNotifyFeature);
        WynntilsMod.unregisterEventListener(priceTooltipFeature);
    }
}
