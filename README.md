# FLUX

FLUX is a lightweight client-side Fabric mod focused on smoother chunk loading and reduced stuttering.

## Target

- Minecraft 26.2
- Fabric Loader 0.19.3+
- Java 25+

## Current optimization

FLUX adjusts vanilla's client chunk-render task queue to favor first-time chunk mesh builds slightly more often than repeat rebuilds when both are waiting. This is intended to help newly visible terrain appear sooner while avoiding a custom thread pool or extra background systems.

FLUX runs quietly in the background. There is no HUD, menu, or extra configuration to manage.

## Compatibility

Designed to complement other performance mods rather than replace them. Compatibility with Sodium and other chunk-renderer replacements still needs in-game testing.

## Build

Run `gradle build` (Windows: `gradle.bat build`). The mod JAR will be in `build/libs/`.
