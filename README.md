# FLUX

FLUX is a Fabric performance mod focused on smoother chunk streaming, lower frame-time spikes, and efficient client-side rendering.

## Target

- Minecraft 26.2
- Fabric Loader
- Java (version pinned by the Minecraft/Fabric toolchain)
- Designed to coexist with Sodium, Lithium, FerriteCore, ImmediatelyFast, Entity Culling, ModernFix, and other optimization mods.

## Development status

Project scaffold in progress. Optimizations will be added only when they are measured and verified; FLUX will avoid duplicating features already provided by installed optimization mods.

## Build

Once the Gradle project is in place, run `./gradlew build` (Windows: `gradlew.bat build`). The mod JAR will be in `build/libs/`.
