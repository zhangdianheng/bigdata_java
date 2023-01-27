package com.zdh.desgin_mode.handler.dynamic;

/**
 * @author zdh
 * @date 2022-07-05 16:18
 * @Version 1.0
 */
public class SessionGatewayHandler extends GatewayHandler{
    @Override
    public int handler() {
        System.out.println("用户会话拦截--->SessionGatewayHandler");
        if (GatewayEnum.SESSION_HANDLER.getGatewayEntity().getNextHandlerId() != null){
            this.next.handler();
        }
        return 0;
    }
}
