package com.wynnventory.model.container;

import java.util.regex.Pattern;

public class PartyFinderContainer {
    public static final Pattern TITLE = Pattern.compile("\uDAFF\uDFE4\uE03E");

    private PartyFinderContainer() {}

    public static boolean matchesTitle(String title) {
        if (title == null) return false;
        return TITLE.matcher(title).find();
    }
}
