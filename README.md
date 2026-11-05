# FLUX

FLUX is a standalone client-side Fabric performance project for Minecraft 26.2.

## Rust rendering core — first integration milestone

The project contains a Rust cdylib and a Java JNI loader. On Windows x64, the mod extracts the packaged flux.dll into Fabric's config directory and loads it. The native API accepts a byte-per-cell occupancy snapshot for opaque full cubes and returns exposed-face descriptors. Internal faces between adjacent solid cells are culled in Rust.

This is the foundation of the Rust mesh pipeline, not yet a replacement for Minecraft's live chunk renderer. The current Minecraft renderer still owns model baking, block states, textures, lighting, biome tint, transparency, fluids, GPU buffer upload, and draw submission. The next integration step is to connect real section snapshots to this API and route supported geometry back into Minecraft while retaining a vanilla fallback for unsupported models.

## Build

The GitHub Actions workflow builds the Rust DLL for Windows x64, runs Rust unit tests, packages the DLL into the Fabric JAR, and builds the mod with Java 25 / Gradle.

## Compatibility and performance

- Minecraft 26.2, Fabric Loader 0.19.3+, Java 25.
- No C2ME dependency.
- Native engine currently targets Windows x64.
- Rust currently handles exposed-face extraction for opaque full-cube occupancy only.
- No chunk-load speedup or in-game performance gain is claimed until the Minecraft section-renderer integration is complete and benchmarked.
