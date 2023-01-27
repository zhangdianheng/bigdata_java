package com.zdh.desgin_mode.handler.dynamic;


/**
 * @author zdh
 * @date 2022-07-05 15:43
 * @Version 1.0
 */
public abstract class GatewayHandler {
    protected GatewayHandler next;

    public void setNext(GatewayHandler next) {
        this.next = next;
    }

    public abstract int handler();

}
