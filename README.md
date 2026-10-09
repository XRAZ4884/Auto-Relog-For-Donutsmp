# Auto Relog (Fabric 1.21.11)

Client-side Fabric mod source project. The configured trigger is **Y <= -5**. When triggered in multiplayer, it remembers the current server entry, disconnects to the title screen, waits about three seconds, and attempts to reconnect once.

## Build

Requirements: Java 21 and Gradle. From this directory run:

```sh
gradle build
```

The JAR should be produced under `build/libs/` if the selected Loom, mappings, and Fabric API versions are available and the code compiles.

## Notes

- This is source code, not a precompiled JAR.
- Reconnect behavior and dependency versions have not been tested in this environment; compile and test locally before use.
- The mod only acts in multiplayer and does not send chat commands or alter server data.
