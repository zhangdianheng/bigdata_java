package com.zdh.disruptor.trade.multi;

import cn.hutool.core.thread.ThreadUtil;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        RingBuffer<Order> ringBuffer =
                RingBuffer.create(ProducerType.MULTI,
                        new EventFactory<Order>() {
                            @Override
                            public Order newInstance() {
                                return new Order();
                            }
                        },1024*1024,new YieldingWaitStrategy());
        SequenceBarrier barrier = ringBuffer.newBarrier();
        Consumer[] consumers = new Consumer[5];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer("c" + i);
        }
        WorkerPool<Order> workerPool =
                new WorkerPool<Order>(ringBuffer,
                        barrier,
                        new IntEventExceptionHandler(),
                        consumers);
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        workerPool.start(new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build()));
        ThreadPoolExecutor executor = ThreadUtil.newExecutor(5, 100);
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            for (int i = 0; i < 100; i++) {
                final Producer p = new Producer(ringBuffer);
                executor.execute(()->{
                    try {
                        latch.await();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    for (int j = 0; j < 100; j++) {
                        p.onData(UUID.randomUUID().toString());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        Thread.sleep(2000);
        System.out.println("---------开始生产-------");
        latch.countDown();
        Thread.sleep(5000);
        System.out.println("总数 : " + consumers[0].getCount());
    }
    static class IntEventExceptionHandler implements ExceptionHandler{

        @Override
        public void handleEventException(Throwable throwable, long l, Object o) {

        }

        @Override
        public void handleOnStartException(Throwable throwable) {

        }

        @Override
        public void handleOnShutdownException(Throwable throwable) {

        }
    }
}
