package com.wynnventory.core.config.settings;

public class FavouriteNotifierSettings {
    private boolean enableNotifier = true;
    private int maxToasts = 5;
    private boolean mythicsOnly = false;

    public boolean isEnableNotifier() {
        return enableNotifier;
    }

    public void setEnableNotifier(boolean enableNotifier) {
        this.enableNotifier = enableNotifier;
    }

    public int getMaxToasts() {
        return maxToasts;
    }

    public void setMaxToasts(int maxToasts) {
        this.maxToasts = maxToasts;
    }

    public boolean isMythicsOnly() {
        return mythicsOnly;
    }

    public void setMythicsOnly(boolean mythicsOnly) {
        this.mythicsOnly = mythicsOnly;
    }
}
