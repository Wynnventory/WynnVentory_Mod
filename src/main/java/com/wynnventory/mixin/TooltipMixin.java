package com.wynnventory.mixin;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.items.WynnItem;
import com.wynntils.utils.mc.McUtils;
import com.wynnventory.accessor.ItemQueueAccessor;
import com.wynnventory.config.ConfigManager;
import com.wynnventory.enums.Region;
import com.wynnventory.enums.RegionType;
import com.wynnventory.util.AspectTooltipHelper;
import com.wynnventory.util.ItemStackUtils;
import com.wynnventory.util.LootpoolManager;
import com.wynnventory.util.PriceTooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public abstract class TooltipMixin {
    private static final String MARKET_TITLE = "󏿨";

    private final ConfigManager config = ConfigManager.getInstance();
    private final ItemQueueAccessor accessor = (ItemQueueAccessor) McUtils.mc().getConnection();

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
            accessor.queueItemForSubmit(itemStack);
        }

        if (config.isShowTooltips() && maybeWynnItem.isPresent()) {
            List<Component> tooltipComponents = ItemStackUtils.getTooltips(itemStack);
            PriceTooltipHelper.renderPriceInfoTooltip(guiGraphics, mouseX, mouseY, itemStack, tooltipComponents, config.isAnchorTooltips());
        }

        Component rawName = Objects.requireNonNull(ItemStackUtils.getWynntilsOriginalName(itemStack)).getComponent();
        String displayName = StyledText.fromComponent(rawName).getStringWithoutFormatting();
        Region region = Region.getRegionByName(displayName);

        if (region != null && region.getRegionType() == RegionType.RAID) {
            LootpoolManager.getRaidPools().stream()
                    .filter(p -> p.getRegion().equalsIgnoreCase(region.getShortName()))
                    .findFirst()
                    .ifPresent(pool -> AspectTooltipHelper.renderAspectTooltip(guiGraphics, mouseX, mouseY, pool));
        }
    }
}