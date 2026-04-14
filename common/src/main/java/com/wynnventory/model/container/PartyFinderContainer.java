package com.wynnventory.model.container;

import java.util.regex.Pattern;

public class PartyFinderContainer {
    public static final Pattern JOIN_TAB = Pattern.compile("\uDAFF\uDFE4\uE03E");
    public static final Pattern QUEUE_TAB = Pattern.compile("\uDAFF\uDFE4\uE03F");

    private PartyFinderContainer() {}

    public static boolean matchesTitle(String title) {
        if (title == null) return false;
        return JOIN_TAB.matcher(title).find() || QUEUE_TAB.matcher(title).find();
    }
}
