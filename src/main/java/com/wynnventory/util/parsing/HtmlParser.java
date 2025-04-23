package com.wynnventory.util.parsing;

import java.util.ArrayList;

public class HtmlParser {
    private final String input;
    private int pos;

    public HtmlParser(String input) {
        this.input = input;
        this.pos = 0;
    }

    // Entry point for parsing
    public TagNode parse() {
        return parseNodes();
    }

    // Parse multiple nodes
    private TagNode parseNodes() {
        TagNode root = new TagNode("root");
        while (pos < input.length() && !startsWith("</")) {
            if (input.charAt(pos) == '<') {
                TagNode element = parseElement();
                if (element != null) {
                    root.children.add(element);
                }
            } else {
                root.children.add(parseText());
            }
        }
        return root;
    }

    // Parse an element (tag)
    private TagNode parseElement() {
        // Expecting '<'
        expect('<');
        if (input.charAt(pos) == '/') {
            // End tag
            return null;
        }
        String tagName = parseTagName();
        TagNode node = new TagNode(tagName);

        // Self-closing tag (e.g., <br/>)
        if (tagName.equals("br")) {
            // Consume any attributes and the closing '/>' or '>'
            while (pos < input.length() && input.charAt(pos) != '>') {
                pos++;
            }
            expect('>');

            // Create a text node with a newline character
            return new TagNode("\n", true);
        }

        // Parse attributes
        while (true) {
            skipWhitespace();
            if (input.charAt(pos) == '>') {
                pos++;
                break;
            } else {
                parseAttribute(node);
            }
        }

        // Parse child nodes
        node.children = new ArrayList<>();
        while (pos < input.length() && !startsWith("</")) {
            if (input.charAt(pos) == '<') {
                TagNode child = parseElement();
                if (child != null) {
                    node.children.add(child);
                }
            } else {
                node.children.add(parseText());
            }
        }

        // Consume end tag
        if (startsWith("</")) {
            expect('<');
            expect('/');
            String endTagName = parseTagName();
            if (!endTagName.equals(tagName)) {
                throw new RuntimeException("Mismatched end tag: " + endTagName);
            }
            expect('>');
        }

        return node;
    }

    // Parse text content
    private TagNode parseText() {
        int start = pos;
        while (pos < input.length() && input.charAt(pos) != '<') {
            pos++;
        }
        String text = input.substring(start, pos).replaceAll("[^\\x20-\\x7E]", "");
        // Trim leading and trailing whitespace
        return new TagNode(text, true);
    }

    // Parse tag name
    private String parseTagName() {
        int start = pos;
        while (pos < input.length() && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '-')) {
            pos++;
        }
        return input.substring(start, pos);
    }

    // Parse an attribute
    private void parseAttribute(TagNode node) {
        skipWhitespace();
        int start = pos;
        while (pos < input.length() && input.charAt(pos) != '=' && !Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
        String attrName = input.substring(start, pos);
        expect('=');
        char quote = input.charAt(pos);
        expect(quote);
        int valueStart = pos;
        while (pos < input.length() && input.charAt(pos) != quote) {
            pos++;
        }
        String attrValue = input.substring(valueStart, pos);
        expect(quote);
        node.attributes.put(attrName, attrValue);
    }

    // Utility methods
    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

    private void expect(char c) {
        if (pos >= input.length() || input.charAt(pos) != c) {
            throw new RuntimeException("Expected '" + c + "' at position " + pos);
        }
        pos++;
    }

    private boolean startsWith(String s) {
        return input.startsWith(s, pos);
    }
}
