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
        if (!NativeBridge.initialize()) {
            LOGGER.warn("FLUX native mesh core is unavailable; Minecraft's normal renderer remains active.");
            return;
        }

        int[] testMesh = NativeBridge.buildGreedyOpaqueMesh(
                new byte[] {1, 1, 1, 1, 1, 1, 1, 1}, 2, 2, 2);
        if (testMesh != null && testMesh.length == 36) {
            LOGGER.info("FLUX Rust JNI self-test passed: a solid 2x2x2 cube produced six greedy quads.");
        } else {
            LOGGER.error("FLUX Rust JNI self-test failed; native mesh output was invalid. Vanilla rendering remains active.");
        }
        LOGGER.info("Rust mesh core loaded. Live Minecraft section-compiler integration remains the next milestone.");
    }
}
