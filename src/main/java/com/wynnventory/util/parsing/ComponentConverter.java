package com.wynnventory.util.parsing;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ComponentConverter {
    public static Component convertTagNodeToComponent(TagNode node, Style parentStyle) {
        Style currentStyle = parentStyle;

        if (node.attributes != null) {
            // Handle 'style' attribute
            if (node.attributes.containsKey("style")) {
                String styleAttr = node.attributes.get("style");
                // Parse style attributes
                String[] styles = styleAttr.split(";");
                for (String s : styles) {
                    s = s.trim();
                    if (s.startsWith("color:")) {
                        String colorCode = s.substring("color:".length()).trim();
                        int colorInt = Integer.parseInt(colorCode.replace("#", ""), 16);
                        currentStyle = currentStyle.withColor(TextColor.fromRgb(colorInt));
                    } else if (s.startsWith("margin-left:")) {
                        // Handle indentation by adding spaces
                        String marginValue = s.substring("margin-left:".length()).trim();
                        int pixels = Integer.parseInt(marginValue.replace("px", "").trim());
                        int spaces = pixels / 4; // Assuming 4 pixels per space
                        node.textContent = " ".repeat(spaces) + node.textContent;
                    }
                    // Add more style properties as needed
                }
            }
        }

        if (node.tagName == null) {
            // Text node
            return Component.literal(node.textContent).setStyle(currentStyle);
        } else {
            // Tag node
            Component component = Component.empty();
            for (TagNode child : node.children) {
                Component childComponent = convertTagNodeToComponent(child, currentStyle);
                component = component.copy().append(childComponent);
            }
            return component;
        }
    }
}

