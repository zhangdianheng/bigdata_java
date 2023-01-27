package com.zdh.disruptor.trade.demo2;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.zdh.disruptor.trade.demo.Trade;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhangdianheng
 */
public class TradePublisher implements Runnable {

    Disruptor<Trade> disruptor;
    private CountDownLatch latch;
    private static int LOOP = 1;

    public TradePublisher(CountDownLatch latch, Disruptor<Trade> disruptor) {
        this.disruptor=disruptor;
        this.latch=latch;
    }

    @Override
    public void run() {
        TradeEventTranslator tradeEventTranslator = new TradeEventTranslator();
        for (int i = 0; i < LOOP; i++) {
            disruptor.publishEvent(tradeEventTranslator);
            latch.countDown();
        }
    }
}

class TradeEventTranslator implements EventTranslator<Trade>{

    private Random random = new Random();
    @Override
    public void translateTo(Trade event, long l) {
        this.generateTrade(event);
    }

    private Trade generateTrade(Trade event) {
        event.setPrice(random.nextDouble()*9999);
        return event;
    }
}
