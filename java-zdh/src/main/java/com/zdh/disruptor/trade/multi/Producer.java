package com.zdh.disruptor.trade.multi;

import com.lmax.disruptor.RingBuffer;

public class Producer {
    private final RingBuffer<Order> ringBuffer;

    public Producer(RingBuffer<Order> ringBuffer) {

        this.ringBuffer = ringBuffer;
    }
    public void  onData(String data){
        long seq = ringBuffer.next();
        try {
            Order order = ringBuffer.get(seq);
            order.setId(data);
        } finally {
            ringBuffer.publish(seq);
        }

    }
}
