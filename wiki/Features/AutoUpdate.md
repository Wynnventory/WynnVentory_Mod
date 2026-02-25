# Auto-Update

## Overview
- On connecting to Wynncraft, the mod checks Modrinth to see if a newer Wynnventory build is available for your current jar.
- If a newer version exists, you’re notified in chat and the file is downloaded to your `mods/` folder.
- The old jar is scheduled for removal when the game closes; just restart Minecraft to complete the update.

## Beta behavior
- Beta builds do not auto-update to release builds. Instead, a one-time informational message is shown on join.

## How it works (technical)
- Computes a SHA‑1 hash of the currently loaded mod jar and queries Modrinth’s `version_file/{sha1}/update` API.
- When a newer compatible version is found, downloads its primary artifact and places it in `mods/`.
- Adds a shutdown hook to delete the old jar once the game exits.

## Notes
- If the current mod file cannot be located for any reason, the updater logs an error and does nothing.
- Network or parsing errors are logged and do not affect gameplay.
