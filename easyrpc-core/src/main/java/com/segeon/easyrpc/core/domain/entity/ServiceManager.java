package com.segeon.easyrpc.core.domain.entity;

import com.segeon.easyrpc.core.domain.exception.RegistrationException;

import java.util.Set;

/**
 * 负责管理本provider发布的服务，并负责注册本地服务到注册中心上
 */
public interface ServiceManager extends Endpoint {
    /**
     * 注册并发布一个服务到注册中心
     * @param remotingService
     */
    void register(RemotingService remotingService);

    /**
     * 将一个服务从注册中心取消注册
     * @param remotingService
     */
    void unregister(RemotingService remotingService);


    /**
     * 根据接口名，group, version三要素查找本地发布的服务。三个参数都是必传参数
     * @param interfaceName
     * @param group
     * @param version
     * @return 返回找到的匹配的服务
     * @throws RegistrationException 如果没有找到，抛出该异常
     */
    RemotingService lookup(String interfaceName, String group, String version);

}
