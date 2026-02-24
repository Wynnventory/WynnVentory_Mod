# Favourite Item Notifications

## Overview
- Notifies you via system toast when items from your Wynntils Favourites are present in any active reward pools.
- Runs when you first join a world and whenever the world state changes to in-world.

## What is matched
- Uses Wynntils’ Favorites service to read your favourite item names.
- Compares favourites against all items in all known reward pools.
- If “Mythics only” is enabled, only items with Mythic rarity will be considered.

## Notifications
- For each match, a toast is shown with the item name (rarity-colored) and the pool short name.
- If many matches are found, only up to the configured maximum are shown; a final toast indicates how many more were found.

## Settings (FavouriteNotifierSettings)
- enableNotifier — master switch
- mythicsOnly — only notify for mythic items
- maxToasts — maximum number of toasts to show per check

## Notes
- Favourite items also appear highlighted throughout the Reward Screen itself.
