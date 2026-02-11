package com.wynnventory.mixin;

import com.wynntils.utils.mc.McUtils;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.events.InventoryKeyPressEvent;
import com.wynnventory.events.RaidLobbyRenderedEvent;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.container.Container;
import com.wynnventory.model.container.TrademarketContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
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
    private static final String RAID_LOBBY_TITLE = "\uDAFF\uDFE1\uE00C";

    @Shadow
    protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V",
            at = @At("RETURN"))
    private void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (hoveredSlot == null || hoveredSlot.getItem().isEmpty()) return;

        Container container = Container.current();
        if (container == null) return;

        if (TrademarketContainer.matchesTitle(container.title()))
            WynnventoryMod.postEvent(new TrademarketTooltipRenderedEvent(hoveredSlot));
    }

    @Inject(method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        InventoryKeyPressEvent event = new InventoryKeyPressEvent(keyEvent);
        WynnventoryMod.postEvent(event);

        if (event.isCanceled()) cir.cancel();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderRaidAspects(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Screen screen = McUtils.mc().screen;

        if(screen != null && screen.getTitle().getString().equals(RAID_LOBBY_TITLE)) {
            WynnventoryMod.postEvent(new RaidLobbyRenderedEvent(guiGraphics, mouseX, mouseY, partialTick, ci));
        }
    }
}
