package com.wynnventory.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.components.Models;
import com.wynntils.models.emeralds.EmeraldModel;
import com.wynntils.models.emeralds.type.EmeraldUnits;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public class TooltipMixin {

    private static final String TITLE_TEXT = "Trade Market Price Info";
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    private static final EmeraldModel EMERALD_MODEL = new EmeraldModel();
    private static final WynnventoryAPI API = new WynnventoryAPI();

    private static GearItem lastHoveredItem;
    private static TradeMarketItemPriceInfo lastHoveredItemPriceInfo;

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("RETURN"))
    private void renderSecondaryTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if(!Screen.hasAltDown()) { return; }
        // Access hoveredSlot using the accessor
        Slot hoveredSlot = ((AbstractContainerScreenAccessor) this).getHoveredSlot();

        if (hoveredSlot == null || !hoveredSlot.hasItem()) {
            return;
        }

        ItemStack item = hoveredSlot.getItem();
        Optional<GearItem> gearItemOptional = Models.Item.asWynnItem(item, GearItem.class);
        gearItemOptional.ifPresent(gearItem -> {
            if (!gearItem.equals(lastHoveredItem)) {
                lastHoveredItem = gearItem;
                lastHoveredItemPriceInfo = API.fetchItemPrices(item);
            }

            List<Component> priceTooltips = createTooltip(lastHoveredItemPriceInfo);
            renderPriceInfoTooltip(guiGraphics, mouseX, mouseY, item, priceTooltips);
        });
    }

    @Unique
    private void renderPriceInfoTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, ItemStack item, List<Component> tooltipLines) {
        mouseX = Math.min(mouseX, guiGraphics.guiWidth() - 10);
        mouseY = Math.max(mouseY, 10);

        final PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(0, 0, 300);

        int toBeRenderedWidth = Screen.getTooltipFromItem(McUtils.mc(), item).stream()
                .map(component -> McUtils.mc().font.width(component))
                .max(Integer::compareTo)
                .orElse(0);

        int hoveredWidth = Screen.getTooltipFromItem(McUtils.mc(), item).stream()
                .map(component -> McUtils.mc().font.width(component))
                .max(Integer::compareTo)
                .orElse(0);

        Font font = Minecraft.getInstance().font;
        if (mouseX + toBeRenderedWidth + hoveredWidth > Minecraft.getInstance().getWindow().getScreenWidth()) {
            guiGraphics.renderComponentTooltip(
                    font, tooltipLines, mouseX - toBeRenderedWidth - 10, mouseY);
        } else {
            guiGraphics.renderComponentTooltip(
                    font, tooltipLines, mouseX + hoveredWidth + 10, mouseY);
        }
        poseStack.popPose();
    }

    @Unique
    private List<Component> createTooltip(TradeMarketItemPriceInfo priceInfo) {
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

        if (priceInfo == null) {
            tooltipLines.add(formatText("No price data available yet!", ChatFormatting.RED));
            return tooltipLines;
        } else {
            tooltipLines.add(formatPrice("Max: ", priceInfo.getHighestPrice()));
            tooltipLines.add(formatPrice("Min:", priceInfo.getAveragePrice()));
            tooltipLines.add(formatPrice("Avg:", priceInfo.getAveragePrice()));
            if (priceInfo.getUnidentifiedAveragePrice() != null) {
                tooltipLines.add(formatPrice("Unidentified Avg: ", priceInfo.getUnidentifiedAveragePrice().intValue()));
            }
            return tooltipLines;
        }
    }

    @Unique
    private static MutableComponent formatPrice(String label, int price) {
        if (price > 0) {
            String formattedPrice = NUMBER_FORMAT.format(price) + EmeraldUnits.EMERALD.getSymbol();
            String formattedEmeralds = EMERALD_MODEL.getFormattedString(price, false);
            return Component.literal(label + formattedPrice)
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
                    .append(Component.literal(" (" + formattedEmeralds + ")")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
        return null;
    }

    @Unique
    private static MutableComponent formatText(String text, ChatFormatting color) {
            return Component.literal(text)
                    .withStyle(Style.EMPTY.withColor(color));
    }
}
