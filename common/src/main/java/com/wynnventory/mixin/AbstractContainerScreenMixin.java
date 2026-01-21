package com.wynnventory.mixin;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.container.TrademarketContainer;
import com.wynnventory.util.ContainerUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

    @Shadow
    protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(
            method =
                    "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V",
            at = @At("RETURN"))
    private void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (hoveredSlot == null || hoveredSlot.getItem().isEmpty()) return;

        ContainerUtil container = ContainerUtil.current();
        if (container == null) return;

        if(TrademarketContainer.matchesTitle(container.title)) WynnventoryMod.postEvent(new TrademarketTooltipRenderedEvent(hoveredSlot));
    }
}
