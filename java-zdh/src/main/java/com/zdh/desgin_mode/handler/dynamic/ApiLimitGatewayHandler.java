package com.zdh.desgin_mode.handler.dynamic;

/**
 * @author zdh
 * @date 2022-07-05 15:45
 * @Version 1.0
 */
public class ApiLimitGatewayHandler extends GatewayHandler{

    @Override
    public int handler() {
        System.out.println("api接口限流-->ApiLimitGatewayHandler");
        if (GatewayEnum.API_HANDLER.getGatewayEntity().getNextHandlerId() != null){
            this.next.handler();
        }
        return 0;
    }
}
