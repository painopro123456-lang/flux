package dev.flux.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * When the render-task queue is heavily backlogged, prefer first-time section
 * builds over repeated rebuilds. Vanilla's scheduler already chooses the
 * nearest task; this only changes its rebuild quota during a large backlog.
 */
@Mixin(targets = "net.minecraft.client.renderer.chunk.SectionTaskDynamicQueue")
public abstract class SectionTaskDynamicQueueMixin {
    private static final int FLUX_BACKLOG_THRESHOLD = 64;

    @Shadow @Final private List<?> tasks;
    @Shadow private int recompileQuota;

    @Inject(method = "poll", at = @At("HEAD"))
    private void flux$prioritizeInitialSectionBuilds(@Coerce Object cameraPosition,
            CallbackInfoReturnable<Object> cir) {
        if (this.tasks.size() >= FLUX_BACKLOG_THRESHOLD) {
            this.recompileQuota = 0;
        }
    }
}
