# FLUX

FLUX is a standalone client-side Fabric performance project for Minecraft 26.2.

## Rust rendering core — first integration milestone

The project contains a Rust cdylib and a Java JNI loader. On Windows x64, the mod extracts the packaged flux.dll into Fabric's config directory and loads it. Rust provides greedy opaque-cube meshing and a full-occlusion mask. Minecraft section compilation now builds a conservative occupancy snapshot and skips model tessellation only for opaque full cubes fully enclosed inside a dense section. Block entities and non-full-cube models remain on vanilla's path.

FLUX does not replace the entire Minecraft renderer. Minecraft still owns model baking, textures, lighting, biome tint, transparency, fluids, GPU buffer upload, and draw submission. Rust currently accelerates one conservative culling step in the live section compiler; it does not yet generate all final Minecraft vertices. Full geometry replacement requires preserving material, light, tint, fluid, and custom-model data.

## Build

The GitHub Actions workflow builds the Rust DLL for Windows x64, runs Rust unit tests, packages the DLL into the Fabric JAR, and builds the mod with Java 25 / Gradle.

## Compatibility and performance

- Minecraft 26.2, Fabric Loader 0.19.3+, Java 25.
- No C2ME dependency.
- Native engine currently targets Windows x64.
- Rust currently handles greedy opaque-cube meshing and section occlusion analysis.
- The new section-compiler hook must be benchmarked in-game before any speedup is claimed.
