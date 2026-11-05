package dev.flux.render;

import net.minecraft.core.BlockPos;

public final class SectionOcclusionContext {
    private static final ThreadLocal<byte[]> MASK = new ThreadLocal<>();
    private static final ThreadLocal<int[]> ORIGIN = new ThreadLocal<>();
    private SectionOcclusionContext() {}
    public static void set(int x, int y, int z, byte[] mask) { ORIGIN.set(new int[]{x,y,z}); MASK.set(mask); }
    public static boolean shouldSkip(BlockPos pos) {
        byte[] mask = MASK.get(); int[] origin = ORIGIN.get();
        if (mask == null || origin == null) return false;
        int x=pos.getX()-origin[0], y=pos.getY()-origin[1], z=pos.getZ()-origin[2];
        if(x<0||x>=16||y<0||y>=16||z<0||z>=16) return false;
        return mask[x+16*(z+16*y)] != 0;
    }
    public static void clear() { MASK.remove(); ORIGIN.remove(); }
}
