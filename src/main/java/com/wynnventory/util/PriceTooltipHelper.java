package com.wynnventory.util;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.models.emeralds.type.EmeraldUnits;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.config.EmeraldDisplayOption;
import com.wynnventory.model.item.TradeMarketItemPriceHolder;
import com.wynnventory.model.item.TradeMarketItemPriceInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

/**
 * Helper class for calculating tooltip dimensions, scale factors, and for formatting price tooltip lines.
 */
public class PriceTooltipHelper {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    private static final EmeraldPrice EMERALD_PRICE = new EmeraldPrice();

    private PriceTooltipHelper() {}

    /**
     * Calculates the dimensions of the tooltip based on its lines.
     *
     * @param tooltipLines the list of tooltip lines
     * @param font         the font used to render the tooltip
     * @return the dimension (width and height) of the tooltip
     */
    public static Dimension calculateTooltipDimension(List<Component> tooltipLines, Font font) {
        int width = tooltipLines.stream().mapToInt(font::width).max().orElse(0);
        int height = tooltipLines.size() * font.lineHeight;
        return new Dimension(width, height);
    }

    /**
     * Calculates a scale factor for the tooltip so it fits within the maximum allowed dimensions.
     *
     * @param tooltipLines the tooltip lines
     * @param maxHeight    the maximum height available
     * @param maxWidth     the maximum width available
     * @param minScale     the minimum allowed scale
     * @param maxScale     the maximum allowed scale
     * @param font         the font used for the tooltip
     * @return the calculated scale factor
     */
    public static float calculateScaleFactor(List<Component> tooltipLines, int maxHeight, int maxWidth,
                                             float minScale, float maxScale, Font font) {
        Dimension dim = calculateTooltipDimension(tooltipLines, font);
        float heightScale = maxHeight / (float) dim.height;
        float scaleFactor = Math.clamp(heightScale, minScale, maxScale);
        int scaledWidth = Math.round(dim.width * scaleFactor);
        if (scaledWidth > maxWidth) {
            float widthScale = (float) maxWidth / dim.width;
            scaleFactor = Math.clamp(widthScale, minScale, maxScale);
        }
        return scaleFactor;
    }

    /**
     * Creates a price tooltip for a given gear.
     *
     * @param info         the gear info
     * @param priceInfo    the current price info
     * @param historicInfo the historic price info (may be null)
     * @return the list of components representing the tooltip lines
     */
    public static List<Component> createPriceTooltip(GearInfo info, TradeMarketItemPriceInfo priceInfo, TradeMarketItemPriceInfo historicInfo) {
        ConfigManager config = ConfigManager.getInstance();
        List<Component> tooltipLines = new java.util.ArrayList<>();
        tooltipLines.add(formatText(info.name(), info.tier().getChatFormatting()));

        if (priceInfo == null) {
            tooltipLines.add(formatText("No price data available yet!", ChatFormatting.RED));
        } else {
            boolean showFluctuation = config.isShowPriceFluctuation() && historicInfo != null;
            addPriceLine(tooltipLines, "Max: ", priceInfo.getHighestPrice(), showFluctuation, historicInfo != null ? historicInfo.getHighestPrice() : 0);
            addPriceLine(tooltipLines, "Min: ", priceInfo.getLowestPrice(), showFluctuation, historicInfo != null ? historicInfo.getLowestPrice() : 0);
            addPriceLine(tooltipLines, "Avg: ", priceInfo.getAveragePrice(), showFluctuation, historicInfo != null ? historicInfo.getAveragePrice() : 0);
            addPriceLine(tooltipLines, "Avg 80%: ", priceInfo.getAverage80Price(), showFluctuation, historicInfo != null ? historicInfo.getAverage80Price() : 0);
            addPriceLine(tooltipLines, "Unidentified Avg: ", priceInfo.getUnidentifiedAveragePrice(), showFluctuation, historicInfo != null ? historicInfo.getUnidentifiedAveragePrice() : 0);
            addPriceLine(tooltipLines, "Unidentified Avg 80%: ", priceInfo.getUnidentifiedAverage80Price(), showFluctuation, historicInfo != null ? historicInfo.getUnidentifiedAverage80Price() : 0);
        }

        return tooltipLines;
    }

