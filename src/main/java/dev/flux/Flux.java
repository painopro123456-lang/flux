package dev.flux;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FLUX standalone client performance mod.
 * It deliberately leaves Minecraft's chunk-generation and mesh executors
 * under vanilla control until a measured, version-specific replacement exists.
 */
public final class Flux implements ModInitializer {
    public static final String MOD_ID = "flux";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("FLUX {} initialized for Minecraft 26.2", BuildInfo.VERSION);
        LOGGER.info("Vanilla chunk-generation and mesh scheduling preserved; no extra worker pool started.");
        LOGGER.info("FLUX runs standalone; no companion performance mods are required.");
    }
}
