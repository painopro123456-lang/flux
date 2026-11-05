package dev.flux;

import dev.flux.chunk.ChunkMeshExecutor;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FLUX standalone client-side chunk mesh scheduling optimization.
 * Terrain generation and chunk I/O remain Minecraft's responsibility.
 */
public final class Flux implements ModInitializer {
    public static final String MOD_ID = "flux";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("FLUX {} initialized for Minecraft 26.2", BuildInfo.VERSION);
        LOGGER.info("Dedicated chunk-mesh executor active with {} worker(s).",
                ChunkMeshExecutor.threadCount());
        LOGGER.info("FLUX runs standalone; no companion performance mods are required.");
    }
}
