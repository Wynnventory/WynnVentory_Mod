package com.wynnventory.model.item.info;

import java.util.List;

public record AspectTierInfo (
    int threshold,
    List<String> description
    ) {

    public String getDescriptionText() {
        StringBuilder sb = new StringBuilder();
        for(String s : description) {
            sb.append(s);
        }

        return sb.toString();
    }
}
