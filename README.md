[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1083173?logo=curseforge&logoColor=%23F16436&label=Downloads&color=%23F16436&link=https%3A%2F%2Fwww.curseforge.com%2Fminecraft%2Fmc-mods%2Fwynnventory)](https://www.curseforge.com/minecraft/mc-mods/wynnventory)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/CORVJbiT?logo=modrinth&logoColor=%2300AF5C&label=Downloads&color=%2300AF5C&link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fwynnventory)](https://modrinth.com/mod/wynnventory)
[![Discord](https://img.shields.io/discord/1272858777577586769?logo=Discord&logoColor=%235865F2&color=%235865F2&link=https%3A%2F%2Fdiscord.gg%2Fb6ATfrePuR)](https://discord.gg/b6ATfrePuR)

# Wynnventory

Backend: https://github.com/Aruloci/WynnVentory_Web



## Commit Messages
Make sure to format commit messages [accordingly](https://www.conventionalcommits.org/en/v1.0.0/#summary).

## Types of Commits:
- **fix**: For patches.
- **feat**: For minor updates that introduce a new feature.
- **build**: For changes that affect the build system or external dependencies.
- **chore**: For routine tasks that do not modify source code or tests (hidden from release notes).
- **ci**: For changes to the Continuous Integration configuration files and scripts (hidden from release notes).
- **docs**: For documentation-only changes
- **style**: For changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc.).
- **refactor**: For code changes that neither fix a bug nor add a feature.
- **perf**: For performance improvements.
- **test**: For adding or modifying tests.

## Major Changes:
- use chore(release) or feat(major) 

## Minor Changes:
- use feat(minor)

## Patches:
Everything apart from the prefixes for major and minor will result in a patch release

## Project setup
### Authentication
To authenticate and login to your account from your IDE we are using [DevAuth](https://github.com/DJtheRedstoner/DevAuth).
For the initial setup add the following line to your JVM arguments:<br/>
`-Ddevauth.enabled=true`
