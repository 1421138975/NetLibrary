package com.dfzt.network.manager;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程池
 *
 * 比方说线程池的核心线程数是3个 ,你往线程池中添加任务超过3分的话就会往阻塞队列中添加,当阻塞队列满了
 * 就会新启线程来执行任务 (新启的线程数不能大于最大线程数) 如果说最大线程数也满了 就会启动线程池的
 * 拒绝策略
 *
 *
 * 拒绝策略有4中  DiscardOldestPolicy 直接丢弃最老的那个问题(也就是阻塞队列最前面的那个数据)
 *               AbortPolicy 直接抛出一起(线程池默认的策略)
 *               CallerRunsPolicy 让调用者线程去执行(你当前线程既然有空闲时间往我这里传数据,我已经满了,你自己执行吧)
 *               DiscardPolicy  把最新提交的任务直接丢弃
 *
 *Runtime.getRuntime().availableProcessors()//获取机器的CPU核心数
 *
 * 任务特性：
 *    CPU密集型：(CPU在不停的计算,从内存中取数计算) 线程池的最大线程数不要超过CUP的核心数 (最大核心数+1)
 *    IO密集型：(网络通信,读写磁盘) 线程池的最大线程数机器的CPU核心数*2
 *    混合型：(既有CPU又有IO)
 */
public class ThreadPoolManager {

    //定义核心线程数
    private int corePoolSize = 3;
    //最大线程数
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2;
    //非核心线程空闲时间
    private int keepTime = 5;
    //非核心线程空闲线程等待时间单位
    private TimeUnit unit = TimeUnit.SECONDS;
    //阻塞队列
    private BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue(maximumPoolSize);
    //使用了volatile关键字就表示这个变量的可见性
    private volatile static ThreadPoolManager mThreadPoolManager;
    //定义一个线程的工厂
    private ThreadFactory threadFactory = new ThreadFactory() {
        AtomicInteger integer = new AtomicInteger();
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable,"生成线程的编号为："+ integer.getAndIncrement());
        }
    };
    private ThreadPoolExecutor mThreadPoolExecutor;
    private ThreadPoolManager(){
        //创建线程池
        if (mThreadPoolExecutor == null || mThreadPoolExecutor.isShutdown() || mThreadPoolExecutor.isTerminated()) {
            synchronized (ThreadPoolManager.class) {
                if (mThreadPoolExecutor == null || mThreadPoolExecutor.isShutdown() || mThreadPoolExecutor.isTerminated()) {
                    mThreadPoolExecutor = new
                            ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepTime,unit,blockingQueue,threadFactory);
                }
            }
        }

    }

    public static ThreadPoolManager getInstance(){
        if (mThreadPoolManager == null){
            synchronized (ThreadPoolManager.class){
                if (mThreadPoolManager == null){
                    mThreadPoolManager = new ThreadPoolManager();
                }
            }
        }
        return mThreadPoolManager;
    }

    //开始任务
    public void execute(Runnable runnable){
        if(mThreadPoolExecutor != null) {
            mThreadPoolExecutor.execute(runnable);
        }
    }

}
