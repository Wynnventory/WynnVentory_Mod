package com.wynnventory.ui.layout;

import com.wynnventory.config.ConfigManager;
import com.wynnventory.model.item.Lootpool;
import com.wynnventory.model.item.LootpoolItem;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;

import java.util.List;

/**
 * Helper class for managing UI layout in the LootpoolScreen.
 * Handles positioning and scaling of UI components based on screen dimensions.
 */
public class LayoutHelper {
    // Constants from LootpoolScreen
    private static final int ITEM_SIZE = 16;
    private static final int ITEMS_PER_ROW = 5;
    private static final int ITEM_PADDING = 8;
    private static final int COL_WIDTH = (ITEM_SIZE * ITEMS_PER_ROW) + (ITEM_PADDING * ITEMS_PER_ROW);
    private static final int PANEL_PADDING = 20;
    private static final int GAP_TITLE_TO_ITEMS = 24;
    private static final int GAP_FROM_RIGHT_BORDER = 10;

    // Screen dimensions
    private final int screenWidth;
    private final int screenHeight;

    // UI component positions
    private final int leftBoundary;

    // Scaling
    private float overallScale;

    // Layout calculations
    private int startX;
    private int lastTitlesY;
    private int itemsStartY;

    /**
     * Creates a new LayoutHelper for the given screen dimensions.
     *
     * @param screenWidth  The width of the screen
     * @param screenHeight The height of the screen
     */
    public LayoutHelper(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.leftBoundary = 20;
    }

    /**
     * Calculates the position for tab buttons.
     *
     * @param buttonWidth Width of a single tab button
     * @param spacing     Spacing between buttons
     * @return An array with [x, y, totalWidth] for the first button
     */
    public int[] calculateTabPosition(int buttonWidth, int spacing) {
        int totalWidth = 2 * buttonWidth + spacing;
        int x = (screenWidth - totalWidth) / 2;
        int y = 10;
        return new int[]{x, y, totalWidth};
    }

    /**
     * Calculates the position for the settings button.
     *
     * @param buttonSize      Size of the settings button (width and height)
     * @param tabButtonHeight Height of the tab buttons for vertical alignment
     * @return An array with [x, y] for the settings button
     */
    public int[] calculateSettingsButtonPosition(int buttonSize, int tabButtonHeight) {
        int x = screenWidth - buttonSize - GAP_FROM_RIGHT_BORDER;
        int y = 10 + tabButtonHeight / 2 - buttonSize / 2;
        return new int[]{x, y};
    }

    /**
     * Calculates the position for the reload button.
     *
     * @param buttonSize          Size of the reload button (width and height)
     * @param settingsButtonWidth Width of the settings button
     * @param tabButtonHeight     Height of the tab buttons for vertical alignment
     * @return An array with [x, y] for the reload button
     */
    public int[] calculateReloadButtonPosition(int buttonSize, int settingsButtonWidth, int tabButtonHeight) {
        int gap = 5;
        int x = screenWidth - buttonSize - settingsButtonWidth - gap - GAP_FROM_RIGHT_BORDER;
        int y = 10 + tabButtonHeight / 2 - buttonSize / 2;
        return new int[]{x, y};
    }

    /**
     * Calculates the position for the search bar.
     *
     * @param width               Width of the search bar
     * @param height              Height of the search bar
     * @param settingsButtonWidth Width of the settings button
     * @param reloadButtonWidth   Width of the reload button
     * @param tabButtonY          Y position of the tab buttons for vertical alignment
     * @return An array with [x, y] for the search bar
     */
    public int[] calculateSearchBarPosition(int width, int height, int settingsButtonWidth, int reloadButtonWidth, int tabButtonY) {
        int x = screenWidth - width - settingsButtonWidth - 5 - GAP_FROM_RIGHT_BORDER - reloadButtonWidth - 5;
        int y = tabButtonY;
        return new int[]{x, y};
    }

    /**
     * Calculates the position for filter toggle buttons.
     *
     * @param buttonWidth        Width of the filter toggle buttons
     * @param buttonHeight       Height of the filter toggle buttons
     * @param spacing            Spacing between buttons
     * @param reloadButtonY      Y position of the reload button
     * @param reloadButtonHeight Height of the reload button
     * @return An array with [x, y] for the first filter toggle button
     */
    public int[] calculateFilterTogglePosition(int buttonWidth, int buttonHeight, int spacing, int reloadButtonY, int reloadButtonHeight) {
        int x = screenWidth - buttonWidth - GAP_FROM_RIGHT_BORDER;
        int y = reloadButtonY + reloadButtonHeight + 6;
        return new int[]{x, y, buttonHeight + spacing};
    }

