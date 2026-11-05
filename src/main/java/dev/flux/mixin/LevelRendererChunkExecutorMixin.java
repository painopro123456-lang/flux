package dev.flux.mixin;

import dev.flux.chunk.ChunkMeshExecutor;
import net.minecraft.TracingExecutor;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Routes chunk mesh compilation away from the shared world-generation executor.
 * Target verified against the Minecraft 26.2 LevelRenderer mapping used by the
 * Multicore Magic 26.2 implementation (MIT-licensed; attribution in NOTICE).
 */
@Mixin(LevelRenderer.class)
abstract class LevelRendererChunkExecutorMixin {
    @Redirect(
            method = "invalidateCompiledGeometry",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Util;backgroundExecutor()Lnet/minecraft/TracingExecutor;"
            ),
            require = 1
    )
    private TracingExecutor flux$useDedicatedChunkMeshExecutor() {
        return ChunkMeshExecutor.executor();
    }
}
