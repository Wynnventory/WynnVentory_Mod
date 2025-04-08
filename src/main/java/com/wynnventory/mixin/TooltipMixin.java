package com.wynnventory.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.components.Models;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.gear.type.GearRestrictions;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.GearBoxItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.accessor.ItemQueueAccessor;
import com.wynnventory.api.WynnventoryAPI;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.core.ModInfo;
import com.wynnventory.model.item.TradeMarketItem;
import com.wynnventory.model.item.TradeMarketItemPriceHolder;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import com.wynnventory.util.PriceTooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(AbstractContainerScreen.class)
public abstract class TooltipMixin {

    @Shadow
    protected abstract void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type);

    private static final String MARKET_TITLE = "󏿨";
    private static final String TITLE_TEXT = "Trade Market Price Info";
    private static final long EXPIRE_MINS = 2;
    private static final TradeMarketItemPriceInfo FETCHING = new TradeMarketItemPriceInfo();
    private static final TradeMarketItemPriceInfo UNTRADABLE = new TradeMarketItemPriceInfo();

    private static final Map<String, TradeMarketItemPriceHolder> fetchedPrices = new HashMap<>();
    private static final Map<String, TradeMarketItemPriceHolder> fetchedHistoricPrices = new HashMap<>();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private final ConfigManager config = ConfigManager.getInstance();
    private final ItemQueueAccessor accessor = (ItemQueueAccessor) McUtils.mc().getConnection();
    private static final WynnventoryAPI API = new WynnventoryAPI();

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("RETURN"))
    private void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen == null) return;

        // Get the hovered slot using an accessor (assumed available)
        Slot hoveredSlot = ((AbstractContainerScreenAccessor) this).getHoveredSlot();
        if (hoveredSlot == null || !hoveredSlot.hasItem()) return;

        ItemStack itemStack = hoveredSlot.getItem();
        Optional<WynnItem> maybeWynnItem = Models.Item.getWynnItem(itemStack);

        // If in the market screen, submit the item for market processing
        if (MARKET_TITLE.equals(currentScreen.getTitle().getString())) {
            submitTrademarketItem(itemStack);
        }

        if (config.isShowTooltips() && maybeWynnItem.isPresent()) {
            List<Component> tooltipComponents = new ArrayList<>();
            tooltipComponents.add(Component.literal(TITLE_TEXT).withStyle(ChatFormatting.GOLD));

            WynnItem wynnItem = maybeWynnItem.get();
            switch (wynnItem) {
                case GearItem gearItem -> {
                    processGearTooltip(gearItem.getItemInfo(), tooltipComponents);
                }
                case GearBoxItem gearBoxItem when config.isShowBoxedItemTooltips() -> {
                    processGearBoxTooltip(gearBoxItem, tooltipComponents);
                }
                default -> {
                    return;
                }
            }

            renderPriceInfoTooltip(guiGraphics, mouseX, mouseY, itemStack, tooltipComponents);
        }
    }

    private void processGearTooltip(GearInfo gearInfo, List<Component> tooltipComponents) {
        fetchPricesForGear(gearInfo);
        tooltipComponents.addAll(getTooltipsForGear(gearInfo));
        cleanExpiredPrices(gearInfo.name());
    }

    private void processGearBoxTooltip(GearBoxItem gearBoxItem, List<Component> tooltipComponents) {
        List<GearInfo> possibleGears = Models.Gear.getPossibleGears(gearBoxItem);
        List<TradeMarketItemPriceHolder> priceHolders = new ArrayList<>();
        for (GearInfo gear : possibleGears) {
            fetchPricesForGear(gear);
            priceHolders.add(fetchedPrices.get(gear.name()));
        }

        PriceTooltipHelper.sortTradeMarketPriceHolders(priceHolders);
        for (TradeMarketItemPriceHolder holder : priceHolders) {
            GearInfo gearInfo = holder.getInfo();
            tooltipComponents.addAll(getTooltipsForGear(gearInfo));
            tooltipComponents.add(Component.literal("")); // Spacer
            cleanExpiredPrices(gearInfo.name());
        }
    }

    private void cleanExpiredPrices(String gearName) {
        TradeMarketItemPriceHolder priceHolder = fetchedPrices.get(gearName);
        if (priceHolder != null && priceHolder.isPriceExpired(EXPIRE_MINS)) {
            fetchedPrices.remove(gearName);
        }
        TradeMarketItemPriceHolder historicHolder = fetchedHistoricPrices.get(gearName);
        if (historicHolder != null && historicHolder.isPriceExpired(EXPIRE_MINS)) {
            fetchedHistoricPrices.remove(gearName);
        }
    }

    private void fetchPricesForGear(GearInfo gearInfo) {
        String gearName = gearInfo.name();
        if (!fetchedPrices.containsKey(gearName)) {
            TradeMarketItemPriceHolder priceHolder = new TradeMarketItemPriceHolder(FETCHING, gearInfo);
            fetchedPrices.put(gearName, priceHolder);

            if (gearInfo.metaInfo().restrictions() == GearRestrictions.UNTRADABLE) {
                priceHolder.setPriceInfo(UNTRADABLE);
            } else {
                CompletableFuture.supplyAsync(() -> API.fetchItemPrices(gearName), executorService)
                        .thenAccept(priceHolder::setPriceInfo);
            }
        }

        if (!fetchedHistoricPrices.containsKey(gearName)) {
            TradeMarketItemPriceHolder historicHolder = new TradeMarketItemPriceHolder(FETCHING, gearInfo);
            fetchedHistoricPrices.put(gearName, historicHolder);

            if (gearInfo.metaInfo().restrictions() == GearRestrictions.UNTRADABLE) {
                historicHolder.setPriceInfo(UNTRADABLE);
            } else {
                CompletableFuture.supplyAsync(() -> API.fetchLatestHistoricItemPrice(gearName), executorService)
                        .thenAccept(historicHolder::setPriceInfo);
            }
        }
    }

    private List<Component> getTooltipsForGear(GearInfo gearInfo) {
        TradeMarketItemPriceInfo priceInfo = fetchedPrices.get(gearInfo.name()).getPriceInfo();
        if (priceInfo == FETCHING) {
            return Collections.singletonList(Component.literal("Retrieving price information...").withStyle(ChatFormatting.WHITE));
        } else if (priceInfo == UNTRADABLE) {
            return Collections.singletonList(Component.literal("Item is untradable.").withStyle(ChatFormatting.RED));
        } else {
            TradeMarketItemPriceInfo historicInfo = fetchedHistoricPrices.get(gearInfo.name()).getPriceInfo();
            return PriceTooltipHelper.createPriceTooltip(gearInfo, priceInfo, historicInfo);
        }
    }

    private void submitTrademarketItem(ItemStack item) {
        if (item.getItem() == Items.AIR || item.getItem() == Items.COMPASS || item.getItem() == Items.POTION) {
            return;
        }
        if (McUtils.inventory().items.contains(item)) {
            return;
        }

        TradeMarketItem marketItem = TradeMarketItem.createTradeMarketItem(item);
        if (marketItem != null && !accessor.getQueuedMarketItems().contains(marketItem)) {
            accessor.getQueuedMarketItems().add(marketItem);
            ModInfo.logDebug("Queued item for submit: " + marketItem.getItem().getName());
        }
    }

    @Unique
    private void renderPriceInfoTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, ItemStack item, List<Component> tooltipLines) {
        Font font = McUtils.mc().font;
        Window window = McUtils.window();

        // Ensure the mouse coordinates are within screen bounds
        mouseX = Math.min(mouseX, guiGraphics.guiWidth() - 10);
        mouseY = Math.max(mouseY, 10);

        int guiScaledWidth = window.getGuiScaledWidth();
        int guiScaledHeight = window.getGuiScaledHeight();
        int guiScale = (int) window.getGuiScale();
        int gap = 5 * guiScale;

        // Calculate tooltip dimensions and scale using the helper
        Dimension tooltipDim = PriceTooltipHelper.calculateTooltipDimension(tooltipLines, font);
        int tooltipMaxWidth = mouseX - gap;
        int tooltipMaxHeight = Math.round(guiScaledHeight * 0.8f);
        float scaleFactor = PriceTooltipHelper.calculateScaleFactor(tooltipLines, tooltipMaxHeight, tooltipMaxWidth, 0.4f, 1.0f, font);
        Dimension scaledTooltipDim = new Dimension(Math.round(tooltipDim.width * scaleFactor), Math.round(tooltipDim.height * scaleFactor));

        // Get primary tooltip dimensions (e.g., Minecraft’s default item tooltip)
        Dimension primaryTooltipDim = PriceTooltipHelper.calculateTooltipDimension(Screen.getTooltipFromItem(McUtils.mc(), item), font);

        int spaceToRight = guiScaledWidth - (mouseX + primaryTooltipDim.width + gap);
        int spaceToLeft = mouseX - gap;

        float minY = (scaledTooltipDim.height / 4f) / scaleFactor;
        float maxY = (guiScaledHeight / 2f) / scaleFactor;
        float scaledTooltipY = ((guiScaledHeight / 2f) - (scaledTooltipDim.height / 2f)) / scaleFactor;

        float posX;
        float posY;
        if (config.isAnchorTooltips()) {
            if (spaceToRight > spaceToLeft * 1.3f) {
                posX = guiScaledWidth - scaledTooltipDim.width - (gap / scaleFactor);
            } else {
                posX = 0;
            }
            posY = Math.clamp(scaledTooltipY, minY, maxY);
        } else {
            if (scaledTooltipDim.width > spaceToRight) {
                posX = mouseX - gap - scaledTooltipDim.width;
            } else {
                posX = mouseX + gap + primaryTooltipDim.width;
            }
            if (mouseY + scaledTooltipDim.height > guiScaledHeight) {
                posY = Math.clamp(scaledTooltipY, minY, maxY);
            } else {
                posY = mouseY;
            }
        }

        // Render the tooltip with applied scaling and positioning
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(posX, posY, 0);
        poseStack.scale(scaleFactor, scaleFactor, 1.0f);
        guiGraphics.renderComponentTooltip(font, tooltipLines, 0, 0);
        poseStack.popPose();
    }
}