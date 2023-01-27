package com.zdh.desgin_mode.handler.dynamic;

/**
 * @author zdh
 * @date 2022-07-05 16:20
 * @Version 1.0
 */
public class GetewayClient {
    public static void main(String[] args) {
        GatewayHandler firstGatewayHandler = GatewayHandlerEnumFactory.getFirstGatewayHandler();
        firstGatewayHandler.handler();
    }
}
