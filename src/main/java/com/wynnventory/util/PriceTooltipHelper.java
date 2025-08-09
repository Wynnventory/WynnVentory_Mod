package com.wynnventory.util;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.models.emeralds.type.EmeraldUnits;
import com.wynntils.models.gear.type.GearType;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.config.EmeraldDisplayOption;
import com.wynnventory.model.item.trademarket.TradeMarketItemPriceHolder;
import com.wynnventory.model.item.trademarket.TradeMarketItemPriceInfo;
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

public class PriceTooltipHelper {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    private static final EmeraldPrice EMERALD_PRICE = new EmeraldPrice();

    private PriceTooltipHelper() {}

    public static Dimension calculateTooltipDimension(List<Component> tooltipLines, Font font) {
        int width = tooltipLines.stream().mapToInt(font::width).max().orElse(0);
        int height = tooltipLines.size() * font.lineHeight;
        return new Dimension(width, height);
    }

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

    public static List<Component> createPriceTooltip(TradeMarketItemPriceInfo priceInfo, TradeMarketItemPriceInfo historicInfo, String itemName, ChatFormatting color) {
        ConfigManager config = ConfigManager.getInstance();
        List<Component> tooltipLines = new java.util.ArrayList<>();
        tooltipLines.add(formatText(itemName, color));

        if (!isPriceInfoValid(priceInfo)) {
            tooltipLines.add(formatText("No price data available yet!", ChatFormatting.RED));
        } else {
            addPriceLine(tooltipLines, "Max: ",                     priceInfo.getHighestPrice(),                config.isShowPriceFluctuation(), isPriceInfoValid(historicInfo) ? historicInfo.getHighestPrice()                : 0);
            addPriceLine(tooltipLines, "Min: ",                     priceInfo.getLowestPrice(),                 config.isShowPriceFluctuation(), isPriceInfoValid(historicInfo) ? historicInfo.getLowestPrice()                 : 0);
            addPriceLine(tooltipLines, "Avg: ",                     priceInfo.getAveragePrice(),                config.isShowPriceFluctuation(), isPriceInfoValid(historicInfo) ? historicInfo.getAveragePrice()                : 0);
            addPriceLine(tooltipLines, "Avg 80%: ",                 priceInfo.getAverage80Price(),              config.isShowPriceFluctuation(), isPriceInfoValid(historicInfo) ? historicInfo.getAverage80Price()              : 0);
            addPriceLine(tooltipLines, "Unidentified Avg: ",        priceInfo.getUnidentifiedAveragePrice(),    config.isShowPriceFluctuation(), isPriceInfoValid(historicInfo) ? historicInfo.getUnidentifiedAveragePrice()    : 0);
            addPriceLine(tooltipLines, "Unidentified Avg 80%: ",    priceInfo.getUnidentifiedAverage80Price(),  config.isShowPriceFluctuation(), isPriceInfoValid(historicInfo) ? historicInfo.getUnidentifiedAverage80Price()  : 0);
        }

        return tooltipLines;
    }

    public static void addPriceLine(List<Component> tooltipLines, String label, int price, boolean showFluct, int historicPrice) {
        boolean shouldShow = isShouldShow(label);

        if (price > 0 && shouldShow) {
            if (showFluct) {
                float fluctuation = calcPriceDiff(price, historicPrice);
                tooltipLines.add(formatPriceWithFluctuation(label, price, fluctuation));
            } else {
                tooltipLines.add(formatPrice(label, price));
            }
        }
    }

