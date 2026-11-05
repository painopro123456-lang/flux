package dev.flux;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.Locale;

final class FluxHud {
    private static final int LEFT = 8;
    private static final int TOP = 8;
    private static final int PANEL_WIDTH = 154;
    private static final int PANEL_HEIGHT = 66;

    private FluxHud() {}

    static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.level == null) return;

        PerformanceStats.onFrame();

        int fps = minecraft.getFps();
        String average = String.format(Locale.ROOT, "%.1f ms", PerformanceStats.averageFrameMs());
        String low = PerformanceStats.sampleCount() < 30
                ? "warming up"
                : String.format(Locale.ROOT, "%.0f FPS", PerformanceStats.onePercentLowFps());

        graphics.fill(LEFT, TOP, LEFT + PANEL_WIDTH, TOP + PANEL_HEIGHT, 0xB8101218);
        graphics.fill(LEFT, TOP, LEFT + 2, TOP + PANEL_HEIGHT, 0xFF63D6C5);
        graphics.text(minecraft.font, "FLUX  /  PERFORMANCE", LEFT + 8, TOP + 6, 0xFF63D6C5, false);
        graphics.text(minecraft.font, "FPS  " + fps + "    FRAME  " + average, LEFT + 8, TOP + 22, 0xFFF2F4F8, false);
        graphics.text(minecraft.font, "1% LOW  " + low, LEFT + 8, TOP + 36, 0xFFE0E5ED, false);
        graphics.text(minecraft.font, "CHUNKS  +" + PerformanceStats.chunksLoadedPerSecond()
                + "  /  -" + PerformanceStats.chunksUnloadedPerSecond(),
                LEFT + 8, TOP + 50, 0xFFB5C0CE, false);
    }
}
