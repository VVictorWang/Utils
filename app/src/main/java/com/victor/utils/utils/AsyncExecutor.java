package com.victor.utils.utils;
/**
 * Created by victor on 6/29/17.
 */

import java.util.concurrent.ExecutorService;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 异步执行
 */
public class AsyncExecutor {
    private static final String TAG = AsyncExecutor.class.getSimpleName();
    private static ExecutorService threadPool;
    public static Handler handler = new Handler(Looper.getMainLooper());

    public AsyncExecutor() {
        this(null);
    }

    public AsyncExecutor(ExecutorService threadPool) {
        if (AsyncExecutor.threadPool != null) {
            shutdownNow();
        }
        if (threadPool == null) {
            AsyncExecutor.threadPool = Executors.newCachedThreadPool();
        } else {
            AsyncExecutor.threadPool = threadPool;
        }
    }

    public static synchronized void shutdownNow() {
        if (threadPool != null && !threadPool.isShutdown()) threadPool.shutdownNow();
        threadPool = null;
    }

    /**
     * 将任务投入线程池执行
     *
     * @param worker
     * @return
     */
    public <T> FutureTask<T> execute(final Worker<T> worker) {
        Callable<T> call = new Callable<T>() {
            @Override
            public T call() throws Exception {
                return postResult(worker, worker.doInBackground());
            }
        };
        FutureTask<T> task = new FutureTask<T>(call) {
            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException e) {
                    LogUtils.e(TAG, e);
                    worker.abort();
                    postCancel(worker);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    LogUtils.e(TAG, e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("An error occured while executing doInBackground()", e.getCause());
                } catch (CancellationException e) {
                    worker.abort();
                    postCancel(worker);
                    LogUtils.e(TAG, e);
                    e.printStackTrace();
                }
            }
        };
        threadPool.execute(task);
        return task;
    }

    /**
     * 将子线程结果传递到UI线程
     *
     * @param worker
     * @param result
     * @return
     */
    private <T> T postResult(final Worker<T> worker, final T result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                worker.onPostExecute(result);
            }
        });
        return result;
    }

    /**
     * 将子线程结果传递到UI线程
     *
     * @param worker
     * @return
     */
    private void postCancel(final Worker worker) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                worker.onCanceled();
            }
        });
    }

    public <T> FutureTask<T> execute(Callable<T> call) {
        FutureTask<T> task = new FutureTask<T>(call);
        threadPool.execute(task);
        return task;
    }

    //暴露对外的执行接口
    public static abstract class Worker<T> {
        protected abstract T doInBackground();

        protected void onPostExecute(T data) {
        }

        protected void onCanceled() {
        }

        protected void abort() {
        }
    }
}

