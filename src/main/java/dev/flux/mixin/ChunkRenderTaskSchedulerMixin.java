package dev.flux.mixin;

import net.minecraft.client.renderer.chunk.SectionTaskDynamicQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Keeps the vanilla section rebuild quota unchanged until a measured,
 * workload-specific scheduling improvement is available.
 */
@Mixin(SectionTaskDynamicQueue.class)
abstract class ChunkRenderTaskSchedulerMixin {
    @ModifyConstant(
            method = "poll",
            constant = @Constant(intValue = 2),
            require = 1
    )
    private int flux$keepVanillaRebuildQuota(int vanillaQuota) {
        return vanillaQuota;
    }
}
