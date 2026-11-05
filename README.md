# FLUX

FLUX is a lightweight Fabric compatibility and performance helper for Minecraft 26.2.

## Target hardware profile

- Intel Core i5-6200U (2 cores / 4 threads)
- NVIDIA GeForce 920MX
- 6 GB system RAM

The target remains **16 render distance + 16 simulation distance**. This is a goal, not a guaranteed result on this hardware.

## Chunk loading

FLUX does not replace Minecraft's chunk-generation engine. Its previous section-queue mixin was a no-op in 0.2.1 and has been removed.

For actual chunk generation, loading, and I/O optimization, install **C2ME for Minecraft 26.2 Fabric** alongside FLUX:
- Official project and downloads: https://modrinth.com/mod/c2me-fabric
- Select a file explicitly marked **26.2 + Fabric**. Do not use a 26.3 build.
- C2ME is a separate mod and is not bundled inside the FLUX JAR.
- Back up existing worlds before testing experimental C2ME builds.

FLUX detects C2ME at startup and reports whether it is present. Without C2ME, FLUX does not claim a chunk-generation speedup.

## Hardware tuning notes

This laptop has 2 CPU cores and 6 GB total RAM, with Windows already using a substantial portion. Do not force a large C2ME worker count or allocate most system memory to Minecraft. Start with C2ME's automatic worker setting and the default config; change one setting at a time only after testing for stutter and memory pressure.

## Benchmark protocol

Compare vanilla Fabric against Fabric + FLUX + C2ME using the same world, seed, route, settings, and warm-up. Record average FPS, 1% lows, frame-time spikes, and time for newly explored chunks to become visible. Run at least three passes.

Published C2ME benchmarks use much stronger hardware and must not be treated as expected results for this laptop.

## Compatibility

Designed for Minecraft 26.2, Fabric Loader 0.19.3+, and Java 25+.

## Build

Run `gradle build` (Windows: `gradle.bat build`). The mod JAR will be in `build/libs/`.