    /**
     * Adds a price line to the tooltip if the price is valid and enabled in config.
     *
     * @param tooltipLines the list to add the line to
     * @param label        the label for the price
     * @param price        the price value
     * @param showFluct    whether to show the fluctuation value
     * @param historicPrice the historic price to calculate fluctuation against
     */
    public static void addPriceLine(List<Component> tooltipLines, String label, int price, boolean showFluct, int historicPrice) {
        ConfigManager config = ConfigManager.getInstance();
        boolean shouldShow = switch (label) {
            case "Max: " -> config.isShowMaxPrice();
            case "Min: " -> config.isShowMinPrice();
            case "Avg: " -> config.isShowAveragePrice();
            case "Avg 80%: " -> config.isShowAverage80Price();
            case "Unidentified Avg: " -> config.isShowUnidAveragePrice();
            case "Unidentified Avg 80%: " -> config.isShowUnidAverage80Price();
            default -> false;
        };

        if (price > 0 && shouldShow) {
            if (showFluct) {
                float fluctuation = calcPriceDiff(price, historicPrice);
                tooltipLines.add(formatPriceWithFluctuation(label, price, fluctuation));
            } else {
                tooltipLines.add(formatPrice(label, price));
            }
        }
    }

    /**
     * Formats a price value into a tooltip component.
     */
    public static MutableComponent formatPrice(String label, int price) {
        ConfigManager config = ConfigManager.getInstance();
        EmeraldDisplayOption priceFormat = config.getPriceFormat();
        MutableComponent priceComponent = Component.literal(label).withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));

        int color = (config.getColorSettings().isShowColors() &&
                price >= config.getColorSettings().getColorMinPrice())
                ? config.getColorSettings().getHighlightColor()
                : ChatFormatting.GRAY.getColor();

        if (price > 0) {
            String formattedPrice = NUMBER_FORMAT.format(price) + EmeraldUnits.EMERALD.getSymbol();
            String formattedEmeralds = EMERALD_PRICE.getFormattedString(price, false);
            if (priceFormat == EmeraldDisplayOption.EMERALDS) {
                priceComponent.append(Component.literal(formattedPrice)
                        .withStyle(Style.EMPTY.withColor(color)));
            } else if (priceFormat == EmeraldDisplayOption.FORMATTED) {
                priceComponent.append(Component.literal(formattedEmeralds)
                        .withStyle(Style.EMPTY.withColor(color)));
            } else {
                priceComponent.append(Component.literal(formattedPrice)
                                .withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)))
                        .append(Component.literal(" (" + formattedEmeralds + ")")
                                .withStyle(Style.EMPTY.withColor(color)));
            }
        }
        return priceComponent;
    }

    /**
     * Formats a price value with a fluctuation percentage.
     */
    public static MutableComponent formatPriceWithFluctuation(String label, int price, float fluctuation) {
        return price > 0
                ? formatPrice(label, price)
                .append(Component.literal(" "))
                .append(formatPriceFluctuation(fluctuation))
                : Component.literal("");
    }

    /**
     * Formats a plain text component with the given color.
     */
    public static MutableComponent formatText(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(Style.EMPTY.withColor(color));
    }

    /**
     * Formats the fluctuation value.
     */
    public static MutableComponent formatPriceFluctuation(float fluctuation) {
        Style style = fluctuation < 0 ? Style.EMPTY.withColor(ChatFormatting.RED)
                : fluctuation > 0 ? Style.EMPTY.withColor(ChatFormatting.GREEN)
                : Style.EMPTY.withColor(ChatFormatting.GRAY);
        String formattedValue = fluctuation < 0
                ? String.format("%.1f", fluctuation) + "%"
                : "+" + String.format("%.1f", fluctuation) + "%";
        return Component.literal(formattedValue).withStyle(style);
    }

    /**
     * Calculates the price difference in percentage.
     */
    public static float calcPriceDiff(float newPrice, float oldPrice) {
        return oldPrice == 0 ? 0 : ((newPrice - oldPrice) / oldPrice) * 100;
    }

    /**
     * Sorts a list of TradeMarketItemPriceHolder objects based on price groups and values.
     */
    public static void sortTradeMarketPriceHolders(List<TradeMarketItemPriceHolder> holders) {
        holders.sort((h1, h2) -> {
            TradeMarketItemPriceInfo p1 = h1.getPriceInfo();
            TradeMarketItemPriceInfo p2 = h2.getPriceInfo();
            int group1 = getPriceGroup(p1);
            int group2 = getPriceGroup(p2);
            int groupComparison = Integer.compare(group1, group2);
            if (groupComparison != 0) {
                return groupComparison;
            }
            if (group1 == 0) {
                return Double.compare(p2.getUnidentifiedAverage80Price(), p1.getUnidentifiedAverage80Price());
            } else if (group1 == 1) {
                return Double.compare(p2.getAverage80Price(), p1.getAverage80Price());
            } else {
                return 0;
            }
        });
    }

    /**
     * Determines the price group for sorting.
     */
    private static int getPriceGroup(TradeMarketItemPriceInfo priceInfo) {
        if (priceInfo == null) {
            return 2;
        }
        return priceInfo.getUnidentifiedAverage80Price() != 0 ? 0 : 1;
    }

    @Unique
    public static void renderPriceInfoTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, ItemStack item, List<Component> tooltipLines, boolean anchored) {
        Font font = McUtils.mc().font;
        Window window = McUtils.window();

        // Clamp the mouse coordinates to avoid going off-screen.
        mouseX = Math.min(mouseX, guiGraphics.guiWidth() - 10);
        mouseY = Math.max(mouseY, 10);

        int guiScaledWidth = window.getGuiScaledWidth();
        int guiScaledHeight = window.getGuiScaledHeight();
        int guiScale = (int) window.getGuiScale();
        int gap = 5 * guiScale;

        // Retrieve the primary tooltip dimensions (which have priority).
        Dimension primaryTooltipDim = PriceTooltipHelper.calculateTooltipDimension(
                Screen.getTooltipFromItem(McUtils.mc(), item), font);

        // Determine the available horizontal space on each side.
        int availableLeft = mouseX - gap;
        int availableRight = guiScaledWidth - (mouseX + primaryTooltipDim.width + gap);

        // Decide whether to render the custom tooltip to the right or left.
        // For anchored tooltips, use a biased threshold.
        boolean placeRight;
        if (anchored) {
            placeRight = availableRight > availableLeft * 1.3f;
        } else {
            placeRight = availableRight >= availableLeft;
        }

        // Define the maximum allowed width based on chosen side.
        int tooltipMaxWidth = placeRight ? availableRight : availableLeft;
        int tooltipMaxHeight = Math.round(guiScaledHeight * 0.8f);

        // Calculate the scale factor using the custom tooltip dimensions and the available space.
        float scaleFactor = PriceTooltipHelper.calculateScaleFactor(tooltipLines, tooltipMaxHeight, tooltipMaxWidth, 0.4f, 1.0f, font);
        Dimension tooltipDim = PriceTooltipHelper.calculateTooltipDimension(tooltipLines, font);
        Dimension scaledTooltipDim = new Dimension(
                Math.round(tooltipDim.width * scaleFactor),
                Math.round(tooltipDim.height * scaleFactor)
        );

        // Calculate horizontal position (posX) based on the chosen side.
        float posX;
        if (anchored) {
            // Anchored tooltips attach to the screen edge.
            if (placeRight) {
                posX = guiScaledWidth - scaledTooltipDim.width - (gap / scaleFactor);
            } else {
                posX = 0;
            }
        } else {
            // In non-anchored mode, position relative to the mouse and the primary tooltip.
            if (placeRight) {
                posX = mouseX + gap + primaryTooltipDim.width;
            } else {
                posX = mouseX - gap - scaledTooltipDim.width;
            }
        }

        // Calculate vertical position (posY).
        float posY;
        if (anchored) {
            // Center vertically on the screen.
            posY = ((guiScaledHeight / 2f) - (scaledTooltipDim.height / 2f)) / scaleFactor;
            // Clamp within the screen's vertical bounds (using a margin based on the gap).
            posY = Math.clamp(posY, gap / (float) scaleFactor, (guiScaledHeight - scaledTooltipDim.height - gap) / (float) scaleFactor);
        } else {
            // In non-anchored mode, use the mouseY position unless it makes the tooltip go off-screen.
            if (mouseY + scaledTooltipDim.height > guiScaledHeight) {
                posY = guiScaledHeight - scaledTooltipDim.height - gap;
            } else {
                posY = mouseY;
            }
        }

        // Render the custom tooltip with the calculated scale and positions.
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(posX, posY, 0);
        poseStack.scale(scaleFactor, scaleFactor, 1.0f);
        guiGraphics.renderComponentTooltip(font, tooltipLines, 0, 0);
        poseStack.popPose();
    }
}
