package com.wynnventory.util.parsing;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public class ComponentConverter {
    public static Component convertTagNodeToComponent(TagNode node) {
        if (node.tagName == null) {
            // Text node
            return Component.literal(node.textContent);
        } else {
            // Tag node
            Component component = Component.empty();
            for (TagNode child : node.children) {
                component = component.copy().append(convertTagNodeToComponent(child));
            }

            // Apply styles based on attributes
            Style style = Style.EMPTY;

            // Handle 'style' attribute
            if (node.attributes.containsKey("style")) {
                String styleAttr = node.attributes.get("style");
                // Simple parsing of 'color:#XXXXXX'
                if (styleAttr.contains("color:")) {
                    String colorCode = styleAttr.substring(styleAttr.indexOf("color:") + 6).split(";")[0].trim();
                    int colorInt = Integer.parseInt(colorCode.replace("#", ""), 16);
                    style = style.withColor(TextColor.fromRgb(colorInt));
                }
            }

            return component.copy().withStyle(style);
        }
    }
}
