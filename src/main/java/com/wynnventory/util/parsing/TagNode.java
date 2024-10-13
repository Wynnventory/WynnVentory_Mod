package com.wynnventory.util.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagNode {
    public String tagName; // null for text nodes
    public Map<String, String> attributes; // Attributes for tags
    public List<TagNode> children; // Child nodes
    public String textContent; // Non-null for text nodes

    // Constructor for tag nodes
    public TagNode(String tagName) {
        this.tagName = tagName;
        this.attributes = new HashMap<>();
        this.children = new ArrayList<>();
        this.textContent = null;
    }

    // Constructor for text nodes
    public TagNode(String textContent, boolean isTextNode) {
        this.tagName = null;
        this.attributes = null;
        this.children = null;
        this.textContent = textContent;
    }
}
