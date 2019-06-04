package com.segeon.easyrpc.core.domain.entity;

/**
 * 代表Provider侧提供的一个服务。interfaceType的名称，group, version三者共同确定唯一一个服务
 */
public interface RemotingService extends ServiceKey {

    /**
     * 具体实现上面接口的实例
     * @return
     */
    Object target();
}
