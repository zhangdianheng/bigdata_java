package com.zdh.multithread;

import cn.hutool.core.thread.ExecutorBuilder;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zdh
 * @date 2022-06-30 14:17
 * @Version 1.0
 */
public class HutoolThreadPool {
    public static void main(String[] args) {
        ThreadPoolExecutor build = ExecutorBuilder.create()
                .setCorePoolSize(5)
                .setMaxPoolSize(10)
                .setKeepAliveTime(30, TimeUnit.SECONDS)
                .setWorkQueue(new LinkedBlockingDeque<>())
                .setHandler(new ThreadPoolExecutor.AbortPolicy())
                .build();

        try {

            for (int i = 0; i < 10; i++) {
                build.execute(()->handler());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            build.shutdown();
        }
    }

    public static void handler() {
        //打印当前线程名字
        System.out.println("当前执行线程：" + Thread.currentThread().getName());

    }
}
