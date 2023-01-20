package com.zdh.disruptor.trade.demo;

import com.lmax.disruptor.*;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * @author zhangdianheng
 */
public class Main1 {
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
        //创建消息处理器
        BatchEventProcessor<Trade> tradeProcessor = new BatchEventProcessor<Trade>(ringBuffer,sequenceBarrier,new TradeHandler());
        //这一步目的就是把消费者的位置信息引用注入到生产者，如果只有一个消费者的情况可以省略
        ringBuffer.addGatingSequences(tradeProcessor.getSequence());
        //把消息处理器提交到线程池
        executorService.submit(tradeProcessor);
        Future<?> future = executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                long seq;
                for (int i = 0; i < 10; i++) {
                    seq = ringBuffer.next();
                    ringBuffer.get(seq).setPrice(Math.random()*9999);
                    ringBuffer.publish(seq);
                }
                return null;
            }
        });
        future.get();
        Thread.sleep(1000);
        tradeProcessor.halt();
        executorService.shutdown();
    }

}
