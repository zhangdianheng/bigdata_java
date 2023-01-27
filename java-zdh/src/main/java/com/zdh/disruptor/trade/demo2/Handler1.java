package com.zdh.disruptor.trade.demo2;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.zdh.disruptor.trade.demo.Trade;

public class Handler1 implements EventHandler<Trade>, WorkHandler<Trade> {
    @Override
    public void onEvent(Trade event, long l, boolean b) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(Trade event) throws Exception {
        System.out.println("Hander1 : set name ");
        event.setName("h1");
        Thread.sleep(1000);
    }
}