    private static boolean isShouldShow(String label) {
        ConfigManager config = ConfigManager.getInstance();
        return switch (label) {
            case "Max: " -> config.isShowMaxPrice();
            case "Min: " -> config.isShowMinPrice();
            case "Avg: " -> config.isShowAveragePrice();
            case "Avg 80%: " -> config.isShowAverage80Price();
            case "Unidentified Avg: " -> config.isShowUnidAveragePrice();
            case "Unidentified Avg 80%: " -> config.isShowUnidAverage80Price();
            default -> false;
        };
    }

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
            switch (priceFormat) {
                case EmeraldDisplayOption.EMERALDS -> priceComponent.append(Component.literal(formattedPrice)
                        .withStyle(Style.EMPTY.withColor(color)));
                case EmeraldDisplayOption.FORMATTED -> priceComponent.append(Component.literal(formattedEmeralds)
                        .withStyle(Style.EMPTY.withColor(color)));
                default -> priceComponent.append(Component.literal(formattedPrice)
                                .withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)))
                        .append(Component.literal(" (" + formattedEmeralds + ")")
                                .withStyle(Style.EMPTY.withColor(color)));
            }
        }
        return priceComponent;
    }

    public static MutableComponent formatPriceWithFluctuation(String label, int price, float fluctuation) {
        return price > 0
                ? formatPrice(label, price)
                .append(Component.literal(" "))
                .append(formatPriceFluctuation(fluctuation))
                : Component.literal("");
    }

    public static MutableComponent formatText(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(Style.EMPTY.withColor(color));
    }

    public static MutableComponent formatPriceFluctuation(float fluctuation) {
        Style style = fluctuation < 0 ? Style.EMPTY.withColor(ChatFormatting.RED)
                : fluctuation > 0 ? Style.EMPTY.withColor(ChatFormatting.GREEN)
                : Style.EMPTY.withColor(ChatFormatting.GRAY);
        String formattedValue = fluctuation < 0
                ? String.format("%.1f", fluctuation) + "%"
                : "+" + String.format("%.1f", fluctuation) + "%";
        return Component.literal(formattedValue).withStyle(style);
    }

    public static float calcPriceDiff(float newPrice, float oldPrice) {
        return oldPrice == 0 ? 0 : ((newPrice - oldPrice) / oldPrice) * 100;
    }

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
            return switch (group1) {
                case 0 -> Double.compare(p2.getUnidentifiedAverage80Price(), p1.getUnidentifiedAverage80Price());
                case 1 -> Double.compare(p2.getAverage80Price(), p1.getAverage80Price());
                default -> 0;
            };
        });
    }

    private static int getPriceGroup(TradeMarketItemPriceInfo priceInfo) {
        if (priceInfo == null) {
            return 2;
        }
        return priceInfo.getUnidentifiedAverage80Price() != 0 ? 0 : 1;
    }

    @Unique
    public static void renderPriceInfoTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY,
                                              ItemStack item, List<Component> tooltipLines,
                                              boolean anchored) {
        Font font = McUtils.mc().font;
        Window window = McUtils.window();

        // 1) Clamp mouse coordinates
        mouseX = Math.min(mouseX, guiGraphics.guiWidth() - 10);
        mouseY = Math.max(mouseY, 10);

        // 2) Screen dims and fixed gap
        int guiwWindow = window.getGuiScaledWidth();
        int guiwGraphics = guiGraphics.guiWidth();
        int guiH = window.getGuiScaledHeight();
        int guiScale = (int) window.getGuiScale();
        int gap = 5 * guiScale;

        // 3) Primary tooltip dimensions
        List<Component> primary = Screen.getTooltipFromItem(McUtils.mc(), item);
        Dimension pDim = PriceTooltipHelper.calculateTooltipDimension(primary, font);

        // 4) “Unidentified” adjustment
        boolean gearBox = false;
        if (!primary.isEmpty()) {
            String title = primary.getFirst().getString();
            if (title.startsWith("Unidentified ")) {
                String suf = title.substring("Unidentified ".length()).trim();
                for (GearType g : GearType.values()) {
                    if (g.name().equalsIgnoreCase(suf)) {
                        gearBox = true;
                        break;
                    }
                }
            }
            if (gearBox) {
                pDim.width += 35 * guiScale;
                pDim.height += 35 * guiScale;
            }
        }

        // 5) Compute free space
        int spaceR = guiwWindow - (mouseX + pDim.width + gap);
        int spaceL = mouseX - gap;

        // 6) Pick side
        boolean placeLeft = spaceL >= spaceR;

        // 7) Available width
        int availableWidth = Math.max(placeLeft ? spaceL : spaceR, 0);
        int tooltipMaxH = Math.round(guiH * 0.8f);

        // 8) Scale factor + dims
        float scale = PriceTooltipHelper.calculateScaleFactor(tooltipLines, tooltipMaxH, availableWidth, 0.4f, 1.0f, font);
        Dimension tDim = PriceTooltipHelper.calculateTooltipDimension(tooltipLines, font);
        Dimension sDim = new Dimension(
                Math.round(tDim.width * scale),
                Math.round(tDim.height * scale)
        );

        // 9) Compute raw posX/posY
        float posX;
        float posY;
        if (!anchored) {
            // non-anchored: standard left/right of primary
            if (placeLeft) {
                posX = mouseX - gap - sDim.width;
            } else {
                // Reduce the gap between the primary tooltip and the price tooltip
                posX = mouseX + pDim.width;
            }
            posY = (mouseY + sDim.height > guiH)
                    ? guiH - sDim.height - gap
                    : mouseY;
        } else {
            if (placeLeft) {
                posX = 0;
            } else {
                posX = guiwWindow - sDim.width;
            }
            posY = (guiH - sDim.height) / 2f;
        }

        // 10) Clamp into [gap .. screen - tooltip - gap]
        int maxX = guiwGraphics - sDim.width - gap;
        int maxY = guiH - sDim.height - gap;
        if (!anchored) {
            posX = Math.min(Math.max(posX, 0), maxX);
        } else {
            // In anchored mode, only clamp the maximum to avoid going off-screen
            posX = Math.min(posX, maxX);
        }
        posY = Math.min(Math.max(posY, gap), maxY);

        // 11) Render
        PoseStack ms = guiGraphics.pose();
        ms.pushPose();
        ms.translate(posX, posY, 0);
        ms.scale(scale, scale, 1f);
        guiGraphics.renderComponentTooltip(font, tooltipLines, 0, 0);
        ms.popPose();
    }

    private static boolean isPriceInfoValid(TradeMarketItemPriceInfo info) {
        return info != null && !info.isEmpty();
    }
}
