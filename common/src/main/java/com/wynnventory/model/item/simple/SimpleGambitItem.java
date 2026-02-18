package com.wynnventory.model.item.simple;

import com.wynntils.core.text.StyledText;
import com.wynntils.models.items.items.gui.GambitItem;
import com.wynntils.utils.colors.CustomColor;
import com.wynnventory.model.item.ModInfoProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleGambitItem extends ModInfoProvider {
    private String name;
    private List<String> description = new ArrayList<>();
    private CustomColor color;

    public SimpleGambitItem(GambitItem gambitItem) {
        this.name = gambitItem.getName();
        this.color = gambitItem.getColor();
        for (StyledText line : gambitItem.getDescription()) {
            this.description.add(line.getString());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public String getColor() {
        return color.toHexString();
    }

    public void setColor(CustomColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SimpleGambitItem other) {
            return Objects.equals(name, other.name) && Objects.equals(description, other.description);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
