package dev.flux.mixin;

import dev.flux.nativebridge.NativeBridge;
import dev.flux.render.SectionOcclusionContext;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Builds a conservative full-cube occupancy mask from the immutable render region.
 * The native mask is consumed by ModelBlockRendererMixin during this same compile.
 */
@Mixin(targets = "net.minecraft.client.renderer.chunk.SectionCompiler")
public abstract class SectionCompilerMixin {
    @Inject(method = "compile", at = @At("HEAD"))
    private void flux$prepareNativeOcclusion(
            SectionPos sectionPos,
            RenderSectionRegion region,
            @Coerce Object vertexSorting,
            @Coerce Object builders,
            CallbackInfoReturnable<?> cir
    ) {
        SectionOcclusionContext.clear();
        int minX = sectionPos.minBlockX();
        int minY = sectionPos.minBlockY();
        int minZ = sectionPos.minBlockZ();
        byte[] occupancy = new byte[16 * 16 * 16];
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int solidCount = 0;

        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    pos.set(minX + x, minY + y, minZ + z);
                    BlockState state = region.getBlockState(pos);
                    if (state.isSolidRender() && !state.hasBlockEntity()) {
                        occupancy[x + 16 * (z + 16 * y)] = 1;
                        solidCount++;
                    }
                }
            }
        }

        // Skip the native pass for sparse sections where it is unlikely to pay back its scan cost.
        if (solidCount < 1024) return;

        byte[] mask = NativeBridge.findFullyOccludedBlocks(occupancy, 16, 16, 16);
        if (mask != null) {
            SectionOcclusionContext.set(minX, minY, minZ, mask);
        }
    }

    @Inject(method = "compile", at = @At("RETURN"))
    private void flux$clearNativeOcclusion(
            SectionPos sectionPos,
            RenderSectionRegion region,
            @Coerce Object vertexSorting,
            @Coerce Object builders,
            CallbackInfoReturnable<?> cir
    ) {
        SectionOcclusionContext.clear();
    }
}
