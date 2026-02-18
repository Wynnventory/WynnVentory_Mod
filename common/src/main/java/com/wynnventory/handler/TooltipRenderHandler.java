package com.wynnventory.handler;

import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.ItemTooltipRenderEvent;
import com.wynnventory.api.service.RewardService;
import com.wynnventory.core.config.ModConfig;
import com.wynnventory.core.queue.QueueScheduler;
import com.wynnventory.core.tooltip.PriceTooltipBuilder;
import com.wynnventory.core.tooltip.PriceTooltipFactory;
import com.wynnventory.events.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.container.PartyFinderContainer;
import com.wynnventory.model.item.trademarket.TrademarketListing;
import com.wynnventory.model.reward.RewardPool;
import com.wynnventory.model.reward.RewardType;
import com.wynnventory.util.AspectTooltipHelper;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.RenderUtils;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Unique;

public final class TooltipRenderHandler {
    private ItemStack lastItem;
    private final PriceTooltipFactory tooltipFactory = new PriceTooltipFactory(new PriceTooltipBuilder());

    @SubscribeEvent
    public void onTrademarketTooltipRendered(TrademarketTooltipRenderedEvent event) {
        ItemStack hoveredItem = getItemFromSlot(event.getItemSlot());
        if (hoveredItem == null) return;

        TrademarketListing listing = TrademarketListing.from(hoveredItem);
        if (listing == null) return;

        QueueScheduler.TRADEMARKET_QUEUE.addItem(listing);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltipRendered(ItemTooltipRenderEvent.Pre event) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null
                && PartyFinderContainer.matchesTitle(screen.getTitle().getString())) {
            renderPartyFinderAspects(
                    event.getGuiGraphics(),
                    event.getMouseX(),
                    event.getMouseY(),
                    event.getItemStack(),
                    event.getTooltips());
        }

        if (ModConfig.getInstance().getTooltipSettings().isShowTooltips()) {
            renderTooltip(
                    event.getGuiGraphics(),
                    event.getMouseX(),
                    event.getMouseY(),
                    event.getItemStack(),
                    event.getTooltips());
        }
    }

    private ItemStack getItemFromSlot(Slot slot) {
        if (slot.container instanceof Inventory) return null;

        ItemStack hoveredItem = slot.getItem();
        if (lastItem == hoveredItem) return hoveredItem;

        lastItem = hoveredItem;

        return hoveredItem;
    }

    @Unique
    private void renderTooltip(
            GuiGraphics guiGraphics, int mouseX, int mouseY, ItemStack stack, List<Component> vanillaLines) {
        if (vanillaLines == null || vanillaLines.isEmpty()) return;

        List<Component> priceLines = tooltipFactory.getPriceTooltip(stack);
        if (priceLines.isEmpty()) return;

        drawTooltip(guiGraphics, mouseX, mouseY, vanillaLines, priceLines);
    }

    @Unique
    private void renderPartyFinderAspects(
            GuiGraphics guiGraphics, int mouseX, int mouseY, ItemStack stack, List<Component> vanillaLines) {
        StyledText originalName = ItemStackUtils.getWynntilsOriginalName(stack);
        if (originalName == null) return;

        String name = originalName.getStringWithoutFormatting();
        RewardPool pool = RewardPool.fromFullName(name);
        if (pool == null || pool.getType() != RewardType.RAID) return;

        RewardService.INSTANCE.getRaidPools().thenAccept(pools -> pools.stream()
                .filter(p -> p.getRewardPool().equals(pool))
                .findFirst()
                .ifPresent(p -> {
                    List<Component> tooltipLines = AspectTooltipHelper.buildLines(p);
                    if (tooltipLines.isEmpty()) return;

                    drawTooltip(guiGraphics, mouseX, mouseY, vanillaLines, tooltipLines);
                }));
    }

    private static void drawTooltip(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            List<Component> vanillaLines,
            List<Component> customLines) {
        List<ClientTooltipComponent> vanillaComponents = RenderUtils.toClientComponents(vanillaLines, Optional.empty());
        List<ClientTooltipComponent> customComponents = RenderUtils.toClientComponents(customLines, Optional.empty());

        Vector2i tooltipCoords =
                RenderUtils.calculateTooltipCoords(mouseX, mouseY, vanillaComponents, customComponents);
        ClientTooltipPositioner fixed = new RenderUtils.FixedTooltipPositioner(tooltipCoords.x, tooltipCoords.y);

        float scale = RenderUtils.getScaleFactor(customComponents);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.renderTooltip(Minecraft.getInstance().font, customComponents, mouseX, mouseY, fixed, null);
        guiGraphics.pose().popMatrix();
    }
}
