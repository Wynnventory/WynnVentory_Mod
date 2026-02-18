[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1083173?logo=curseforge&logoColor=%23F16436&label=Downloads&color=%23F16436&link=https%3A%2F%2Fwww.curseforge.com%2Fminecraft%2Fmc-mods%2Fwynnventory)](https://www.curseforge.com/minecraft/mc-mods/wynnventory)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/CORVJbiT?logo=modrinth&logoColor=%2300AF5C&label=Downloads&color=%2300AF5C&link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fwynnventory)](https://modrinth.com/mod/wynnventory)
[![Discord](https://img.shields.io/discord/1272858777577586769?logo=Discord&logoColor=%235865F2&color=%235865F2&link=https%3A%2F%2Fdiscord.gg%2Fb6ATfrePuR)](https://discord.gg/b6ATfrePuR)

# Wynnventory
WynnVentory is a Minecraft mod for the Wynncraft server. It allows users to display trade market prices on items ingame. Data is also be available on the [WynnVentory website](https://www.wynnventory.com/).


## Setup
The project is using [DevAuth](https://github.com/DJtheRedstoner/DevAuth) which you need to enable in your run configs.
Just add `-Ddevauth.enabled=true` to your JVM options and you should be good to go.

### Formatting & Git Hooks
We use [Spotless](https://github.com/diffplug/spotless) to keep our code formatted.
To automatically run `spotlessApply` before every commit, you can install the Git hooks by running:
```bash
./gradlew installGitHooks
```
This task is also automatically run during the `build` task.
If there are violations that `spotlessApply` cannot fix (like wildcard imports), the commit will be aborted.
```bash
./gradlew spotlessApply
```
