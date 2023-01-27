package com.zdh.disruptor.trade.demo2;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.zdh.disruptor.trade.demo.Trade;

public class Handler2 implements EventHandler<Trade>, WorkHandler<Trade> {
    @Override
    public void onEvent(Trade event, long l, boolean b) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(Trade event) throws Exception {
        System.out.println("Hander2 : set price");
        event.setPrice(17.0);
        Thread.sleep(1000);
    }
}
