package com.zdh.disruptor.trade.demo;

import com.lmax.disruptor.*;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * @author zhangdianheng
 */
public class Main2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int BUFFER_SIZE = 1024;
        int THREAD_NUMBERS = 4;
        final RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade();
            }
        },BUFFER_SIZE,new YieldingWaitStrategy());
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(THREAD_NUMBERS,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        WorkHandler<Trade> handler = new TradeHandler();
        WorkerPool<Trade> workerPool = new WorkerPool<Trade>(ringBuffer,sequenceBarrier,new IgnoreExceptionHandler(),handler);
        workerPool.start(executorService);
        for (int i = 0; i < 10; i++) {
            long seq = ringBuffer.next();
            ringBuffer.get(seq).setPrice(Math.random()*9999);
            ringBuffer.publish(seq);
        }
        Thread.sleep(1000);
        workerPool.halt();
        executorService.shutdown();
    }

}
