# FLUX

FLUX is a standalone client-side Fabric performance mod for Minecraft 26.2. It does not require C2ME, Lithium, or Sodium.

## Target hardware

- Intel Core i5-6200U (2 cores / 4 threads)
- NVIDIA GeForce 920MX
- 6 GB system RAM
- Target: **16 render distance + 16 simulation distance** (a goal, not a guarantee)

## Chunk mesh scheduling

FLUX routes client chunk mesh compilation to a dedicated worker pool instead of Minecraft's shared background executor. The default worker count matches vanilla's background-pool sizing: max(1, logical CPU count - 1). On a 4-logical-thread CPU this gives 3 mesh workers, rather than serializing mesh work through one worker.

This isolates mesh compilation from unrelated background tasks, but it does not parallelize terrain generation, disk I/O, or Minecraft's main game tick. It therefore cannot guarantee faster first-time terrain generation or 16-chunk performance on every system. Worker oversubscription can also hurt performance, so this default should be benchmarked on the target laptop.

The executor redirection is based on the MIT-licensed Multicore Magic project by the-real-ltcg; see NOTICE for attribution and license terms.

## Compatibility

- Minecraft 26.2
- Fabric Loader 0.19.3+
- Java 25+
- Fabric API for 26.2

No companion optimization mods are required.

## Benchmarking

A successful build verifies compilation only. In-game testing is still required to measure FPS, 1% lows, frame-time spikes, and chunk visibility time against vanilla using the same world, route, and settings. Run multiple passes after a warm-up.

## Build

Run `gradle build` (Windows: `gradle.bat build`). The mod JAR is in `build/libs/`.
