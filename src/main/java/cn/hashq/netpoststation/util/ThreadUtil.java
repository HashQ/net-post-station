package cn.hashq.netpoststation.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ThreadUtil {

    /**
     * 可用CPU核心数(可同时执行线程数量)
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static class CustomerThreadFactory implements ThreadFactory {

        /**
         * 线程池数量
         */
        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;

        /**
         * 线程数量
         */
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final String threadTag;

        CustomerThreadFactory(String threadTag) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.threadTag = threadTag;
        }

        @Override
        public Thread newThread(Runnable target) {
            Thread t = new Thread(group, target, threadTag + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.MAX_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    /**
     * 空闲保活时间，单位：秒
     */
    private static final int KEEP_ALIVE_SECONDS = 30;

    /**
     * 有界队列size
     */
    private static final int QUEUE_SIZE = 10000;

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 0;

    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT;

    /**
     * CPU密集型线程池(核心线程数=最大线程数=CPU可用核心数)
     */
    private static class CpuIntenseTargetThreadPoolLazyHolder {

        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                MAXIMUM_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new CustomerThreadFactory("cpu"));

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread("CPU密集型任务线程池", new Callable() {
                @Override
                public Object call() throws Exception {
                    shutdownThreadPoolGracefully(EXECUTOR);
                    return null;
                }
            }));
        }

    }

    public static ThreadPoolExecutor getCpuIntenseTargetThreadPool() {
        return CpuIntenseTargetThreadPoolLazyHolder.EXECUTOR;
    }

    /**
     * IO线程池最大线程数
     */
    private static final int IO_MAX = Math.max(2, CPU_COUNT * 2);

    /**
     * IO线程池核心线程数
     */
    private static final int IO_CORE = 0;

    /**
     * IO密集型线程池(最大线程数=可用CPU核心数*2)
     */
    public static class IOIntenseTargetThreadPoolLazyHolder {
        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                IO_MAX,
                IO_MAX,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new CustomerThreadFactory("IO")
        );

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread("IO密集型任务线程池", new Callable() {
                @Override
                public Object call() throws Exception {
                    shutdownThreadPoolGracefully(EXECUTOR);
                    return null;
                }
            }));
        }

    }


    public static ThreadPoolExecutor getIOIntenseTargetThreadPool() {
        return IOIntenseTargetThreadPoolLazyHolder.EXECUTOR;
    }


    /**
     * 混合型线程池核心线程数
     */
    private static final int MIXED_CORE = 0;

    private static final int MIXED_MAX = 128;

    private static class MixedTargetThreadPoolLazyHolder {
        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                MIXED_MAX,
                MIXED_MAX,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new CustomerThreadFactory("mixed"));

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread("混合型任务线程池", new Callable() {
                @Override
                public Object call() throws Exception {
                    shutdownThreadPoolGracefully(EXECUTOR);
                    return null;
                }
            }));
        }
    }

    public static ThreadPoolExecutor getMixedTargetThreadPool() {
        return MixedTargetThreadPoolLazyHolder.EXECUTOR;
    }

    static class SeqOrScheduledTargetThreadPoolLazyHolder {
        static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(
                1,
                new CustomerThreadFactory("seq"));

        static {
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread("定时和顺序任务线程池", new Callable() {
                @Override
                public Object call() throws Exception {
                    shutdownThreadPoolGracefully(EXECUTOR);
                    return null;
                }
            }));
        }
    }

    public static ScheduledThreadPoolExecutor getSeqOrScheduledExecutorService() {
        return SeqOrScheduledTargetThreadPoolLazyHolder.EXECUTOR;
    }


    public static void shutdownThreadPoolGracefully(ExecutorService threadPool) {
        if (!(threadPool instanceof ExecutorService) || threadPool.isTerminated()) {
            return;
        }
        try {
            // 拒绝接受新任务
            threadPool.shutdown();
        } catch (SecurityException e) {
            return;
        } catch (NullPointerException e) {
            return;
        }
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                // 取消正在执行的任务
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("线程池任务未正常执行结束");
                }
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
        if (!threadPool.isTerminated()) {
            try {
                for (int i = 0; i < 1000; i++) {
                    if (threadPool.awaitTermination(10, TimeUnit.MILLISECONDS))
                        break;
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.error("关闭线程池异常:{}", e.getMessage());
            } catch (Throwable e) {
                log.error("关闭线程池异常:{}", e.getMessage());
            }
        }

    }

    static class ShutdownHookThread extends Thread {
        private volatile boolean hasShutdown = false;

        private static AtomicInteger shutdownTimes = new AtomicInteger(0);

        private final Callable callback;

        public ShutdownHookThread(String name, Callable callback) {
            super("JVM推出钩子(" + name + ")");
            this.callback = callback;
        }

        @Override
        public void run() {
            synchronized (this) {
                if (!this.hasShutdown) {
                    this.hasShutdown = true;
                    long beginTime = System.currentTimeMillis();
                    try {
                        callback.call();
                    } catch (Exception e) {
                        log.error("{} error:{}", getName(), e.getMessage());
                    }
                    long consumingTimeTotal = System.currentTimeMillis() - beginTime;
                    log.info("{} 耗时(ms):{}", getName(), consumingTimeTotal);
                }
            }
        }
    }
}
