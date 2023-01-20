package com.zdh.disruptor.trade.demo2;

import com.zdh.disruptor.trade.demo.Trade;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        long beginTime = System.currentTimeMillis();
        int bufferSize = 1024;
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(8,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

        Disruptor<Trade> disruptor = new Disruptor<Trade>(new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade();
            }
        },bufferSize,executorService, ProducerType.SINGLE,new BusySpinWaitStrategy());
//        //菱形操作
//        EventHandlerGroup<Trade> handler =
//                disruptor.handleEventsWith(new Handler1(),new Handler2());
//        handler.then(new Handler3());
        //顺序操作
//        disruptor.handleEventsWith(new Handler1()).handleEventsWith(new Handler2())
//                .handleEventsWith(new Handler3());

//      //六边形操作
        Handler1 h1 = new Handler1();
        Handler2 h2 = new Handler2();
        Handler3 h3 = new Handler3();
        Handler4 h4 = new Handler4();
        Handler5 h5 = new Handler5();
        disruptor.handleEventsWith(h1,h2);
        disruptor.after(h1).handleEventsWith(h4);
        disruptor.after(h2).handleEventsWith(h5);
        disruptor.after(h4,h5).handleEventsWith(h3);





        disruptor.start();
        CountDownLatch latch = new CountDownLatch(1);
        executorService.submit(new TradePublisher(latch,disruptor));
        latch.await();
        disruptor.shutdown();
        executorService.shutdown();
        System.out.println("总耗时："+(System.currentTimeMillis()-beginTime));
    }
}
