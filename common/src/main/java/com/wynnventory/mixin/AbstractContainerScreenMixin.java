package com.wynnventory.mixin;

import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.events.InventoryKeyPressEvent;
import com.wynnventory.events.RaidLobbyScreenInitEvent;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.container.Container;
import com.wynnventory.model.container.RaidLobbyContainer;
import com.wynnventory.model.container.TrademarketContainer;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
    @Shadow
    protected Slot hoveredSlot;

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    protected int imageWidth;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("RETURN"))
    private void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (hoveredSlot == null || hoveredSlot.getItem().isEmpty()) return;

        Container container = Container.current();
        if (container == null) return;

        if (TrademarketContainer.matchesTitle(container.title()))
            WynnventoryMod.postEvent(new TrademarketTooltipRenderedEvent(hoveredSlot));
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        InventoryKeyPressEvent event = new InventoryKeyPressEvent(keyEvent);
        WynnventoryMod.postEvent(event);

        if (event.isCanceled()) cir.cancel();
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        if (RaidLobbyContainer.matchesTitle(this.getTitle().getString())) {
            WynnventoryMod.postEvent(new RaidLobbyScreenInitEvent(
                    this::addRenderableWidget, this.leftPos, this.topPos, this.imageWidth));
        }
    }
}
