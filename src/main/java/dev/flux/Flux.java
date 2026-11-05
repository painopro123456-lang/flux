package dev.flux;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FLUX compatibility entry point. Chunk generation is handled by the
 * optional C2ME companion rather than by a fake client-side scheduler mixin.
 */
public final class Flux implements ModInitializer {
    public static final String MOD_ID = "flux";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("FLUX {} initialized for Minecraft 26.2", BuildInfo.VERSION);

        if (FabricLoader.getInstance().isModLoaded("c2me")) {
            LOGGER.info("C2ME detected: parallel chunk generation/loading optimizations are active.");
        } else {
            LOGGER.warn("C2ME is not installed. FLUX alone does not accelerate terrain generation or chunk I/O.");
            LOGGER.warn("For chunk-loading optimization on Minecraft 26.2, install the matching Fabric C2ME build.");
        }
    }
}
