package com.wynnventory.util;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.models.emeralds.type.EmeraldUnits;
import com.wynntils.models.gear.type.GearInfo;
import com.wynntils.models.gear.type.GearType;
import com.wynntils.models.items.items.game.GameItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.items.items.game.IngredientItem;
import com.wynntils.models.items.items.game.MaterialItem;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.config.EmeraldDisplayOption;
import com.wynnventory.core.ModInfo;
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


        if (priceInfo.isEmpty()) {
            tooltipLines.add(formatText("No price data available yet!", ChatFormatting.RED));
        } else {
            addPriceLine(tooltipLines, "Max: ", priceInfo.getHighestPrice(), config.isShowPriceFluctuation(), historicInfo.isEmpty() ? 0 : historicInfo.getHighestPrice());
            addPriceLine(tooltipLines, "Min: ", priceInfo.getLowestPrice(), config.isShowPriceFluctuation(), historicInfo.isEmpty() ? 0 : historicInfo.getLowestPrice());
            addPriceLine(tooltipLines, "Avg: ", priceInfo.getAveragePrice(), config.isShowPriceFluctuation(), historicInfo.isEmpty() ? 0 : historicInfo.getAveragePrice());
            addPriceLine(tooltipLines, "Avg 80%: ", priceInfo.getAverage80Price(), config.isShowPriceFluctuation(), historicInfo.isEmpty() ? 0 : historicInfo.getAverage80Price());
            addPriceLine(tooltipLines, "Unidentified Avg: ", priceInfo.getUnidentifiedAveragePrice(), config.isShowPriceFluctuation(), historicInfo.isEmpty() ? 0 : historicInfo.getUnidentifiedAveragePrice());
            addPriceLine(tooltipLines, "Unidentified Avg 80%: ", priceInfo.getUnidentifiedAverage80Price(), config.isShowPriceFluctuation(), historicInfo.isEmpty() ? 0 : historicInfo.getUnidentifiedAverage80Price());
        }

        return tooltipLines;
    }

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
            if (group1 == 0) {
                return Double.compare(p2.getUnidentifiedAverage80Price(), p1.getUnidentifiedAverage80Price());
            } else if (group1 == 1) {
                return Double.compare(p2.getAverage80Price(), p1.getAverage80Price());
            } else {
                return 0;
            }
        });
    }

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

        // Retrieve the primary tooltip (it has priority and must not be overlapped).
        List<Component> primaryTooltips = Screen.getTooltipFromItem(McUtils.mc(), item);
        Dimension primaryTooltipDim = PriceTooltipHelper.calculateTooltipDimension(primaryTooltips, font);

        // Use the new logic to detect an unidentified tooltip.
        if (!primaryTooltips.isEmpty()) {
            boolean gearBoxItem = false;
            String primaryTitle = primaryTooltips.getFirst().getString();
            final String prefix = "Unidentified ";
            if (primaryTitle.startsWith(prefix)) {
                String suffix = primaryTitle.substring(prefix.length()).trim();
                // Iterate over GearType enum values and see if the suffix matches one of the enum keys (ignoring case).
                for (GearType gear : GearType.values()) {
                    if (gear.name().equalsIgnoreCase(suffix)) {
                        gearBoxItem = true;
                        break;
                    }
                }
            }
            if (gearBoxItem) {
                primaryTooltipDim.width += 35 * guiScale;
                primaryTooltipDim.height += 35 * guiScale;
            }
        }


        // Compute available horizontal space from the mouse position.
        // spaceToRight: what remains if the primary tooltip is rendered to the right.
        int spaceToRight = guiScaledWidth - (mouseX + primaryTooltipDim.width + gap);
        int spaceToLeft = mouseX - gap; // space to the left of the mouse.

        // Use a minimum threshold so that if only a few pixels are available, it’s deemed insufficient.
        final int MIN_SPACE_FOR_PRIMARY = 20;
        boolean primaryRenderedRight = spaceToRight >= MIN_SPACE_FOR_PRIMARY;

        // Determine available width (for scaling the price tooltip) and base position depending on anchored mode.
        int availableWidth;
        float posX;
        float posY;
        int tooltipMaxHeight = Math.round(guiScaledHeight * 0.8f);

        if (!anchored) {
            // Non-anchored behavior: position price tooltip near the mouse.
            if (primaryRenderedRight) {
                // Primary tooltip will be rendered to the right.
                // => Render price tooltip on the opposite side (left of the mouse).
                availableWidth = spaceToLeft; // equals mouseX - gap.
            } else {
                // Not enough space on the right for the primary tooltip.
                // => Primary is rendered left, so price tooltip will be to its right.
                availableWidth = guiScaledWidth - (mouseX + gap);
            }
        } else {
            // Anchored behavior: price tooltip is snapped to the screen edge.
            if (primaryRenderedRight) {
                // Primary tooltip is rendered to the right (has sufficient room on right).
                // => Price tooltip is anchored at the left edge.
                availableWidth = spaceToLeft; // space from left edge to mouse.
            } else {
                // Not enough room on the right for the primary tooltip.
                // => Primary tooltip is rendered to the left.
                // => Price tooltip is anchored to the right edge.
                availableWidth = guiScaledWidth - (mouseX + gap);
            }
        }

        // Calculate the scale factor so that the price tooltip fits in the available width and height.
        float scaleFactor = PriceTooltipHelper.calculateScaleFactor(tooltipLines, tooltipMaxHeight, availableWidth, 0.4f, 1.0f, font);
        Dimension tooltipDim = PriceTooltipHelper.calculateTooltipDimension(tooltipLines, font);
        Dimension scaledTooltipDim = new Dimension(
                Math.round(tooltipDim.width * scaleFactor),
                Math.round(tooltipDim.height * scaleFactor)
        );

        // Now compute final positions.
        if (!anchored) {
            // Non-anchored: position relative to the mouse.
            if (primaryRenderedRight) {
                // Render price tooltip to the left of the mouse.
                posX = (float) mouseX - gap - scaledTooltipDim.width;
            } else {
                // Render price tooltip to the right of the mouse.
                posX = (float) mouseX + gap;
            }
            // Vertical position: if rendering at mouseY would cause it to go off the bottom, adjust.
            if (mouseY + scaledTooltipDim.height > guiScaledHeight) {
                posY = (float) guiScaledHeight - scaledTooltipDim.height - gap;
            } else {
                posY = mouseY;
            }
        } else {
            // Anchored: pin to screen edge and center vertically.
            posY = (guiScaledHeight - scaledTooltipDim.height) / 2f;
            if (primaryRenderedRight) {
                // Primary is rendered on the right → price tooltip anchored on left.
                posX = 0;
            } else {
                // Primary is rendered on left → price tooltip anchored on right.
                posX = (float) guiScaledWidth - scaledTooltipDim.width;
            }
        }

        // Render the price tooltip with applied translation and scale.
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(posX, posY, 0);
        poseStack.scale(scaleFactor, scaleFactor, 1.0f);
        guiGraphics.renderComponentTooltip(font, tooltipLines, 0, 0);
        poseStack.popPose();
    }
}
