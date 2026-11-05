package dev.flux.mixin;

import net.minecraft.client.renderer.chunk.SectionTaskDynamicQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Limits consecutive section rebuilds so newly visible sections are not
 * held behind an uninterrupted run of rebuild tasks.
 */
@Mixin(SectionTaskDynamicQueue.class)
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
