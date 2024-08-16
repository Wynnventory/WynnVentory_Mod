## Wynnventory
Backend: https://github.com/Aruloci/WynnVentory_Web

## Download the mod!
Currently only Fabric is supported. You can download our mod from one of the links below.<br/>
Modrinth: https://modrinth.com/mod/wynnventory<br/>
CurseForge: https://www.curseforge.com/minecraft/mc-mods/wynnventory

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
