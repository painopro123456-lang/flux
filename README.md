# FLUX

FLUX is a standalone client-side Fabric performance mod for Minecraft 26.2. It does not require C2ME, Lithium, or Sodium.

## Target hardware

- Intel Core i5-6200U (2 cores / 4 threads)
- NVIDIA GeForce 920MX
- 6 GB system RAM
- Target: **16 render distance + 16 simulation distance** (a goal, not a guarantee)

## Chunk scheduling

FLUX 0.2.4 removes the previous dedicated three-thread chunk-mesh pool. That pool ran alongside Minecraft's own world-generation workers and could compete with terrain generation for CPU time on low-core laptops. FLUX now leaves both chunk generation and mesh scheduling on Minecraft's native executors rather than adding an unmeasured competing pool.

This is a contention-reduction correction, not a claim that terrain generation has been accelerated. Faster terrain generation requires profiling the exact 26.2 generation pipeline and changing its scheduling safely. Startup speed also needs measurement; FLUX currently performs only lightweight initialization.

## Compatibility

- Minecraft 26.2
- Fabric Loader 0.19.3+
- Java 25+
- Fabric API for 26.2

No companion optimization mods are required.

## Benchmarking

A successful build verifies compilation only. In-game testing is still required to measure cold startup time, world-entry time, chunk visibility time, FPS, and frame-time spikes against the previous build and vanilla using the same world and settings. Run multiple passes after a warm-up.

## Build

Run `gradle build` (Windows: `gradle.bat build`). The mod JAR is in `build/libs/`.
