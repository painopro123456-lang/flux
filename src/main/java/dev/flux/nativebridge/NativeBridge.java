package dev.flux.nativebridge;

import dev.flux.BuildInfo;
import dev.flux.Flux;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Thin Windows x64 JNI bridge to FLUX's Rust chunk-mesh core.
 * Native mesh input is a byte-per-cell opaque full-cube occupancy snapshot.
 */
public final class NativeBridge {
    private static volatile boolean loaded;

    private NativeBridge() {
    }

    public static synchronized boolean initialize() {
        if (loaded) return true;
        String os = System.getProperty("os.name", "").toLowerCase();
        String arch = System.getProperty("os.arch", "").toLowerCase();
        if (!os.contains("win") || !(arch.equals("amd64") || arch.equals("x86_64"))) {
            Flux.LOGGER.info("FLUX Rust engine is packaged for Windows x64; vanilla rendering remains active.");
            return false;
        }

        String resource = "/natives/windows-x86_64/flux.dll";
        Path library = FabricLoader.getInstance().getConfigDir()
                .resolve("flux").resolve("native").resolve(BuildInfo.VERSION).resolve("flux.dll");
        try (InputStream input = NativeBridge.class.getResourceAsStream(resource)) {
            if (input == null) {
                Flux.LOGGER.warn("FLUX native library resource is missing: {}", resource);
                return false;
            }
            Files.createDirectories(library.getParent());
            Files.copy(input, library, StandardCopyOption.REPLACE_EXISTING);
            System.load(library.toAbsolutePath().toString());
            loaded = true;
            Flux.LOGGER.info("FLUX Rust greedy mesh engine loaded from {}", library);
            return true;
        } catch (IOException | UnsatisfiedLinkError | SecurityException exception) {
            Flux.LOGGER.error("Could not load FLUX Rust engine; Minecraft will keep its normal renderer.", exception);
            return false;
        }
    }

    /**
     * Builds greedy quads for opaque full-cube occupancy.
     * Output is a flat int array of six-int records:
     * [x, y, z, faceId, width, height].
     * Face IDs: 0=-X, 1=+X, 2=-Y, 3=+Y, 4=-Z, 5=+Z.
     * Returns null if the native engine is unavailable.
     */
    public static int[] buildGreedyOpaqueMesh(byte[] occupancy, int width, int height, int depth) {
        if (!loaded || occupancy == null || width <= 0 || height <= 0 || depth <= 0) return null;
        long expected = (long) width * height * depth;
        if (expected != occupancy.length) {
            throw new IllegalArgumentException("Occupancy length must equal width * height * depth");
        }
        return buildGreedyOpaqueMeshNative(occupancy, width, height, depth);
    }

    /**
     * Returns a byte mask marking solid cells fully surrounded by solid cells.
     * The caller must only use this to skip opaque full-cube model tessellation.
     */
    public static byte[] findFullyOccludedBlocks(byte[] occupancy, int width, int height, int depth) {
        if (!loaded || occupancy == null || width <= 0 || height <= 0 || depth <= 0) return null;
        long expected = (long) width * height * depth;
        if (expected != occupancy.length) {
            throw new IllegalArgumentException("Occupancy length must equal width * height * depth");
        }
        return findFullyOccludedBlocksNative(occupancy, width, height, depth);
    }

    private static native byte[] findFullyOccludedBlocksNative(byte[] occupancy, int width, int height, int depth);

    private static native int[] buildGreedyOpaqueMeshNative(byte[] occupancy, int width, int height, int depth);
}
