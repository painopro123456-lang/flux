# FLUX

FLUX is a lightweight client-side Fabric optimization mod for Minecraft 26.2.

## Target hardware profile

FLUX is being tuned and tested against a modest laptop profile:
- Intel Core i5-6200U (2 cores / 4 threads)
- NVIDIA GeForce 920MX
- 6 GB system RAM

The performance target is **16 render distance + 16 simulation distance**. This is a target, not a guaranteed result; actual performance depends on the world, drivers, thermals, other mods, and memory pressure.

## Current FLUX optimization

FLUX adjusts the vanilla client section-task queue so that first-time section mesh builds are favored sooner relative to repeat rebuilds. This is a small scheduling change, not a replacement for terrain generation or a complete chunk pipeline rewrite.

## Optional companion optimizations

For a fuller 26.2 Fabric performance stack, FLUX now advertises these optional companions:
- **C2ME** — parallelizes chunk generation, loading, and I/O. On a 2-core CPU, more concurrency is not automatically better; test for stutter and memory pressure.
- **Lithium** — optimizes game logic and ticking.
- **Sodium** — replaces the renderer and can improve frame rate and frame pacing, subject to GPU limits.

These are optional, separate mods. FLUX does not bundle or silently install them. Check that each downloaded file explicitly supports Minecraft 26.2 and Fabric. Back up worlds before testing major chunk-generation changes.

## Benchmark protocol

Compare vanilla Fabric against FLUX using the same world, seed, route, settings, and companion-mod set. Record average FPS, 1% lows, frame-time spikes, and the time for newly explored chunks to become visible. Run each pass at least three times after one warm-up pass.

The target profile is 16 render distance and 16 simulation distance. Do not interpret a successful Gradle build as proof of an in-game performance gain.

## Compatibility

Designed for Minecraft 26.2, Fabric Loader 0.19.3+, and Java 25+. Compatibility with renderer replacements and other mixin-heavy mods requires in-game testing.

## Build

Run `gradle build` (Windows: `gradle.bat build`). The mod JAR will be in `build/libs/`.
