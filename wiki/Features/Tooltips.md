# Price Tooltips

## Overview
- Adds price information to item tooltips when hovering items.
- Works across the game UI; party finder has additional “Aspect Tooltips” (see that page).
- Rendering respects Wynnventory tooltip settings and can be toggled via keybinds.

## What is shown
- Min / Max price
- Average price (overall and 80% trimmed) — optional
- Unidentified item prices — optional
- Optional price fluctuation indicator when available

## Rendering behavior
- If enabled and data exists, a secondary tooltip panel is drawn adjacent to the vanilla tooltip.
- The panel contains price information and is rendered using a custom positioner.
- If “Anchor tooltips” is enabled, the panel is fixed to the middle-left of the screen; otherwise, it attempts to follow the vanilla tooltip.
- The secondary tooltip also scales to fit the screen height if necessary.

## Settings reference
### TooltipSettings (ESC > Options > Wynnventory > Tooltip)
- showTooltips — master switch
- showBoxedItemTooltips — when enabled, shows price info for all possible items that could be contained within a Gear Box (e.g. "The Gambler" reward chest). This allows you to see the potential value of all options at once in the secondary tooltip panel.
- anchorTooltips — if enabled, fixes the secondary tooltip to the center-left of the screen
- showPriceFluctuation — show +/- trend when available
- displayFormat — number format
  - FORMATTED: using separators and suffixes where appropriate
  - RAW: raw numeric amounts
- showMaxPrice — include Max price line
- showUnidentifiedMaxPrice — include Max price for unID items
- showMinPrice — include Min price line
- showUnidentifiedMinPrice — include Min price for unID items
- showAveragePrice — include Average price
- showAverage80Price — include 80% trimmed average
- showUnidAveragePrice — include Average price for unID items
- showUnidAverage80Price — include 80% trimmed average for unID items

## Price highlighting (optional color emphasis)
- Enable color highlighting for lines above a chosen price.
- This only affects tooltip coloring; it does not filter items.

### PriceHighlightSettings (ESC > Options > Wynnventory > Highlighting)
- showColors — master switch for price highlighting
- colorMinPrice — minimum price threshold (items priced at or above this value are colored)
- highlightColor — the color used for emphasis (configure via slider or hex field)

## Keybinds affecting tooltips
- Toggle Tooltips — toggles `showTooltips` on/off and prints a chat message
- Toggle Boxed Tooltips — toggles `showBoxedItemTooltips` on/off and prints a chat message
