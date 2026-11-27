# WinTrain

WinTrain is a Fabric mod project for Minecraft Java Edition 1.21.11.

## Development

- Minecraft: 1.21.11
- Fabric Loader: 0.18.1
- Fabric API: 0.141.1+1.21.11
- Java: 21
- Fabric Loom: 1.14.10

## Build

GitHub Actions builds the mod on every push and pull request.
Open the **Actions** tab, select the latest **Build WinTrain** run, and download
the WinTrain artifact.

For a local build with Gradle 9.2.1 installed:

```bash
gradle build
```

The distributable mod JAR is created in `build/libs/`.

## Current status

- [x] Fabric project scaffold
- [x] Minecraft 1.21.11 build configuration
- [x] Automated GitHub Actions build
- [ ] Friendly trainer NPC and custom appearance
- [ ] Training menus and movement practice challenges
