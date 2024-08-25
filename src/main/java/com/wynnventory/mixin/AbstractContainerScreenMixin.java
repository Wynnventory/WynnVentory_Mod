package com.wynnventory.mixin;

import com.wynnventory.accessor.ItemQueueAccessor;
import com.wynnventory.api.WynnventoryAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    private static final WynnventoryAPI API = new WynnventoryAPI();

    @Inject(method = "onClose", at = @At("RETURN"))
    private void onContainerClose(CallbackInfo ci) {
        ItemQueueAccessor accessor = (ItemQueueAccessor) Minecraft.getInstance().getConnection();
        if (accessor != null) {
            if (!accessor.getQueuedMarketItems().isEmpty()) {
                API.sendTradeMarketResults(accessor.getQueuedMarketItems());
                accessor.getQueuedMarketItems().clear();
            }
            if (!accessor.getQueuedLootItems().isEmpty()) {
                API.sendLootpoolData(accessor.getQueuedLootItems());
                accessor.getQueuedLootItems().clear();
            }
        }
    }
}