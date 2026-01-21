package com.wynnventory.handler;

import com.wynntils.mc.event.ItemTooltipRenderEvent;
import com.wynnventory.core.WynnventoryMod;
import com.wynnventory.data.CalculatedItemPriceDictionary;
import com.wynnventory.event.TrademarketTooltipRenderedEvent;
import com.wynnventory.model.item.simple.SimpleGearItem;
import com.wynnventory.model.item.simple.SimpleItem;
import com.wynnventory.model.item.simple.SimpleTierItem;
import com.wynnventory.model.item.trademarket.TradeMarketListing;
import com.wynnventory.queue.QueueManager;
import com.wynnventory.util.FixedTooltipPositioner;
import com.wynnventory.util.ItemStackUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Vector2ic;

import java.util.List;
import java.util.Optional;

public final class TooltipRenderHandler {
    private ItemStack lastItem;

    @SubscribeEvent
    public void onTrademarketTooltipRendered(TrademarketTooltipRenderedEvent event) {
        Slot hoveredItemSlot = event.getItemSlot();
        if (hoveredItemSlot.container instanceof Inventory) return;

        ItemStack hoveredItem = hoveredItemSlot.getItem();
        if (lastItem == hoveredItem) return;
        lastItem = hoveredItem;

        TradeMarketListing listing = TradeMarketListing.from(hoveredItem);
        if (listing == null) return;

        QueueManager.TRADEMARKET_QUEUE.addItem(listing);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltipRendered(ItemTooltipRenderEvent.Pre event) {
        SimpleItem simpleItem = ItemStackUtils.toSimpleItem(event.getItemStack());


        //TODO: Refactor this (new class?)
        switch (simpleItem) {
            case SimpleGearItem gearItem -> CalculatedItemPriceDictionary.INSTANCE.getItem(gearItem.getName(), gearItem.isShiny());
            case SimpleTierItem tierItem -> CalculatedItemPriceDictionary.INSTANCE.getItem(tierItem.getName(), tierItem.getTier());
            case SimpleItem item -> CalculatedItemPriceDictionary.INSTANCE.getItem(item.getName());
            case null -> WynnventoryMod.logError("Wait a minute... Who are you!?");
        }

        renderTooltip(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getItemStack(), event.getTooltips());
    }

    private void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, ItemStack itemStack, List<Component> tooltipLines) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        Optional<TooltipComponent> tooltipImage = itemStack.getTooltipImage();
        Identifier background = itemStack.get(DataComponents.TOOLTIP_STYLE);

        // ----------------------------
        // 1) Build the same "client tooltip components" vanilla uses for sizing/position
        // ----------------------------
        if (tooltipLines == null || tooltipLines.isEmpty()) return;

        List<ClientTooltipComponent> vanillaComponents = toClientComponents(tooltipLines, tooltipImage);

        int vanillaW = tooltipWidth(vanillaComponents, font);
        int vanillaH = tooltipHeight(vanillaComponents, font);

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        Vector2ic vanillaPos = DefaultTooltipPositioner.INSTANCE.positionTooltip(
                screenW, screenH, mouseX, mouseY, vanillaW, vanillaH
        );

        int vanillaX = vanillaPos.x();
        int vanillaY = vanillaPos.y();

        // ----------------------------
        // 2) Create your "price tooltip"
        // ----------------------------
        List<Component> priceLines = List.of(
                Component.literal("Trade Market Price Info").withStyle(ChatFormatting.GOLD),
                Component.literal("Price: 1eb")
        );
        List<ClientTooltipComponent> priceComponents = toClientComponents(priceLines, Optional.empty());

        int priceW = tooltipWidth(priceComponents, font);
        int priceH = tooltipHeight(priceComponents, font);

        // ----------------------------
        // 3) Position to the right of the vanilla tooltip (+gap), flip/clamp if needed
        // ----------------------------
        final int gap = 6;

        int priceX = vanillaX + vanillaW + gap;
        int priceY = vanillaY;

        // Flip to left if overflowing right edge
        if (priceX + priceW > screenW - 4) {
            priceX = vanillaX - gap - priceW;
        }

        // Clamp inside screen bounds
        priceX = clamp(priceX, 4, screenW - priceW - 4);
        priceY = clamp(priceY, 4, screenH - priceH - 4);

        // ----------------------------
        // 4) Render our tooltip at a fixed position
        // ----------------------------
        ClientTooltipPositioner fixed = new FixedTooltipPositioner(priceX, priceY);

        graphics.renderTooltip(font, priceComponents, priceX, priceY, fixed, background);
    }

    private List<ClientTooltipComponent> toClientComponents(List<Component> lines, Optional<TooltipComponent> tooltipImage) {
        List<ClientTooltipComponent> list = lines.stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .collect(Util.toMutableList());

        tooltipImage.ifPresent(img ->
                list.add(list.isEmpty() ? 0 : 1, ClientTooltipComponent.create(img))
        );

        return list;
    }

    private int tooltipWidth(List<ClientTooltipComponent> comps, Font font) {
        int w = 0;
        for (ClientTooltipComponent c : comps) {
            w = Math.max(w, c.getWidth(font));
        }
        return w;
    }

    private int tooltipHeight(List<ClientTooltipComponent> comps, Font font) {
        int h = 0;
        for (ClientTooltipComponent c : comps) {
            h += c.getHeight(font);
        }
        return h;
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}