# FLUX

FLUX is a client-side Fabric performance mod for Minecraft 26.2.

## Target hardware profile

The target test laptop profile is:
- Intel Core i5-6200U (2 cores / 4 threads)
- NVIDIA GeForce 920MX
- 6 GB system RAM

The performance target is **16 render distance + 16 simulation distance**. This is a target, not a guarantee.

## Current status

FLUX 0.2.1 restores the vanilla section rebuild quota. The earlier quota reduction was not supported by in-game benchmark evidence and could make chunk rebuild throughput worse. FLUX does not currently claim a measured FPS or chunk-loading improvement over vanilla.

## Optional companion optimizations

These are separate, optional mods and are not bundled:
- **C2ME** — parallelizes chunk generation, loading, and I/O. On a 2-core CPU, extra concurrency can also increase contention and memory pressure.
- **Lithium** — optimizes game logic and ticking.
- **Sodium** — replaces the renderer and may improve rendering performance, depending on the GPU and settings.

Only install builds explicitly compatible with Minecraft 26.2 and Fabric. Back up worlds before testing chunk-generation mods.

## Benchmark protocol

Compare vanilla Fabric against FLUX using the same world, seed, route, settings, and companion-mod set. Record average FPS, 1% lows, frame-time spikes, and the time for newly explored chunks to become visible. Run at least three passes after a warm-up pass.

A successful Gradle build is not proof of an in-game performance gain.

## Compatibility

Designed for Minecraft 26.2, Fabric Loader 0.19.3+, and Java 25+. Compatibility with renderer replacements and other mixin-heavy mods requires in-game testing.

## Build

Run `gradle build` (Windows: `gradle.bat build`). The mod JAR will be in `build/libs/`.
