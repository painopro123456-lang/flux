package dev.flux;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public final class FluxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> PerformanceStats.onChunkLoad());
        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> PerformanceStats.onChunkUnload());

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(Flux.MOD_ID, "performance"),
                FluxHud::extract
        );

        Flux.LOGGER.info("FLUX client diagnostics enabled");
    }
}
