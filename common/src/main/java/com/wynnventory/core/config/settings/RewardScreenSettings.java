package com.wynnventory.core.config.settings;

public class RewardScreenSettings {
    private boolean showMythic = true;
    private boolean showFabled = true;
    private boolean showLegendary = true;
    private boolean showRare = true;
    private boolean showUnique = true;
    private boolean showCommon = true;
    private boolean showSet = true;
    private boolean showUnusable = true;
    private int lootrunColumns = 5;
    private int raidColumns = 3;

    public boolean isShowMythic() {
        return showMythic;
    }

    public void setShowMythic(boolean showMythic) {
        this.showMythic = showMythic;
    }

    public boolean isShowFabled() {
        return showFabled;
    }

    public void setShowFabled(boolean showFabled) {
        this.showFabled = showFabled;
    }

    public boolean isShowLegendary() {
        return showLegendary;
    }

    public void setShowLegendary(boolean showLegendary) {
        this.showLegendary = showLegendary;
    }

    public boolean isShowRare() {
        return showRare;
    }

    public void setShowRare(boolean showRare) {
        this.showRare = showRare;
    }

    public boolean isShowUnique() {
        return showUnique;
    }

    public void setShowUnique(boolean showUnique) {
        this.showUnique = showUnique;
    }

    public boolean isShowCommon() {
        return showCommon;
    }

    public void setShowCommon(boolean showCommon) {
        this.showCommon = showCommon;
    }

    public boolean isShowSet() {
        return showSet;
    }

    public void setShowSet(boolean showSet) {
        this.showSet = showSet;
    }

    public boolean isShowUnusable() {
        return showUnusable;
    }

    public void setShowUnusable(boolean showUnusable) {
        this.showUnusable = showUnusable;
    }

    public int getLootrunColumns() {
        return lootrunColumns;
    }

    public void setLootrunColumns(int lootrunColumns) {
        this.lootrunColumns = lootrunColumns;
    }

    public int getRaidColumns() {
        return raidColumns;
    }

    public void setRaidColumns(int raidColumns) {
        this.raidColumns = raidColumns;
    }

    @Override
    public String toString() {
        return "RarityConfig{" +
                "showMythic=" + showMythic +
                ", showFabled=" + showFabled +
                ", showLegendary=" + showLegendary +
                ", showRare=" + showRare +
                ", showUnique=" + showUnique +
                ", showCommon=" + showCommon +
                ", showSet=" + showSet +
                ", showUnusable=" + showUnusable +
                '}';
    }
}
