package com.zdh.desgin_mode.handler;

/**
 * @author zdh
 * @date 2022-07-05 15:25
 * @Version 1.0
 */
public abstract class AbstractHandler {
    protected  AbstractHandler next;

    public void setNext(AbstractHandler next) {
        this.next = next;
    }
    public abstract int handler();
}
