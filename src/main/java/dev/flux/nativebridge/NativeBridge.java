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
 * The first native milestone supports opaque full-cube occupancy snapshots.
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
            Flux.LOGGER.info("FLUX Rust native mesh engine loaded from {}", library);
            return true;
        } catch (IOException | UnsatisfiedLinkError | SecurityException exception) {
            Flux.LOGGER.error("Could not load FLUX Rust engine; Minecraft will keep its normal renderer.", exception);
            return false;
        }
    }

    /**
     * Returns [x,y,z,faceId] tuples for exposed faces. Null means native unavailable.
     * Occupancy layout: x + width * (z + depth * y); non-zero cells are solid.
     */
    public static int[] buildOpaqueMesh(byte[] occupancy, int width, int height, int depth) {
        if (!loaded || occupancy == null || width <= 0 || height <= 0 || depth <= 0) return null;
        long expected = (long) width * height * depth;
        if (expected != occupancy.length) {
            throw new IllegalArgumentException("Occupancy length must equal width * height * depth");
        }
        return buildOpaqueMeshNative(occupancy, width, height, depth);
    }

    private static native int[] buildOpaqueMeshNative(byte[] occupancy, int width, int height, int depth);
}