    /**
     * Calculates the layout for item columns based on available space and content.
     *
     * @param pools         The list of lootpools to display
     * @param searchBar     The search bar component for reference
     * @param filterToggles The list of filter toggle buttons
     * @param query         The current search query
     */
    public void calculateItemLayout(List<Lootpool> pools, EditBox searchBar, List<Button> filterToggles, String query) {
        int rightBoundary;
        float verticalScale;
        float horizontalScale;
        // Set boundaries
        rightBoundary = filterToggles.isEmpty() ? searchBar.getX() - 10 : filterToggles.getFirst().getX() - 10;
        int availableColumnsWidth = rightBoundary - leftBoundary;

        // Calculate horizontal scale
        int totalWidth = pools.size() * COL_WIDTH + (pools.size() - 1) * PANEL_PADDING;
        horizontalScale = 1.0f;
        if (totalWidth > availableColumnsWidth) {
            horizontalScale = availableColumnsWidth / (float) totalWidth;
        }

        // Calculate vertical scale
        lastTitlesY = searchBar.getY() + searchBar.getHeight() + 10;
        int bottomMargin = 10;
        int availableVertical = screenHeight - lastTitlesY - bottomMargin;

        int maxColumnHeightUnscaled = calculateMaxColumnHeight(pools, query);
        verticalScale = 1.0f;
        if (maxColumnHeightUnscaled > availableVertical && availableVertical > 0) {
            verticalScale = availableVertical / (float) maxColumnHeightUnscaled;
        }

        // Use the smaller of the two scales
        overallScale = Math.min(horizontalScale, verticalScale);

        // Calculate starting positions
        int scaledTotalWidth = Math.round(totalWidth * overallScale);
        startX = leftBoundary + (availableColumnsWidth - scaledTotalWidth) / 2;
        itemsStartY = lastTitlesY + GAP_TITLE_TO_ITEMS;

    }

    /**
     * Calculates the maximum column height needed based on the number of items.
     *
     * @param pools The list of lootpools to display
     * @param query The current search query
     * @return The maximum unscaled column height
     */
    private int calculateMaxColumnHeight(List<Lootpool> pools, String query) {
        int maxColumnHeightUnscaled = 0;

        for (Lootpool pool : pools) {
            int rendered = countMatchingItems(pool, query);
            if (rendered > 0) {
                int rows = (rendered + ITEMS_PER_ROW - 1) / ITEMS_PER_ROW;
                int colHeight = rows * ITEM_SIZE + (rows - 1) * ITEM_PADDING;
                maxColumnHeightUnscaled = Math.max(maxColumnHeightUnscaled, colHeight);
            }
        }

        return maxColumnHeightUnscaled;
    }

    /**
     * Counts the number of items in a pool that match the search query and rarity filters.
     *
     * @param pool  The lootpool to check
     * @param query The current search query
     * @return The number of matching items
     */
    private int countMatchingItems(Lootpool pool, String query) {
        int count = 0;
        for (LootpoolItem item: pool.getItems()) {
            String name = item.getName();
            if (!name.toLowerCase().contains(query.toLowerCase())) continue;
            if (matchesRarityFilters(item, ConfigManager.getInstance())) continue;
            count++;
        }

        return count;
    }

    /**
     * Checks if an item matches the current rarity filters.
     *
     * @param item   The item to check
     * @param config The config manager instance
     * @return True if the item matches the filters, false otherwise
     */
    public boolean matchesRarityFilters(LootpoolItem item, ConfigManager config) {
        String rarity = item.getRarity().toLowerCase();
        var filter = config.getRarityConfig();

        return !switch (rarity) {
            case "mythic" -> filter.getShowMythic();
            case "fabled" -> filter.getShowFabled();
            case "legendary" -> filter.getShowLegendary();
            case "unique" -> filter.getShowUnique();
            case "rare" -> filter.getShowRare();
            case "common" -> filter.getShowCommon();
            case "set" -> filter.getShowSet();
            default -> true;
        };
    }

    /**
     * Calculates the position for a column of items.
     *
     * @param columnIndex The index of the column
     * @return An array with [x, y] for the column
     */
    public int[] calculateColumnPosition(int columnIndex) {
        int x = startX + Math.round(columnIndex * (COL_WIDTH + PANEL_PADDING) * overallScale);
        return new int[]{x, itemsStartY};
    }

    /**
     * Calculates the position for an item within a column.
     *
     * @param columnX   The x position of the column
     * @param itemIndex The index of the item within the column
     * @return An array with [x, y, size] for the item
     */
    public int[] calculateItemPosition(int columnX, int itemIndex) {
        int x = columnX + Math.round((itemIndex % ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING) * overallScale);
        int y = itemsStartY + Math.round((itemIndex / ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_PADDING) * overallScale);
        int size = Math.round(ITEM_SIZE * overallScale);
        return new int[]{x, y, size};
    }

    /**
     * Calculates the position for a column title.
     *
     * @param columnIndex The index of the column
     * @return An array with [x, y] for the title
     */
    public int[] calculateColumnTitlePosition(int columnIndex) {
        int x = startX + Math.round(columnIndex * (COL_WIDTH + PANEL_PADDING) * overallScale);
        int textX = x + Math.round(((COL_WIDTH - ITEM_PADDING) / 2f) * overallScale);
        return new int[]{textX, lastTitlesY};
    }
}
