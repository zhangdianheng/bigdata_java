package com.zdh.disruptor.trade.demo;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.UUID;

public class TradeHandler implements EventHandler<Trade>, WorkHandler<Trade> {

    @Override
    public void onEvent(Trade event, long l, boolean b) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(Trade event) throws Exception {
        event.setId(UUID.randomUUID().toString());
        System.out.println(event.getId());
        System.out.println(event.toString());
    }
}
