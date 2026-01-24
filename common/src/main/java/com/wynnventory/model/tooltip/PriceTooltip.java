package com.wynnventory.model.tooltip;

import com.wynnventory.model.item.trademarket.TrademarketPriceSummary;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class PriceTooltip {
    public List<Component> tooltipLines = new ArrayList<>();

    public PriceTooltip(TrademarketPriceSummary summary) {

    }
}
