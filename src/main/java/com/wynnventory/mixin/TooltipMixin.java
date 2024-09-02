package com.wynnventory.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.components.Models;
import com.wynntils.models.emeralds.type.EmeraldUnits;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.gear.type.GearRestrictions;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.GearBoxItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.utils.mc.ComponentUtils;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.WynnventoryMod;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.model.item.TradeMarketItemPriceHolder;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import com.wynnventory.util.EmeraldPrice;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(AbstractContainerScreen.class)
public abstract class TooltipMixin {

    @Shadow protected abstract void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type);

    private static final String TITLE_TEXT = "Trade Market Price Info";
    private static final long EXPIRE_MINS = 2;
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    private static final EmeraldPrice EMERALD_PRICE = new EmeraldPrice();
    private static final WynnventoryAPI API = new WynnventoryAPI();

    private static final TradeMarketItemPriceInfo FETCHING = new TradeMarketItemPriceInfo();
    private static final TradeMarketItemPriceInfo UNTRADABLE = new TradeMarketItemPriceInfo();
    private static HashMap<String, TradeMarketItemPriceHolder> fetchedPrices = new HashMap<>();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("RETURN"))
    private void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if(!WynnventoryMod.SHOW_TOOLTIP) { return; }
        Slot hoveredSlot = ((AbstractContainerScreenAccessor) this).getHoveredSlot();
        if (hoveredSlot == null || !hoveredSlot.hasItem()) return;

        ItemStack item = hoveredSlot.getItem();
        Optional<WynnItem> wynnItemOptional = Models.Item.getWynnItem(item);

        if(wynnItemOptional.isPresent()) {
            WynnItem wynnItem = wynnItemOptional.get();

            List<Component> tooltips = new ArrayList<>();
            if(wynnItem instanceof GearItem gearItem) {
                tooltips.add(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

                fetchPricesForGear(gearItem.getItemInfo());

                tooltips.addAll(getTooltipsForGear(gearItem.getItemInfo()));

                // remove price if expired
                if (fetchedPrices.get(gearItem.getName()).isPriceExpired(EXPIRE_MINS)) fetchedPrices.remove(gearItem.getName());
            } else if(wynnItem instanceof GearBoxItem gearBoxItem) {
                tooltips.add(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

                List<GearInfo> possibleGear = Models.Gear.getPossibleGears(gearBoxItem);
                for(GearInfo gear : possibleGear) {
                    fetchPricesForGear(gear);

                    tooltips.addAll(getTooltipsForGear(gear));
                    tooltips.add(Component.literal(""));

                    // remove price if expired
                    if (fetchedPrices.get(gear.name()).isPriceExpired(EXPIRE_MINS)) fetchedPrices.remove(gear.name());
                }
            }

            renderPriceInfoTooltip(guiGraphics, mouseX, mouseY, item, tooltips);
        }
    }

    @Unique
    private void renderPriceInfoTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, ItemStack item, List<Component> tooltipLines) {
        Font font = McUtils.mc().font;

        mouseX = Math.min(mouseX, guiGraphics.guiWidth() - 10);
        mouseY = Math.max(mouseY, 10);
        int guiScaledWidth = McUtils.window().getGuiScaledWidth();
        int guiScaledHeight = McUtils.window().getGuiScaledHeight();
        int guiScaleFactor = (int) Minecraft.getInstance().getWindow().getGuiScale();
        int gap = 5 * guiScaleFactor;

        // Calculate the height of the tooltip
        int tooltipHeight = tooltipLines.size() * font.lineHeight;

        // Calculate maximum allowed height based on screen size
        int maxTooltipHeight = guiScaledHeight - (gap * 4); // 20px padding for top and bottom

        // Calculate the scaling factor
        float scaleFactor = tooltipHeight > maxTooltipHeight ? (float) maxTooltipHeight / tooltipHeight : 1.0f;

        // Apply scaling to the PoseStack
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, (guiScaledHeight / 2f) - ((tooltipHeight * scaleFactor) / 2f), 1);
        if (scaleFactor < 1.0f) {
            poseStack.scale(scaleFactor, scaleFactor, 1.0f);
        }

        List<Component> primaryTooltip = Screen.getTooltipFromItem(McUtils.mc(), item);
        int primaryTooltipWidth = primaryTooltip.stream()
                .map(component -> McUtils.mc().font.width(component))
                .max(Integer::compareTo)
                .orElse(0);

        int priceTooltipWidth = tooltipLines.stream()
                .map(component -> McUtils.mc().font.width(component))
                .max(Integer::compareTo)
                .orElse(0);
        priceTooltipWidth+=gap;

        int spaceToRight = guiScaledWidth - (mouseX + primaryTooltipWidth + gap);

        try {
            if (priceTooltipWidth > spaceToRight) {
                // Render on left
                guiGraphics.renderComponentTooltip(
                        font, tooltipLines, 0, 0);
            } else {
                // Render on right
                guiGraphics.renderComponentTooltip(
                        font, tooltipLines, 0, 0);
            }
        } catch (Exception e) {
            WynnventoryMod.error("Failed to render price tooltip for " + item.getDisplayName());
        }
        poseStack.popPose();
    }

    @Unique
    private List<Component> createPriceTooltip(GearInfo info, TradeMarketItemPriceInfo priceInfo) {
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(formatText(info.name(), info.tier().getChatFormatting()));
        if (priceInfo == null) {
            tooltipLines.add(formatText("No price data available yet!", ChatFormatting.RED));
        } else {
            if (priceInfo.getHighestPrice() > 0) {
                tooltipLines.add(formatPrice("Max: ", priceInfo.getHighestPrice()));
            }
            if (priceInfo.getLowestPrice() > 0) {
                tooltipLines.add(formatPrice("Min: ", priceInfo.getLowestPrice()));
            }
            if (priceInfo.getAveragePrice() > 0.0) {
                tooltipLines.add(formatPrice("Avg: ", priceInfo.getAveragePrice()));
            }
            if (priceInfo.getUnidentifiedAveragePrice() != null) {
                tooltipLines.add(formatPrice("Unidentified Avg: ", priceInfo.getUnidentifiedAveragePrice().intValue()));
            }
        }
        return tooltipLines;
    }

    private void fetchPricesForGear(GearInfo info) {
        if (!fetchedPrices.containsKey(info.name())) {
            TradeMarketItemPriceHolder requestedPrice = new TradeMarketItemPriceHolder(FETCHING, info);
            fetchedPrices.put(info.name(), requestedPrice);

            if (info.metaInfo().restrictions() == GearRestrictions.UNTRADABLE) {
                // ignore untradable
                requestedPrice.setPriceInfo(UNTRADABLE);
            } else {
                // fetch price async
                CompletableFuture.supplyAsync(() -> API.fetchItemPrices(info.name()), executorService)
                        .thenAccept(requestedPrice::setPriceInfo);
            }
        }
    }

    private List<Component> getTooltipsForGear(GearInfo info) {
        TradeMarketItemPriceInfo price = fetchedPrices.get(info.name()).getPriceInfo();
        List<Component> tooltips = new ArrayList<>();

        if (price == FETCHING) { // Display retrieving info
            tooltips.add(formatText("Retrieving price information...", ChatFormatting.WHITE));
        } else if (price == UNTRADABLE) { // Display untradable
            tooltips.add(formatText("Item is untradable.", ChatFormatting.RED));
        } else { // Display fetched price
            tooltips = createPriceTooltip(info, price);
        }

        return tooltips;
    }

    @Unique
    private static MutableComponent formatPrice(String label, int price) {
        if (price > 0) {
            String formattedPrice = NUMBER_FORMAT.format(price) + EmeraldUnits.EMERALD.getSymbol();
            String formattedEmeralds = EMERALD_PRICE.getFormattedString(price, false);
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
