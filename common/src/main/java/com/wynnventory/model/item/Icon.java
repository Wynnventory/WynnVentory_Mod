package com.wynnventory.model.item;

public class Icon {
    private String format;
    private String value;

    public Icon() {}

    public Icon(String format, String value) {
        this.format = format;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
