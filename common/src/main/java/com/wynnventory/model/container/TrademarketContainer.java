package com.wynnventory.model.container;

import java.util.regex.Pattern;

public class TrademarketContainer {
    public static final Pattern TITLE = Pattern.compile("\uDAFF\uDFE8\uE011");

    private TrademarketContainer() {}

    public static boolean matchesTitle(String title) {
        if (title == null) return false;
        return TITLE.matcher(title).find();
    }
}
