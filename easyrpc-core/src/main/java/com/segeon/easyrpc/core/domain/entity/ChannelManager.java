package com.segeon.easyrpc.core.domain.entity;

import java.util.List;
import java.util.Set;

/**
 * 维护consumer当前可用的Channel集合，以及某个服务对应channel的映射关系
 */
public interface ChannelManager {

    /**
     * 返回直连的provider，或者注册中心中提供的provider
     * @param referenceConfig
     * @return
     */
    List<Endpoint> resolveProviders(ReferenceConfig referenceConfig);

    void registerChannel(ReferenceConfig referenceConfig, Channel channel);

    void registerChannel(ReferenceConfig referenceConfig, List<Channel> channels);

    void removeChannel(Channel channel);

    /**
     * 查找对于某个服务的引用可用的Channel。如果目前尚未创建可用的Channel，则从注册中心获取服务provider地址，并建立Channel。
     * 注意: 该方法返回的是本地缓存的Channel，并不一定跟当前注册中心中实际可用的服务provider一致
     * @param referenceConfig
     * @return
     */
    List<Channel> availableChannels(ReferenceConfig referenceConfig);
}
