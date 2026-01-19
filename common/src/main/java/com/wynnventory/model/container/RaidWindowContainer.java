package com.wynnventory.model.container;

import java.util.regex.Pattern;

public class RaidWindowContainer {
    public static final Pattern TITLE = Pattern.compile("\uDAFF\uDFE1\uE00C");

    private RaidWindowContainer() {}

    public static boolean matchesTitle(String title) {
        if (title == null) return false;
        return TITLE.matcher(title).find();
    }
}
