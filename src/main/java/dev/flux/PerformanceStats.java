package dev.flux;

import java.util.Arrays;

/**
 * Lightweight client-side measurements used to identify frame-time spikes
 * that coincide with incoming chunk work. This class does not alter vanilla
 * chunk scheduling.
 */
public final class PerformanceStats {
    private static final int SAMPLE_COUNT = 120;
    private static final double[] FRAME_TIMES = new double[SAMPLE_COUNT];

    private static int sampleIndex;
    private static int samples;
    private static long lastFrameNanos;
    private static long chunkWindowStart = System.nanoTime();
    private static int chunksLoadedInWindow;
    private static int chunksUnloadedInWindow;
    private static int chunksLoadedPerSecond;
    private static int chunksUnloadedPerSecond;
    private static double averageFrameMs;
    private static double onePercentLowFps;

    private PerformanceStats() {}

    public static void onChunkLoad() {
        chunksLoadedInWindow++;
        updateChunkWindow();
    }

    public static void onChunkUnload() {
        chunksUnloadedInWindow++;
        updateChunkWindow();
    }

    public static void onFrame() {
        long now = System.nanoTime();
        if (lastFrameNanos != 0L) {
            double frameMs = (now - lastFrameNanos) / 1_000_000.0;
            if (frameMs > 0.0 && frameMs < 10_000.0) {
                FRAME_TIMES[sampleIndex] = frameMs;
                sampleIndex = (sampleIndex + 1) % SAMPLE_COUNT;
                samples = Math.min(samples + 1, SAMPLE_COUNT);
                recalculate();
            }
        }
        lastFrameNanos = now;
        updateChunkWindow();
    }

    private static void updateChunkWindow() {
        long now = System.nanoTime();
        if (now - chunkWindowStart >= 1_000_000_000L) {
            chunksLoadedPerSecond = chunksLoadedInWindow;
            chunksUnloadedPerSecond = chunksUnloadedInWindow;
            chunksLoadedInWindow = 0;
            chunksUnloadedInWindow = 0;
            chunkWindowStart = now;
        }
    }

    private static void recalculate() {
        double total = 0.0;
        double[] sorted = Arrays.copyOf(FRAME_TIMES, samples);
        for (double frame : sorted) total += frame;
        averageFrameMs = total / samples;
        Arrays.sort(sorted);
        int percentileIndex = Math.min(sorted.length - 1,
                (int) Math.ceil(sorted.length * 0.99) - 1);
        double slowFrameMs = sorted[percentileIndex];
        onePercentLowFps = slowFrameMs <= 0.0 ? 0.0 : 1000.0 / slowFrameMs;
    }

    public static double averageFrameMs() { return averageFrameMs; }
    public static double onePercentLowFps() { return onePercentLowFps; }
    public static int chunksLoadedPerSecond() { return chunksLoadedPerSecond; }
    public static int chunksUnloadedPerSecond() { return chunksUnloadedPerSecond; }
    public static int sampleCount() { return samples; }
}
