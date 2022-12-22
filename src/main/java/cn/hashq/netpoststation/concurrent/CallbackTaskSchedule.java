package cn.hashq.netpoststation.concurrent;

import cn.hashq.netpoststation.util.ThreadUtil;
import com.google.common.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

public class CallbackTaskSchedule {

    static ListeningExecutorService guavaPool = null;

    static {
        ThreadPoolExecutor jPool = ThreadUtil.getCpuIntenseTargetThreadPool();
        guavaPool = MoreExecutors.listeningDecorator(jPool);
    }

    private CallbackTaskSchedule() {
    }

    public static <R> void add(CallbackTask<R> executeTask) {
        ListenableFuture<R> future = guavaPool.submit(new Callable<R>() {
            @Override
            public R call() throws Exception {
                R r = executeTask.execute();
                return r;
            }
        });
        Futures.addCallback(future, new FutureCallback<R>() {
            @Override
            public void onSuccess(R r) {
                executeTask.onBack(r);
            }

            @Override
            public void onFailure(Throwable throwable) {
                executeTask.onException(throwable);
            }
        });
    }
}
