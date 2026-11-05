package dev.flux.chunk;

import net.minecraft.TracingExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dedicated executor for client chunk mesh compilation.
 *
 * This isolates mesh compilation from Minecraft's shared background executor.
 * The worker count is intentionally conservative: on a 4-logical-thread CPU,
 * FLUX uses one mesh worker so the game and terrain generation retain CPU time.
 */
public final class ChunkMeshExecutor {
    private static final int THREAD_COUNT =
            Math.max(1, Runtime.getRuntime().availableProcessors() / 4);

    private static final AtomicInteger THREAD_IDS = new AtomicInteger();

    private static final ThreadFactory THREAD_FACTORY = task -> {
        Thread thread = new Thread(task,
                "FLUX Chunk Mesh #" + THREAD_IDS.incrementAndGet());
        thread.setDaemon(true);
        thread.setPriority(Thread.NORM_PRIORITY - 1);
        return thread;
    };

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            THREAD_COUNT,
            THREAD_COUNT,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            THREAD_FACTORY
    );

    private static final TracingExecutor EXECUTOR = new TracingExecutor(POOL);

    private ChunkMeshExecutor() {
    }

    public static TracingExecutor executor() {
        return EXECUTOR;
    }

    public static int threadCount() {
        return THREAD_COUNT;
    }
}
