package com.zdh.multithread;

import java.util.concurrent.*;

/**
 * @author zdh
 * @date 2022-06-30 14:14
 * @Version 1.0
 */
public class ThreadDemo {

    public static void main(String[] args) {
        //核心线程数
        int corePoolSize = 30;
        //最大线程数
        int maximumPoolSize = 60;
        //超过 corePoolSize 线程数量的线程最大空闲时间
        long keepAliveTime = 2;
        //以秒为时间单位
        TimeUnit unit = TimeUnit.SECONDS;
        //创建工作队列，用于存放提交的等待执行任务
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor threadPoolExecutor = null;
        try {
            //创建线程池
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    unit,
                    workQueue,
                    new ThreadPoolExecutor.CallerRunsPolicy());

            //循环提交任务
            Demo demo = new Demo();
            for (int i = 0; i < 10000; i++) {
                //提交任务的索引
                threadPoolExecutor.submit(() -> {
                   demo.getA();
                   demo.getB();
                });
                //每个任务提交后休眠500ms再提交下一个任务，用于保证提交顺序
            }
        }  finally {
            threadPoolExecutor.shutdown();
        }
    }
}
