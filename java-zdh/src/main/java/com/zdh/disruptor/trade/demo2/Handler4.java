package com.zdh.disruptor.trade.demo2;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.zdh.disruptor.trade.demo.Trade;

public class Handler4 implements EventHandler<Trade>, WorkHandler<Trade> {
    @Override
    public void onEvent(Trade event, long l, boolean b) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(Trade event) throws Exception {
        System.out.println("handler4 : get name" + event.getName());
        event.setName(event.getName() + "h4");
    }
}
