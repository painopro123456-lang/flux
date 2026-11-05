package dev.flux.mixin;

import net.minecraft.client.render.chunk.ChunkRenderTaskScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Slightly favors first-time chunk meshes over repeated rebuilds when both
 * kinds of work are queued. This helps newly visible terrain appear sooner.
 */
@Mixin(ChunkRenderTaskScheduler.class)
abstract class ChunkRenderTaskSchedulerMixin {
    @ModifyConstant(
            method = "poll",
            constant = @Constant(intValue = 2),
            require = 1
    )
    private int flux$prioritizeInitialChunkBuilds(int vanillaQuota) {
        return 1;
    }
}
