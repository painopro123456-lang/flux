package dev.flux;

import dev.flux.nativebridge.NativeBridge;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** FLUX Fabric entrypoint. */
public final class Flux implements ModInitializer {
    public static final String MOD_ID = "flux";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("FLUX {} initialized for Minecraft 26.2", BuildInfo.VERSION);
        if (NativeBridge.initialize()) {
            LOGGER.info("FLUX Rust mesh core is ready; live chunk-renderer integration is the next milestone.");
        } else {
            LOGGER.warn("FLUX native mesh core is unavailable; Minecraft's normal renderer remains active.");
        }
    }
}
