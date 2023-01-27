package com.zdh.desgin_mode.handler.dynamic;

/**
 * @author zdh
 * @date 2022-07-05 15:34
 * @Version 1.0
 */
public enum GatewayEnum {

    API_HANDLER(new GatewayEntity("api接口限流", "com.zdh.desgin_mode.handler.dynamic.ApiLimitGatewayHandler",1,  null, 2)),
    BLACKLIST_HANDLER(new GatewayEntity( "黑名单拦截", "com.zdh.desgin_mode.handler.dynamic.BlacklistGatewayHandler", 2,1, 3)),
    SESSION_HANDLER(new GatewayEntity("用户会话拦截", "com.zdh.desgin_mode.handler.dynamic.SessionGatewayHandler", 3, 2, null)),
    ;

    GatewayEntity gatewayEntity;

    GatewayEnum(GatewayEntity gatewayEntity) {
        this.gatewayEntity = gatewayEntity;
    }

    public GatewayEntity getGatewayEntity() {
        return gatewayEntity;
    }
}
