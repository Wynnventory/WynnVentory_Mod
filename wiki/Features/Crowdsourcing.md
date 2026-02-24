# Crowdsourcing (Data Collection)

## Overview
- Wynnventory can collect and anonymously submit market and reward-pool data to improve price estimates and pool contents.
- Data is batched in memory and sent periodically or on-demand using the `/wynnventory send` command.

## What is collected
- Lootrun reward pools — item drops grouped by pool
- Raid reward pools — item drops grouped by raid pool
- Trade Market listings — active listing snapshots
- Gambit items — gambit choices inferred from UI interactions

## How it is sent
- A background scheduler runs every 5 minutes and flushes all queues to the Wynnventory API.
- The mod also flushes everything gracefully on game shutdown.
- You can force-send pending data with the command below.

## Manual send command
- `/wynnventory send` — immediately sends any pending lootrun/raid/trademarket/gambit data and prints a confirmation message.

## Privacy
- No personal identifiers are attached; only the relevant item and pool information is sent.
- Submission endpoints are versioned and may change between releases to ensure data quality.
