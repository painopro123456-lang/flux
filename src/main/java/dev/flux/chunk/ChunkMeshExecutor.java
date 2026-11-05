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
 * Match vanilla's background pool sizing by default instead of starving mesh
 * compilation with a single worker on a 4-thread laptop CPU.
 */
public final class ChunkMeshExecutor {
    private static final int THREAD_COUNT =
            Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    private static final AtomicInteger THREAD_IDS = new AtomicInteger();

    private static final ThreadFactory THREAD_FACTORY = task -> {
        Thread thread = new Thread(task,
                "FLUX Chunk Mesh #" + THREAD_IDS.incrementAndGet());
        thread.setDaemon(true);
        thread.setPriority(Thread.NORM_PRIORITY);
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
