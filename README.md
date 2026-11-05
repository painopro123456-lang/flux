# FLUX

FLUX is a client-side Fabric performance project focused on smoother chunk streaming, lower frame-time spikes, and efficient rendering.

## Target

- Minecraft 26.2
- Fabric Loader
- Java 25
- Designed to coexist with Sodium, Lithium, FerriteCore, ImmediatelyFast, Entity Culling, ModernFix, Krypton, and other optimization mods.

## Current implementation

- Lightweight in-game performance panel.
- Rolling frame-time measurements and an estimated 1% low.
- Client chunk load/unload activity per second.
- Measurements are diagnostic only; they do not change vanilla chunk scheduling.

## Roadmap

1. Use the measurements to identify chunk-streaming and frame-time bottlenecks.
2. Add compatibility-aware chunk pipeline improvements only where FLUX can provide a measurable benefit.
3. Benchmark average FPS, 1% lows, and chunk-loading behavior against a clean baseline and common optimization mods.

FLUX will avoid duplicating features already provided by installed optimization mods. No performance gains are claimed until they are benchmarked.

## Build

Run `gradle build` (Windows: `gradle.bat build`). The mod JAR will be in `build/libs/`.
