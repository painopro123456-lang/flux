package dev.flux.mixin;

import dev.flux.render.SectionOcclusionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Avoids invoking model tessellation for an opaque full cube that Rust proved
 * has all six neighbours filled inside the current section.
 */
@Mixin(targets = "net.minecraft.client.renderer.block.ModelBlockRenderer")
public abstract class BlockModelRendererMixin {
    @Inject(method = "tesselateBlock", at = @At("HEAD"), cancellable = true)
    private void flux$skipFullyOccludedCube(
            @Coerce Object output,
            float x,
            float y,
            float z,
            @Coerce Object renderRegion,
            BlockPos pos,
            BlockState state,
            @Coerce Object model,
            long seed,
            CallbackInfo ci
    ) {
        if (state.isSolidRender() && !state.hasBlockEntity()
                && SectionOcclusionContext.shouldSkip(pos)) {
            ci.cancel();
        }
    }
}
