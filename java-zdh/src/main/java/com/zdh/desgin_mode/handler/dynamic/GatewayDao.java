package com.zdh.desgin_mode.handler.dynamic;

/**
 * @author zdh
 * @date 2022-07-05 15:39
 * @Version 1.0
 */
public interface GatewayDao {
    /**
     * 根据 handlerId 获取配置项
     * @param handlerId
     * @return
     */
    GatewayEntity getGatewayEntity(Integer handlerId);

    /**
     * 获取第一个处理者
     * @return
     */
    GatewayEntity getFirstGatewayEntity();
}
