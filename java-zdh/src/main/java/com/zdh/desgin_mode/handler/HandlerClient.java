package com.zdh.desgin_mode.handler;

/**
 * @author zdh
 * @date 2022-07-05 15:29
 * @Version 1.0
 */
public class HandlerClient {
    public static void main(String[] args) {
        FirstPassHandler firstPassHandler = new FirstPassHandler();
        SecondPassHandler secondPassHandler = new SecondPassHandler();
        ThirdPassHandler thirdPassHandler = new ThirdPassHandler();
        firstPassHandler.setNext(secondPassHandler);
        secondPassHandler.setNext(thirdPassHandler);
        firstPassHandler.handler();
    }
}
