package com.zdh.disruptor;

public class LongEvent {
    private long value;

    public void set(long value)
    {
        this.value = value;
    }

    public long geValue() {
        return value;
    }

    @Override
    public String toString() {
        return "LongEvent{" +
                "value=" + value +
                '}';
    }
}